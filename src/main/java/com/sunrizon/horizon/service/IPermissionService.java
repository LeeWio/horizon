package com.sunrizon.horizon.service;

import java.util.List;

import com.sunrizon.horizon.enums.PermissionType;
import com.sunrizon.horizon.pojo.Permission;
import com.sunrizon.horizon.utils.ResultResponse;

/**
 * IPermissionService
 *
 * <p>
 * Service interface for managing system permissions. Provides methods to
 * create, retrieve, list, and delete permissions.
 * </p>
 */
public interface IPermissionService {

  /**
   * Creates a new permission.
   *
   * @param name        unique permission name as {@link PermissionType}
   * @param description optional description
   * @return {@link ResultResponse} containing created {@link Permission}
   */
  ResultResponse<Permission> createPermission(PermissionType name, String description);

  /**
   * Retrieves a permission by its ID.
   *
   * @param pid permission ID (UUID)
   * @return {@link ResultResponse} containing {@link Permission} if found
   */
  ResultResponse<Permission> getPermission(String pid);

  /**
   * Retrieves a permission by its name.
   *
   * @param name {@link PermissionType}
   * @return {@link ResultResponse} containing {@link Permission} if found
   */
  ResultResponse<Permission> getPermissionByName(PermissionType name);

  /**
   * Lists all permissions.
   *
   * @return {@link ResultResponse} containing all {@link Permission}s
   */
  ResultResponse<List<Permission>> getPermissions();

  /**
   * Deletes a permission by its ID.
   *
   * @param pid permission ID (UUID)
   * @return {@link ResultResponse} with a success message
   */
  ResultResponse<String> deletePermission(String pid);
}
