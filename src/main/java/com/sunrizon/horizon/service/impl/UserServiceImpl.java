package com.sunrizon.horizon.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import cn.hutool.core.*;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.Validator;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sunrizon.horizon.dto.AssignRolesRequest;
import com.sunrizon.horizon.dto.CreateUserRequest;
import com.sunrizon.horizon.dto.UpdateUserRequest;
import com.sunrizon.horizon.enums.UserStatus;
import com.sunrizon.horizon.pojo.Role;
import com.sunrizon.horizon.pojo.User;
import com.sunrizon.horizon.repository.RoleRepository;
import com.sunrizon.horizon.repository.UserRepository;
import com.sunrizon.horizon.service.IUserService;
import com.sunrizon.horizon.utils.ResultResponse;
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
}
