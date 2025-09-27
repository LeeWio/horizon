package com.sunrizon.horizon.service.impl;

import java.util.Set;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;

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

import com.sunrizon.horizon.dto.CreateUserRequest;
import com.sunrizon.horizon.dto.LoginUserRequest;
import com.sunrizon.horizon.dto.UpdateUserRequest;
import com.sunrizon.horizon.enums.ResponseCode;
import com.sunrizon.horizon.enums.UserStatus;
import com.sunrizon.horizon.pojo.Role;
import com.sunrizon.horizon.pojo.User;
import com.sunrizon.horizon.repository.RoleRepository;
import com.sunrizon.horizon.repository.UserRepository;
import com.sunrizon.horizon.service.IUserService;
import com.sunrizon.horizon.utils.JwtUtil;
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
      return ResultResponse.error("Invalid email format");
    }

    // Check email uniqueness
    if (userRepository.existsByEmail(request.getEmail())) {
      return ResultResponse.error("Email already in use");
    }

    // Check username uniqueness
    if (userRepository.existsByUsername(request.getUsername())) {
      return ResultResponse.error("Username already in use");
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
        return ResultResponse.error("Some roles do not exist");
      }
      user.setRoles(roles);
    }

    // Save user
    User savedUser = userRepository.save(user);

    // Convert to VO and return
    UserVO userVO = BeanUtil.copyProperties(savedUser, UserVO.class);
    return ResultResponse.success("User created successfully", userVO);
  }

  /**
   * Authenticate user and issue JWT token.
   *
   * @param request Login request
   * @return {@link ResultResponse} with {@link AuthVO} containing token and user
   *         info
   */
  @Override
  public ResultResponse<AuthVO> login(LoginUserRequest request) {

    // Validate email format
    if (!Validator.isEmail(request.getEmail())) {
      return ResultResponse.error("Invalid email format");
    }

    // Validate password
    if (StrUtil.isBlank(request.getPassword())) {
      return ResultResponse.error("Password cannot be empty");
    }

    // Authenticate credentials
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(), request.getPassword()));

    // Load user by email
    User user = userRepository.findUserByEmail(request.getEmail())
        .orElseThrow(() -> new UsernameNotFoundException(
            "User not found with email: " + request.getEmail()));

    // Set authentication context
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Generate JWT token
    String authorization = jwtUtil.createAuthorization(authentication);

    // Build response object
    AuthVO authVO = new AuthVO(authorization, user.getUid(),
        user.getEmail(), user.getUsername());

    return ResultResponse.success("Login successful", authVO);
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
      return ResultResponse.error("User has been deleted and cannot be updated.");
    }

    // Skip if already same status
    if (currentStatus == status) {
      return ResultResponse.success("User status is already " + status);
    }

    // Validate allowed transitions
    switch (currentStatus) {
      case PENDING:
        if (status == UserStatus.ACTIVE || status == UserStatus.BANNED) {
          user.setStatus(status);
        } else {
          return ResultResponse.error("Pending user can only be activated or banned.");
        }
        break;

      case ACTIVE:
        if (status == UserStatus.INACTIVE || status == UserStatus.BANNED) {
          user.setStatus(status);
        } else {
          return ResultResponse.error("Active user can only be set to inactive or banned.");
        }
        break;

      case INACTIVE:
        if (status == UserStatus.ACTIVE || status == UserStatus.DELETED) {
          user.setStatus(status);
        } else {
          return ResultResponse.error("Inactive user can only be activated or deleted.");
        }
        break;

      case BANNED:
        if (status == UserStatus.ACTIVE || status == UserStatus.DELETED) {
          user.setStatus(status);
        } else {
          return ResultResponse.error("Banned user can only be reactivated or deleted.");
        }
        break;

      default:
        return ResultResponse.error("Invalid user status.");
    }

    // Save changes
    userRepository.saveAndFlush(user);

    return ResultResponse.success("User status updated successfully to " + status);
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
      return ResultResponse.error("User ID cannot be empty");
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
    String currentUserId = securityContextUtil.getCurrentUserId()
        .orElseThrow(() -> new UsernameNotFoundException("Current user not found"));

    // Prevent self-deletion
    if (StrUtil.equals(user.getUid(), currentUserId)) {
      return ResultResponse.error("Cannot delete the current user");
    }

    // Prevent deletion of admin (except SUPER_ADMIN)
    if (user.getRoles() != null &&
        user.getRoles().stream().anyMatch(role -> RoleType.ADMIN.equals(role.getName()))) {
      return ResultResponse.error(ResponseCode.FORBIDDEN, "Deletion of admin accounts is not allowed");
    }

    // Prevent deletion if already soft-deleted
    if (UserStatus.DELETED.equals(user.getStatus())) {
      return ResultResponse.error("User is already marked as deleted");
    }

    // Hard delete
    userRepository.delete(user);

    return ResultResponse.success("User deleted successfully");
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

    // Map fields from request to entity
    // ⚠️ 只更新允许修改的字段，避免直接覆盖敏感信息
    if (StrUtil.isNotBlank(request.getUsername())) {
      user.setUsername(request.getUsername());
    }
    if (StrUtil.isNotBlank(request.getEmail())) {
      user.setEmail(request.getEmail());
    }
    // 可在此添加更多字段更新逻辑

    // Save changes
    userRepository.saveAndFlush(user);

    return ResultResponse.success("User updated successfully");
  }

}
