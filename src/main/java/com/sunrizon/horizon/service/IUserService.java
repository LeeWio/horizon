package com.sunrizon.horizon.service;

import java.util.List;

import com.sunrizon.horizon.dto.*;
import com.sunrizon.horizon.enums.UserStatus;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.AuthVO;
import com.sunrizon.horizon.vo.UserVO;

public interface IUserService {

  /**
   * Creates a new user in the system.
   *
   * Accepts a {@link CreateUserRequest} DTO containing necessary user
   * information.
   * Returns the newly created user data wrapped in a {@link ResultResponse} for
   * API consistency.
   *
   * @param request DTO containing user creation information
   * @return {@link ResultResponse} containing the created {@link UserVO}
   */
  ResultResponse<UserVO> createUser(CreateUserRequest request);

  /**
   * Authenticates a user based on credentials.
   *
   * Accepts a {@link LoginUserRequest} DTO containing the user's credentials
   * Returns a {@link AuthVO} wrapped in a {@link ResultResponse} on successful
   * authentication
   *
   * @param request DTO containing login credentials
   * @return {@link ResultResponse} containing a {@link AuthVO} if authentication
   *         succeeds
   */
  ResultResponse<AuthVO> login(LoginUserRequest request);

  // /**
  // * Updates an existing user's information.
  // *
  // * Accepts an {@link UpdateUserRequest} DTO containing the updated user
  // * fields.
  // * Returns the updated user data wrapped in a {@link ResultResponse}.
  // *
  // * @param request DTO containing updated user information
  // * @return {@link ResultResponse} containing the updated {@link UserVO}
  // */
  // ResultResponse<UserVO> updateUser(UpdateUserRequest request);
  //
  // /**
  // * Deletes a user by their ID (logical deletion recommended).
  // *
  // * @param uid the unique ID of the user to delete
  // * @return {@link ResultResponse} indicating success or failure
  // */
  // ResultResponse<Void> deleteUser(String uid);
  //
  // /**
  // * Retrieves a user by their unique ID.
  // *
  // * @param uid the user's ID
  // * @return {@link ResultResponse} containing the {@link UserVO} if found
  // */
  // ResultResponse<UserVO> findById(String uid);
  //
  // /**
  // * Retrieves a user by their email.
  // *
  // * @param email the user's email address
  // * @return {@link ResultResponse} containing the {@link UserVO} if found
  // */
  // ResultResponse<UserVO> findByEmail(String email);
  //
  // /**
  // * Lists all users, optionally filtered by status.
  // *
  // * @param status the {@link UserStatus} to filter by (null to ignore
  // * filtering)
  // * @return {@link ResultResponse} containing a list of {@link UserVO}
  // */
  // ResultResponse<List<UserVO>> listUsers(UserStatus status);
  //
  // /**
  // * Updates the status of a user.
  // *
  // * @param uid the user's ID
  // * @param status the new {@link UserStatus} to set
  // * @return {@link ResultResponse} indicating success or failure
  // */
  // ResultResponse<Void> updateStatus(String uid, UserStatus status);
  //
  // /**
  // * Assigns roles to a user.
  // *
  // * Accepts an {@link AssignRolesRequest} DTO containing the user ID and role
  // * IDs
  // * to assign.
  // * Returns a {@link ResultResponse} indicating the success of the operation.
  // *
  // * @param request DTO containing user ID and role IDs
  // * @return {@link ResultResponse} indicating success or failure
  // */
  // ResultResponse<Void> assignRoles(AssignRolesRequest request);
}
