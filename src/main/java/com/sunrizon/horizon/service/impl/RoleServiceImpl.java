package com.sunrizon.horizon.service.impl;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sunrizon.horizon.dto.AssignRolesRequest;
import com.sunrizon.horizon.dto.CreateRoleRequest;
import com.sunrizon.horizon.pojo.Role;
import com.sunrizon.horizon.pojo.User;
import com.sunrizon.horizon.repository.RoleRepository;
import com.sunrizon.horizon.repository.UserRepository;
import com.sunrizon.horizon.service.IRoleService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.RoleVO;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link IRoleService}.
 * <p>
 * Provides business logic for creating, retrieving, and deleting roles.
 */
@Service
@Slf4j
public class RoleServiceImpl implements IRoleService {

  @Resource
  private RoleRepository roleRepository;

  @Resource
  private UserRepository userRepository;

  /**
   * Creates a new role in the system.
   *
   * @param request {@link CreateRoleRequest} containing role name and description
   * @return {@link ResultResponse} with created {@link RoleVO} or error message
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public ResultResponse<RoleVO> createRole(CreateRoleRequest request) {
    // Validate input
    if (StrUtil.isBlank(request.getName())) {
      return ResultResponse.error("Role name cannot be empty.");
    }

    // Convert DTO to Entity
    Role role = BeanUtil.copyProperties(request, Role.class);

    // Save to DB
    Role savedRole = roleRepository.saveAndFlush(role);

    // Convert Entity to VO for response
    RoleVO roleVO = BeanUtil.copyProperties(savedRole, RoleVO.class);

    return ResultResponse.success(roleVO);
  }

  /**
   * Retrieves a role by its unique ID.
   *
   * @param rid Role ID (UUID)
   * @return {@link ResultResponse} with role details as {@link RoleVO} or error
   *         message
   */
  @Override
  public ResultResponse<RoleVO> getRole(String rid) {
    // Validate input
    if (StrUtil.isBlank(rid)) {
      return ResultResponse.error("Role ID cannot be empty");
    }

    // Retrieve role from DB
    Role role = roleRepository.findById(rid)
        .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + rid));

    // Convert Entity to VO
    RoleVO roleVO = BeanUtil.copyProperties(role, RoleVO.class);

    return ResultResponse.success(roleVO);
  }

  /**
   * Deletes a role by its unique ID.
   *
   * @param rid Role ID (UUID)
   * @return {@link ResultResponse} with success message or error information
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public ResultResponse<String> deleteRole(String rid) {
    // Validate input
    if (StrUtil.isBlank(rid)) {
      return ResultResponse.error("Role ID cannot be empty");
    }

    // Find the role
    Role role = roleRepository.findById(rid)
        .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + rid));

    // Delete role
    roleRepository.delete(role);

    return ResultResponse.success("Role deleted successfully");
  }

  @Override
  @Transactional
  public ResultResponse<String> assignRoles(AssignRolesRequest request) {

    if (StrUtil.isBlank(request.getUid())) {
      return ResultResponse.error("User ID must be provided");
    }

    if (CollUtil.isEmpty(request.getRoleIds())) {
      return ResultResponse.error("Role IDs must be provided");
    }

    User user = userRepository.findById(request.getUid())
        .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUid()));

    Set<Role> roles = roleRepository.findAllById(request.getRoleIds()).stream().collect(Collectors.toSet());

    if (!NumberUtil.equals(roles.size(), request.getRoleIds().size())) {
      return ResultResponse.error("Some roles do not exist");
    }

    user.setRoles(roles);

    userRepository.saveAndFlush(user);

    return ResultResponse.success("Roles assigned successfully");
  }

}
