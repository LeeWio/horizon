package com.sunrizon.horizon.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunrizon.horizon.dto.AssignRolesRequest;
import com.sunrizon.horizon.dto.CreateRoleRequest;
import com.sunrizon.horizon.service.IRoleService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.RoleVO;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/role")
public class RoleController {

  @Resource
  private IRoleService roleService;

  @PostMapping
  public ResultResponse<RoleVO> createRole(@RequestBody CreateRoleRequest request) {
    return roleService.createRole(request);
  }

  /**
   * Get a role by ID.
   *
   * @param rid Role ID
   * @return {@link ResultResponse} containing the role details as a string or
   *         {@link RoleVO}
   */
  @GetMapping("/{rid}")
  public ResultResponse<RoleVO> getRole(@PathVariable String rid) {
    return roleService.getRole(rid);
  }

  /**
   * Delete a role by ID.
   *
   * @param rid Role ID
   * @return {@link ResultResponse} success or error message
   */
  @DeleteMapping("/{rid}")
  public ResultResponse<String> deleteRole(@PathVariable String rid) {
    return roleService.deleteRole(rid);
  }

  /**
   * Assigns one or more roles to a user.
   *
   * @param request {@link AssignRolesRequest} containing user ID and role IDs
   * @return {@link ResultResponse} containing success message or error info
   */
  @PostMapping("/assign")
  public ResultResponse<String> assignRoles(@Valid @RequestBody AssignRolesRequest request) {
    return roleService.assignRoles(request);
  }

}
