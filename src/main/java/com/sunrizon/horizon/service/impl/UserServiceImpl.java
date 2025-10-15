package com.sunrizon.horizon.service.impl;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sunrizon.horizon.constants.RabbitContants;
import com.sunrizon.horizon.constants.RedisContants;
import com.sunrizon.horizon.dto.CreateUserRequest;
import com.sunrizon.horizon.dto.LoginUserRequest;
import com.sunrizon.horizon.dto.UpdateUserRequest;
import com.sunrizon.horizon.enums.ResponseCode;
import com.sunrizon.horizon.enums.UserStatus;
import com.sunrizon.horizon.messaging.UserAuditMessage;
import com.sunrizon.horizon.pojo.Role;
import com.sunrizon.horizon.pojo.User;
import com.sunrizon.horizon.repository.RoleRepository;
import com.sunrizon.horizon.repository.UserRepository;
import com.sunrizon.horizon.service.IUserService;
import com.sunrizon.horizon.utils.JwtUtil;
import com.sunrizon.horizon.utils.RedisUtil;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.utils.SecurityContextUtil;
import com.sunrizon.horizon.vo.AuthVO;
import com.sunrizon.horizon.vo.UserVO;

import com.sunrizon.horizon.enums.RoleType;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of IUserService.
 * <p>
 * Handles user management operations including creation, update, deletion,
 * role assignment, and status changes.
 */
@Service
@Slf4j
public class UserServiceImpl implements IUserService {

  @Resource
  private RabbitTemplate rabbitTemplate;

  @Resource
  private UserRepository userRepository;

  @Resource
  private RoleRepository roleRepository;

  @Resource
  private PasswordEncoder passwordEncoder;

  @Resource
  private AuthenticationManager authenticationManager;

  @Resource
  private JwtUtil jwtUtil;

  @Resource
  private SecurityContextUtil securityContextUtil;

  @Resource
  private RedisUtil redisUtil;

  /**
   * Sends an OTP (One-Time Password) to the specified email address.
   * Validates the email format and checks if the user exists before sending
   * the OTP.
   * 
   * @param email the email address to send the OTP to
   * @return ResultResponse containing success or error message
   */
  @Override
  public ResultResponse<String> sendOtp(String email) {

    if (!Validator.isEmail(email)) {
      return ResultResponse.error(ResponseCode.USER_EMAIL_INVAILD);
    }

    if (!userRepository.existsByEmail(email)) {
      return ResultResponse.error(ResponseCode.USER_NOT_FOUND);
    }

    rabbitTemplate.convertAndSend(RabbitContants.OTP_VERIFICATION_EXCHANGE, RabbitContants.OTP_VERIFICATION_ROUTING_KEY,
        email);

    return ResultResponse.success(ResponseCode.SUCCESS);
  }

  /**
   * Create a new user.
   *
   * Validates uniqueness, encodes password, assigns roles, and saves user.
   *
   * @param request User creation request
   * @return {@link ResultResponse} with created {@link UserVO}
   */
  @Override
  @Transactional
  public ResultResponse<UserVO> createUser(CreateUserRequest request) {
    // Validate email format
    if (!Validator.isEmail(request.getEmail())) {
      return ResultResponse.error(ResponseCode.USER_EMAIL_INVAILD);
    }

    // Check email uniqueness
    if (userRepository.existsByEmail(request.getEmail())) {
      return ResultResponse.error(ResponseCode.USER_EMAIL_EXISTS);
    }

    // Check username uniqueness
    if (userRepository.existsByUsername(request.getUsername())) {
      return ResultResponse.error(ResponseCode.USER_ALREADY_EXISTS);
    }

    // Map DTO to Entity
    User user = BeanUtil.copyProperties(request, User.class);

    // Encode password
    user.setPassword(passwordEncoder.encode(request.getPassword()));

    // Assign roles
    if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
      Set<Role> roles = roleRepository.findAllById(request.getRoleIds())
          .stream().collect(Collectors.toSet());

      if (roles.size() != request.getRoleIds().size()) {
        return ResultResponse.error(ResponseCode.ROLE_IDS_NOT_FOUND);
      }
      user.setRoles(roles);
    }

