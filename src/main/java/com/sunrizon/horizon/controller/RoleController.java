package com.sunrizon.horizon.controller;

import org.springframework.security.access.prepost.PreAuthorize;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/role")
@Tag(name = "角色管理", description = "角色相关的API接口，包括角色创建、查询、删除和分配等功能")
public class RoleController {

  @Resource
  private IRoleService roleService;

  @Operation(
      summary = "创建角色", 
      description = "创建新的系统角色。需要管理员权限。",
      security = @SecurityRequirement(name = "Bearer Authentication")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "角色创建成功"),
      @ApiResponse(responseCode = "400", description = "请求参数错误"),
      @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
      @ApiResponse(responseCode = "403", description = "权限不足"),
      @ApiResponse(responseCode = "409", description = "角色名已存在")
  })
  @PostMapping
  @PreAuthorize("hasAuthority('ROLE_CREATE')")
  public ResultResponse<RoleVO> createRole(
      @Parameter(description = "角色创建信息", required = true)
      @RequestBody CreateRoleRequest request) {
    return roleService.createRole(request);
  }

  @Operation(
      summary = "获取角色信息", 
      description = "根据角色ID获取角色详细信息。需要认证。",
      security = @SecurityRequirement(name = "Bearer Authentication")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "获取成功"),
      @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
      @ApiResponse(responseCode = "404", description = "角色不存在")
  })
  @GetMapping("/{rid}")
  public ResultResponse<RoleVO> getRole(
      @Parameter(description = "角色唯一标识符", required = true) @PathVariable String rid) {
    return roleService.getRole(rid);
  }

  @Operation(
      summary = "删除角色", 
      description = "根据角色ID删除角色。需要管理员权限。",
      security = @SecurityRequirement(name = "Bearer Authentication")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "删除成功"),
      @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
      @ApiResponse(responseCode = "403", description = "权限不足"),
      @ApiResponse(responseCode = "404", description = "角色不存在"),
      @ApiResponse(responseCode = "409", description = "角色正在使用中，无法删除")
  })
  @DeleteMapping("/{rid}")
  @PreAuthorize("hasAuthority('ROLE_DELETE')")
  public ResultResponse<String> deleteRole(
      @Parameter(description = "角色唯一标识符", required = true) @PathVariable String rid) {
    return roleService.deleteRole(rid);
  }

  @Operation(
      summary = "分配角色给用户", 
      description = "将一个或多个角色分配给指定用户。需要管理员权限。",
      security = @SecurityRequirement(name = "Bearer Authentication")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "分配成功"),
      @ApiResponse(responseCode = "400", description = "请求参数错误"),
      @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
      @ApiResponse(responseCode = "403", description = "权限不足"),
      @ApiResponse(responseCode = "404", description = "用户或角色不存在")
  })
  @PostMapping("/assign")
  @PreAuthorize("hasAuthority('ROLE_ASSIGN')")
  public ResultResponse<String> assignRoles(
      @Parameter(description = "角色分配信息", required = true)
      @Valid @RequestBody AssignRolesRequest request) {
    return roleService.assignRoles(request);
  }

}
