package com.sunrizon.horizon.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunrizon.horizon.dto.CreatePermissionRequest;
import com.sunrizon.horizon.service.IPermissionService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.PermissionVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/permission")
@Tag(name = "Permission Management", description = "APIs for managing permissions")
@Slf4j
public class PermissionController {

  @Resource
  private IPermissionService permissionService;

  /**
   * Creates a new permission.
   *
   * @param request DTO containing permission creation information
   * @return ResultResponse containing the created PermissionVO
   */
  @PostMapping
  @Operation(summary = "Create a new permission", description = "Creates a new permission with the provided details.")
  public ResultResponse<PermissionVO> createPermission(@Valid @RequestBody CreatePermissionRequest request) {
    return permissionService.createPermission(request);
  }

  /**
   * Retrieves a permission by its ID.
   *
   * @param pid the unique identifier of the permission
   * @return ResultResponse containing the requested PermissionVO
   */
  @GetMapping("/{pid}")
  @Operation(summary = "Get permission by ID", description = "Retrieves the permission details for the specified permission ID.")
  public ResultResponse<PermissionVO> getPermission(@PathVariable String pid) {
    return permissionService.getPermission(pid);
  }

  /**
   * Deletes a permission by its ID.
   *
   * @param pid the unique identifier of the permission to delete
   * @return ResultResponse containing a success message or error code
   */
  @DeleteMapping("/{pid}")
  @Operation(summary = "Delete permission by ID", description = "Deletes the permission with the specified ID. Returns a success message if deletion is successful.")
  public ResultResponse<String> deletePermission(@PathVariable String pid) {
    return permissionService.deletePermission(pid);
  }

  @GetMapping
  @Operation(summary = "Get paginated permissions", description = "Returns a paginated list of permissions.")
  public ResultResponse<Page<PermissionVO>> getPermissions(Pageable pageable) {
    return permissionService.getPermissions(pageable);
  }

  // 赋予权限给 role
  @PostMapping("/assign")
  public ResultResponse<String> assignPermissions(@Valid @RequestBody String request) {
    return null;
  }

}