    // Save user
    User savedUser = userRepository.save(user);

    // Send audit notification if user status is PENDING
    if (savedUser.getStatus() == UserStatus.PENDING) {
      try {
        UserAuditMessage auditMessage = new UserAuditMessage(
            savedUser.getUid(),
            savedUser.getUsername(),
            savedUser.getEmail(),
            savedUser.getCreatedAt()
        );
        rabbitTemplate.convertAndSend(
            RabbitContants.USER_AUDIT_EXCHANGE,
            RabbitContants.USER_AUDIT_ROUTING_KEY,
            auditMessage
        );
        log.info("User audit notification sent for user: {}", savedUser.getUid());
      } catch (Exception e) {
        log.error("Failed to send user audit notification for user: {}", savedUser.getUid(), e);
        // Don't fail user registration if notification fails
      }
    }

    // Convert to VO and return
    UserVO userVO = BeanUtil.copyProperties(savedUser, UserVO.class);
    return ResultResponse.success(ResponseCode.USER_CREATED, userVO);
  }

  /**
   * Verifies the OTP (One-Time Password) provided by the user.
   * Checks if the OTP matches the one stored in Redis for the given email.
   *
   * @param email the email address associated with the OTP
   * @param otp   the OTP code to verify
   * @return ResultResponse containing true if OTP is valid, false otherwise
   * 
   */
  @Override
  public ResultResponse<Boolean> verifyOtp(String email, String otp) {

    if (!Validator.isEmail(email)) {
      return ResultResponse.error(ResponseCode.USER_EMAIL_INVAILD, false);
    }

    if (StrUtil.isBlank(otp)) {
      return ResultResponse.error(ResponseCode.INVALID_OTP, false);
    }

    String redisKey = String.format(RedisContants.OTP_KEY_FORMAT, email);

    Optional<String> storedOtpOpt = redisUtil.get(redisKey, String.class);
    if (storedOtpOpt.isEmpty()) {
      // OTP not found in Redis (may have expired)
      return ResultResponse.error(ResponseCode.OTP_EXPIRED, false);
    }

    String storedOtp = storedOtpOpt.get();
    if (StrUtil.equals(storedOtp, otp)) {
      redisUtil.delete(redisKey);
      return ResultResponse.success(ResponseCode.OTP_VERIFIED, true);
    } else {
      return ResultResponse.error(ResponseCode.INVALID_OTP, false);
    }
  }

  /*
   * Authenticate user and issue JWT token.
   *
   * @param request Login request
   * 
   * @return {@link ResultResponse} with {@link AuthVO} containing token and user
   * info
   */
  @Override
  public ResultResponse<AuthVO> login(LoginUserRequest request) {

    // Validate email format
    if (!Validator.isEmail(request.getEmail())) {
      return ResultResponse.error(ResponseCode.USER_EMAIL_INVAILD);
    }

    // Validate password
    if (StrUtil.isBlank(request.getPassword())) {
      return ResultResponse.error(ResponseCode.USER_PASSWORD_REQUIRED);
    }

    // Authenticate credentials
    Authentication authentication;
    authentication = authenticationManager.authenticate(

        new UsernamePasswordAuthenticationToken(
            request.getEmail(), request.getPassword()));
    log.error("enter 2");

    // Load user by email
    User user = userRepository.findUserByEmail(request.getEmail())
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));

    // Set authentication context
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Generate JWT token
    String authorization = jwtUtil.createAuthorization(authentication);

    // Build response object
    AuthVO authVO = new AuthVO(authorization, user.getUid(),
        user.getEmail(), user.getUsername());

    return ResultResponse.success(ResponseCode.LOGIN_SUCCESS, authVO);
  }

  /**
   * Update user account status with strict state transitions.
   *
   * @param uid    User ID
   * @param status New {@link UserStatus}
   * @return {@link ResultResponse} indicating success or failure
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public ResultResponse<String> updateStatus(String uid, UserStatus status) {

    // Load user by ID
    User user = userRepository.findById(uid)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with uid: " + uid));

    UserStatus currentStatus = user.getStatus();

    // Deleted users cannot be updated
    if (currentStatus == UserStatus.DELETED) {
      return ResultResponse.error(ResponseCode.USER_HAS_BEEN_DELETED_CANNOT_UPDATE);
    }

    // Skip if already same status
    if (currentStatus == status) {
      return ResultResponse.success(ResponseCode.USER_STATUS_ALREADY, "User status is already " + status);
    }

    // Validate allowed transitions
    Optional<String> error = currentStatus.transitionTo(status);
    if (error.isPresent()) {
      return ResultResponse.error(ResponseCode.FAILURE, error.get());
    }
    user.setStatus(status);

    // Save changes
    userRepository.saveAndFlush(user);

    return ResultResponse.success(ResponseCode.USER_STATUS_UPDATED_SUCCESSFULLY,
        "User status updated successfully to " + status);
  }

  /**
   * Get user by ID.
   *
   * @param uid User ID
   * @return {@link ResultResponse} with {@link UserVO} or error
   */
  @Override
  public ResultResponse<UserVO> getUser(String uid) {

    // Validate input
    if (StrUtil.isBlank(uid)) {
      return ResultResponse.error(ResponseCode.USER_ID_CANNOT_BE_EMPTY);
    }

    // Load user by ID
    User user = userRepository.findById(uid)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with uid: " + uid));

    // Map entity to VO
    UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);

    // Return response
    return ResultResponse.success(userVO);
  }

  /**
   * Delete user by ID.
   *
   * @param uid User ID
   * @return {@link ResultResponse} with success or error message
   */
  @Override
  public ResultResponse<String> deleteUser(String uid) {

    // Validate input
    if (StrUtil.isBlank(uid)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "User ID is required");
    }

    // Find user
    User user = userRepository.findById(uid)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + uid));

    // Get current logged-in user ID
    Optional<String> optionalCurrentUserId = securityContextUtil.getCurrentUserId();
    if (optionalCurrentUserId.isEmpty()) {
      return ResultResponse.error(ResponseCode.CURRENT_USER_NOT_FOUND);
    }
    String currentUserId = optionalCurrentUserId.get();

    // Prevent self-deletion
    if (StrUtil.equals(user.getUid(), currentUserId)) {
      return ResultResponse.error(ResponseCode.CANNOT_DELETE_CURRENT_USER);
    }

    // Prevent deletion of admin (except SUPER_ADMIN)
    if (user.getRoles() != null &&
        user.getRoles().stream().anyMatch(role -> RoleType.ADMIN.equals(role.getName()))) {
      return ResultResponse.error(ResponseCode.FORBIDDEN, "Deletion of admin accounts is not allowed");
    }

    // Prevent deletion if already soft-deleted
    if (UserStatus.DELETED.equals(user.getStatus())) {
      return ResultResponse.error(ResponseCode.USER_ALREADY_MARKED_DELETED);
    }

    // Hard delete
    userRepository.delete(user);

    return ResultResponse.success(ResponseCode.USER_DELETED_SUCCESSFULLY);
  }

  /**
   * Get paginated list of users.
   *
   * @param pageable Pagination and sorting info
   * @return {@link ResultResponse} with paginated {@link UserVO} list
   */
  @Override
  public ResultResponse<Page<UserVO>> getUsers(Pageable pageable) {
    // Fetch paginated users
    Page<User> userPage = userRepository.findAll(pageable);

    // Map entity to VO
    Page<UserVO> voPage = userPage.map(user -> BeanUtil.copyProperties(user, UserVO.class));

    // Return response
    return ResultResponse.success(voPage);
  }

  /**
   * Get all users (non-paginated).
   *
   * @return {@link ResultResponse} with list of all {@link UserVO}
   */
  @Override
  public ResultResponse<java.util.List<UserVO>> getAllUsers() {
    // Fetch all users
    java.util.List<User> users = userRepository.findAll();

    // Map entity to VO list
    java.util.List<UserVO> voList = users.stream()
        .map(user -> cn.hutool.core.bean.BeanUtil.copyProperties(user, UserVO.class))
        .collect(java.util.stream.Collectors.toList());

    // Return response
    return ResultResponse.success(voList);
  }

  /**
   * TODO: 待实现
   * Update user details.
   *
   * @param uid     User ID
   * @param request Update request
   * @return {@link ResultResponse} indicating success or failure
   */
  @Override
  public ResultResponse<String> updateUser(String uid, UpdateUserRequest request) {

    // Validate input
    if (StrUtil.isBlank(uid)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "User ID is required");
    }

    // Find user
    User user = userRepository.findById(uid)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + uid));

    // Check if username already exists
    if (StrUtil.isNotBlank(request.getUsername()) && !request.getUsername().equals(user.getUsername())) {
      if (userRepository.existsByUsername(request.getUsername())) {
        return ResultResponse.error(ResponseCode.USERNAME_ALREADY_EXISTS);
      }
      user.setUsername(request.getUsername());
    }

    // Check if email already exists
    if (StrUtil.isNotBlank(request.getEmail()) && !request.getEmail().equals(user.getEmail())) {
      if (userRepository.existsByEmail(request.getEmail())) {
        return ResultResponse.error(ResponseCode.EMAIL_ALREADY_EXISTS);
      }
      user.setEmail(request.getEmail());
    }

    // Update avatar
    if (StrUtil.isNotBlank(request.getAvatar())) {
      user.setAvatar(request.getAvatar());
    }

    // Save changes
    userRepository.saveAndFlush(user);

    return ResultResponse.success(ResponseCode.PROFILE_UPDATED);
  }

  /**
   * Change user password.
   *
   * @param uid         unique user ID
   * @param oldPassword old password
   * @param newPassword new password
   * @return ResultResponse with success or error message
   */
  @Override
  public ResultResponse<String> changePassword(String uid, String oldPassword, String newPassword) {

    // Validate input
    if (StrUtil.isBlank(uid)) {
      return ResultResponse.error(ResponseCode.USER_ID_CANNOT_BE_EMPTY);
    }

    if (StrUtil.isBlank(oldPassword) || StrUtil.isBlank(newPassword)) {
      return ResultResponse.error(ResponseCode.USER_PASSWORD_REQUIRED);
    }

    // Find user
    User user = userRepository.findById(uid)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + uid));

    // Verify old password
    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
      return ResultResponse.error(ResponseCode.OLD_PASSWORD_INCORRECT);
    }

    // Validate new password strength (minimum 8 characters, contains letter and number)
    if (newPassword.length() < 8 || !newPassword.matches(".*[a-zA-Z].*") || !newPassword.matches(".*\\d.*")) {
      return ResultResponse.error(ResponseCode.PASSWORD_TOO_WEAK);
    }

    // Encode and save new password
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.saveAndFlush(user);

    return ResultResponse.success(ResponseCode.PASSWORD_UPDATED);
  }

  /**
   * Send password reset email.
   *
   * @param email user's email address
   * @return ResultResponse with success or error message
   */
  @Override
  public ResultResponse<String> sendPasswordResetEmail(String email) {

    // Validate email format
    if (!Validator.isEmail(email)) {
      return ResultResponse.error(ResponseCode.USER_EMAIL_INVAILD);
    }

    // Check if user exists
    if (!userRepository.existsByEmail(email)) {
      return ResultResponse.error(ResponseCode.USER_NOT_FOUND);
    }

    // Send OTP via RabbitMQ (reuse existing OTP mechanism)
    rabbitTemplate.convertAndSend(
        RabbitContants.OTP_VERIFICATION_EXCHANGE,
        RabbitContants.OTP_VERIFICATION_ROUTING_KEY,
        email);

    return ResultResponse.success(ResponseCode.PASSWORD_RESET_EMAIL_SENT);
  }

  /**
   * Reset password using OTP.
   *
   * @param email       user's email address
   * @param otp         OTP code
   * @param newPassword new password
   * @return ResultResponse with success or error message
   */
  @Override
  public ResultResponse<String> resetPassword(String email, String otp, String newPassword) {

    // Validate email format
    if (!Validator.isEmail(email)) {
      return ResultResponse.error(ResponseCode.USER_EMAIL_INVAILD);
    }

    // Validate inputs
    if (StrUtil.isBlank(otp) || StrUtil.isBlank(newPassword)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "OTP and new password are required");
    }

    // Verify OTP
    String redisKey = String.format(RedisContants.OTP_KEY_FORMAT, email);
    Optional<String> storedOtpOpt = redisUtil.get(redisKey, String.class);

    if (storedOtpOpt.isEmpty()) {
      return ResultResponse.error(ResponseCode.RESET_TOKEN_EXPIRED);
    }

    String storedOtp = storedOtpOpt.get();
    if (!StrUtil.equals(storedOtp, otp)) {
      return ResultResponse.error(ResponseCode.RESET_TOKEN_INVALID);
    }

    // Validate password strength
    if (newPassword.length() < 8 || !newPassword.matches(".*[a-zA-Z].*") || !newPassword.matches(".*\\d.*")) {
      return ResultResponse.error(ResponseCode.PASSWORD_TOO_WEAK);
    }

    // Find user and reset password
    User user = userRepository.findUserByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.saveAndFlush(user);

    // Delete used OTP
    redisUtil.delete(redisKey);

    return ResultResponse.success(ResponseCode.PASSWORD_RESET_SUCCESS);
  }

  /**
   * Audit a pending user (approve or reject).
   * Allows admin to change user status from PENDING to ACTIVE (approve) or BANNED (reject).
   *
   * @param userId user ID to audit
   * @param status target status (ACTIVE to approve, BANNED to reject)
   * @param reason optional rejection reason
   * @return ResultResponse with success or error message
   */
  @Override
  @Transactional
  public ResultResponse<String> auditUser(String userId, UserStatus status, String reason) {
    // Validate inputs
    if (StrUtil.isBlank(userId)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "User ID is required");
    }

    if (status == null) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "Status is required");
    }

    // Only ACTIVE and BANNED are valid audit results
    if (status != UserStatus.ACTIVE && status != UserStatus.BANNED) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "Audit status must be ACTIVE or BANNED");
    }

    // Find user
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

    // Check if user is in PENDING status
    if (user.getStatus() != UserStatus.PENDING) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, 
          String.format("User is not pending audit. Current status: %s", user.getStatus()));
    }

    // Update user status
    user.setStatus(status);
    userRepository.saveAndFlush(user);

    // Log audit action
    String action = (status == UserStatus.ACTIVE) ? "approved" : "rejected";
    log.info("User {} has been {} by admin. Reason: {}", userId, action, 
        StrUtil.isNotBlank(reason) ? reason : "N/A");

    // TODO: Send notification to user via email
    // emailService.sendUserAuditResult(user.getEmail(), status, reason);

    String message = String.format("User %s successfully", action);
    return ResultResponse.success(ResponseCode.SUCCESS, message);
  }

  /**
   * Get all pending users waiting for audit.
   *
   * @param pageable pagination info
   * @return ResultResponse containing page of pending users
   */
  @Override
  public ResultResponse<Page<UserVO>> getPendingUsers(Pageable pageable) {
    Page<User> userPage = userRepository.findByStatus(UserStatus.PENDING, pageable);
    Page<UserVO> userVOPage = userPage.map(user -> BeanUtil.copyProperties(user, UserVO.class));
    return ResultResponse.success(ResponseCode.SUCCESS, userVOPage);
  }

}
