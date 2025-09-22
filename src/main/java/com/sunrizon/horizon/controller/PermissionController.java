package com.sunrizon.horizon.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunrizon.horizon.enums.PermissionType;
import com.sunrizon.horizon.pojo.Permission;
import com.sunrizon.horizon.service.IPermissionService;
import com.sunrizon.horizon.utils.ResultResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;

@RestController
@RequestMapping("/api/permission")
@Tag(name = "权限管理", description = "权限相关的API接口，包括创建、查询、列表和删除")
public class PermissionController {

  @Resource
  private IPermissionService permissionService;

  public static class CreatePermissionRequest {
    public PermissionType name;
    public String description;
  }

  @Operation(summary = "创建权限", description = "创建新的权限。需要管理员权限。", security = @SecurityRequirement(name = "Bearer Authentication"))
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "创建成功"),
      @ApiResponse(responseCode = "400", description = "请求参数错误"),
      @ApiResponse(responseCode = "409", description = "权限已存在")
  })
  @PostMapping
  @PreAuthorize("hasAuthority('ROLE_CREATE') or hasAuthority('USER_ASSIGN_ROLE')")
  public ResultResponse<Permission> create(@RequestBody CreatePermissionRequest request) {
    return permissionService.createPermission(request.name, request.description);
  }

  @Operation(summary = "获取权限详情", description = "根据ID获取权限详情。", security = @SecurityRequirement(name = "Bearer Authentication"))
  @GetMapping("/{pid}")
  public ResultResponse<Permission> get(@PathVariable String pid) {
    return permissionService.getPermission(pid);
  }

  @Operation(summary = "根据名称获取权限", description = "根据权限枚举名称获取权限。", security = @SecurityRequirement(name = "Bearer Authentication"))
  @GetMapping("/name/{name}")
  public ResultResponse<Permission> getByName(@PathVariable PermissionType name) {
    return permissionService.getPermissionByName(name);
  }

  @Operation(summary = "权限列表", description = "获取所有权限列表。", security = @SecurityRequirement(name = "Bearer Authentication"))
  @GetMapping
  public ResultResponse<java.util.List<Permission>> list() {
    return permissionService.getPermissions();
  }

  @Operation(summary = "删除权限", description = "根据ID删除权限。需要管理员权限。", security = @SecurityRequirement(name = "Bearer Authentication"))
  @DeleteMapping("/{pid}")
  @PreAuthorize("hasAuthority('ROLE_DELETE')")
  public ResultResponse<String> delete(@PathVariable String pid) {
    return permissionService.deletePermission(pid);
  }
}
