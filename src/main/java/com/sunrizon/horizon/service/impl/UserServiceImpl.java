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
import com.sunrizon.horizon.enums.ResponseCode;
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
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "邮箱格式不正确");
    }

    // 2. Check if email already exists
    if (userRepository.existsByEmail(request.getEmail())) {
      return ResultResponse.error(ResponseCode.USER_EMAIL_EXISTS, "邮箱已存在");
    }

    // 3. Check if username already exists
    if (userRepository.existsByUsername(request.getUsername())) {
      return ResultResponse.error(ResponseCode.USER_USERNAME_EXISTS, "用户名已存在");
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
        return ResultResponse.error(ResponseCode.ROLE_NOT_FOUND, "部分角色不存在");
      }

      user.setRoles(roles);
    }

    // 8. Save user
    User savedUser = userRepository.save(user);

    // 9. Convert to VO
    UserVO userVO = BeanUtil.copyProperties(savedUser, UserVO.class);

    // 10. Return success response
    return ResultResponse.success("用户创建成功", userVO);
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

    if (StrUtil.isBlank(request.getUsername())) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "用户名或邮箱不能为空");
    }

    if (StrUtil.isBlank(request.getPassword())) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "密码不能为空");
    }

    Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
        request.getUsername(), request.getPassword()));

    // 根据输入是邮箱还是用户名来查找用户
    User user;
    if (Validator.isEmail(request.getUsername())) {
      user = userRepository.findUserByEmail(request.getUsername())
          .orElseThrow(() -> new UsernameNotFoundException(
              "User not found with email: " + request.getUsername()));
    } else {
      user = userRepository.findUserByUsername(request.getUsername())
          .orElseThrow(() -> new UsernameNotFoundException(
              "User not found with username: " + request.getUsername()));
    }

    SecurityContextHolder.getContext().setAuthentication(authentication);

    String authorization = jwtUtil.createAuthorization(authentication);

    AuthVO authVO = new AuthVO(authorization, user.getUid(), user.getEmail(),
        user.getUsername());

    return ResultResponse.success("登录成功", authVO);
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
      return ResultResponse.error(ResponseCode.USER_INVALID_STATUS, "用户已被删除，无法更新状态");
    }

    if (userStatus == status) {
      return ResultResponse.success("用户状态已经是 " + status);
    }

    switch (userStatus) {
      case PENDING:
        if (status == UserStatus.ACTIVE || status == UserStatus.BANNED) {
          user.setStatus(status);
        } else {
          return ResultResponse.error(ResponseCode.USER_INVALID_STATUS, "待激活用户只能被激活或封禁");
        }
        break;

      case ACTIVE:
        if (status == UserStatus.INACTIVE || status == UserStatus.BANNED) {
          user.setStatus(status);
        } else {
          return ResultResponse.error(ResponseCode.USER_INVALID_STATUS, "激活用户只能被设置为未激活或封禁");
        }
        break;

      case INACTIVE:
        if (status == UserStatus.ACTIVE || status == UserStatus.DELETED) {
          user.setStatus(status);
        } else {
          return ResultResponse.error(ResponseCode.USER_INVALID_STATUS, "未激活用户只能被激活或删除");
        }
        break;

      case BANNED:
        if (status == UserStatus.ACTIVE || status == UserStatus.DELETED) {
          user.setStatus(status);
        } else {
          return ResultResponse.error(ResponseCode.USER_INVALID_STATUS, "封禁用户只能被重新激活或删除");
        }
        break;

      default:
        return ResultResponse.error(ResponseCode.USER_INVALID_STATUS, "无效的用户状态");
    }

    userRepository.saveAndFlush(user);

    return ResultResponse.success("用户状态更新成功为 " + status);
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
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "用户ID不能为空");
    }

    User user = userRepository.findById(uid)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with uid: " + uid));

    UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);

    return ResultResponse.success(userVO);
  }

}
