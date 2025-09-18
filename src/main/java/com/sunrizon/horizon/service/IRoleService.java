
package com.sunrizon.horizon.service;

import com.sunrizon.horizon.dto.AssignRolesRequest;
import com.sunrizon.horizon.dto.CreateRoleRequest;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.RoleVO;

/**
 * IRoleService
 *
 * <p>
 * Service interface for managing system roles.
 * Provides methods to create and retrieve roles.
 * </p>
 */
public interface IRoleService {

  /**
   * Creates a new role in the system.
   *
   * @param request {@link CreateRoleRequest} containing role name and description
   * @return {@link ResultResponse} containing the created {@link RoleVO} or error
   *         information
   */
  ResultResponse<RoleVO> createRole(CreateRoleRequest request);

  /**
   * Retrieves a role by its unique ID.
   *
   * @param rid Role ID (UUID)
   * @return {@link ResultResponse} containing role details as {@link RoleVO} or
   *         error message
   */
  ResultResponse<RoleVO> getRole(String rid);

  /**
   * Deletes a role by its unique ID.
   *
   * @param rid Role ID (UUID)
   * @return {@link ResultResponse} containing a success message or error
   *         information after deletion
   */
  ResultResponse<String> deleteRole(String rid);

  /**
   * Assigns one or more roles to a specified user.
   *
   * @param request {@link AssignRolesRequest} containing the user ID and a set of
   *                role IDs to assign
   * @return {@link ResultResponse} containing a success message or error
   *         information
   */
  ResultResponse<String> assignRoles(AssignRolesRequest request);

}
