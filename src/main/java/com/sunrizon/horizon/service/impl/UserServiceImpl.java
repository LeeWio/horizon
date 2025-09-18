package com.sunrizon.horizon.service.impl;

import java.util.Set;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;

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
import com.sunrizon.horizon.enums.UserStatus;
import com.sunrizon.horizon.pojo.Role;
import com.sunrizon.horizon.pojo.User;
import com.sunrizon.horizon.repository.RoleRepository;
import com.sunrizon.horizon.repository.UserRepository;
import com.sunrizon.horizon.service.IUserService;
import com.sunrizon.horizon.utils.JwtUtil;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.AuthVO;
import com.sunrizon.horizon.vo.UserVO;

import jakarta.annotation.Resource;

/**
 * Implementation of IUserService.
 * <p>
 * Handles user management operations including creation, update, deletion,
 * role assignment, and status changes.
 */
@Service
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

  /**
   * Creates a new user in the system.
   *
   * Validates email format, uniqueness of email and username.
   * Sets default status to PENDING.
   * Assigns roles if provided and validates their existence.
   *
   * @param request DTO containing user creation info
   * @return {@link ResultResponse} containing the created {@link UserVO}
   */
  @Override
  @Transactional
  public ResultResponse<UserVO> createUser(CreateUserRequest request) {

    // 1. Validate email format
    if (!Validator.isEmail(request.getEmail())) {
      return ResultResponse.error("Invalid email format");
    }

    // 2. Check if email already exists
    if (userRepository.existsByEmail(request.getEmail())) {
      return ResultResponse.error("Email already in use");
    }

    // 3. Check if username already exists
    if (userRepository.existsByUsername(request.getUsername())) {
      return ResultResponse.error("Username already in use");
    }

    // 4. Map DTO -> Entity safely
    User user = BeanUtil.copyProperties(request, User.class);

    // 5. Set default status
    user.setStatus(UserStatus.PENDING);

    // 6. Encode password
    user.setPassword(passwordEncoder.encode(request.getPassword()));

    // 7. Assign roles if provided
    if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
      Set<Role> roles = roleRepository.findAllById(request.getRoleIds())
          .stream().collect(Collectors.toSet());

      if (roles.size() != request.getRoleIds().size()) {
        return ResultResponse.error("Some roles do not exist");
      }

      user.setRoles(roles);
    }

    // 8. Save user
    User savedUser = userRepository.save(user);

    // 9. Convert to VO
    UserVO userVO = BeanUtil.copyProperties(savedUser, UserVO.class);

    // 10. Return success response
    return ResultResponse.success("User created successfully", userVO);
  }

  /**
   * Authenticates a user and generates an authorization token.
   *
   * Validates the email format and credentials. Retrieves the user's record from
   * the database, checks status, and issues a JWT authorization token on
   * successful authentication.
   *
   * @param request DTO containing the user's login credentials
   * @return {@link ResultResponse} containing an {@link AuthVO} with the
   *         authorization token and user info
   */
  @Override
  public ResultResponse<AuthVO> login(LoginUserRequest request) {

    if (!Validator.isEmail(request.getEmail())) {
      return ResultResponse.error("Invalid email format");
    }

    if (StrUtil.isBlank(request.getPassword())) {
      return ResultResponse.error("Password cannot be empty");
    }

    Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
        request.getEmail(), request.getPassword()));

    User user = userRepository.findUserByEmail(request.getEmail())
        .orElseThrow(() -> new UsernameNotFoundException(
            "User not found with email: " + request.getEmail()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    String authorization = jwtUtil.createAuthorization(authentication);

    AuthVO authVO = new AuthVO(authorization, user.getUid(), user.getEmail(),
        user.getUsername());

    return ResultResponse.success("Login successful", authVO);
  }

  /**
   * Updates the status of a user account.
   *
   * <p>
   * This method enforces strict state transition rules to prevent illegal
   * status changes. For example:
   * <ul>
   * <li>PENDING → can only become ACTIVE or BANNED</li>
   * <li>ACTIVE → can only become INACTIVE or BANNED</li>
   * <li>INACTIVE → can only become ACTIVE or DELETED</li>
   * <li>BANNED → can only become ACTIVE or DELETED</li>
   * <li>DELETED → cannot be changed anymore</li>
   * </ul>
   * </p>
   *
   * <p>
   * If the status change is valid, the user entity will be updated in the
   * database. Otherwise, an error response is returned.
   * </p>
   *
   * <p>
   * This method is transactional, and will roll back on any {@link Exception}.
   * </p>
   *
   * @param uid    Unique ID of the user to update
   * @param status The new {@link UserStatus} to set
   * @return {@link ResultResponse} containing success or error message
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public ResultResponse<String> updateStatus(String uid, UserStatus status) {

    User user = userRepository.findById(uid)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with uid: " + uid));

    UserStatus userStatus = user.getStatus();

    if (userStatus == UserStatus.DELETED) {
      return ResultResponse.error("User has been deleted and cannot be updated.");
    }

    if (userStatus == status) {
      return ResultResponse.success("User status is already " + status);
    }

    switch (userStatus) {
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

    userRepository.saveAndFlush(user);

    return ResultResponse.success("User status updated successfully to " + status);
  }

  /**
   * Retrieves a user by their unique ID.
   *
   * <p>
   * Fetches the user from the database using {@link UserRepository#findById}.
   * If the user exists, maps to {@link UserVO} and returns in
   * {@link ResultResponse}.
   * Otherwise, throws {@link UsernameNotFoundException}.
   * </p>
   *
   * @param uid Unique identifier of the user
   * @return {@link ResultResponse} containing {@link UserVO} or error
   */
  @Override
  public ResultResponse<UserVO> getUser(String uid) {

    if (StrUtil.isBlank(uid)) {
      return ResultResponse.error("User ID cannot be empty");
    }

    User user = userRepository.findById(uid)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with uid: " + uid));

    UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);

    return ResultResponse.success(userVO);
  }

}
