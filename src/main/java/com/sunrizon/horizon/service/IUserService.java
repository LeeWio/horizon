package com.sunrizon.horizon.service;

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
  // * Lists all users, optionally filtered by status.
  // *
  // * @param status the {@link UserStatus} to filter by (null to ignore
  // * filtering)
  // * @return {@link ResultResponse} containing a list of {@link UserVO}
  // */
  // ResultResponse<List<UserVO>> getUsers(UserStatus status);

  /**
   * Retrieves a user by their unique ID.
   *
   * This method fetches the user's information from the database using the
   * provided
   * unique identifier. If the user exists, their data is wrapped in a
   * {@link UserVO}
   * and returned inside a {@link ResultResponse}. If the user does not exist, an
   * appropriate error message is returned.
   *
   * @param uid the unique identifier of the user
   * @return {@link ResultResponse} containing the {@link UserVO} if found,
   *         or an error message if not found
   */
  ResultResponse<UserVO> getUser(String uid);

  /**
   * Updates the status of an existing user.
   *
   * <p>
   * This method allows the service layer to change the status of a user account.
   * For example, a user in {@link UserStatus#PENDING} can be activated or banned,
   * and a {@link UserStatus#ACTIVE} user can be set to
   * {@link UserStatus#INACTIVE}
   * or {@link UserStatus#BANNED}. All status transitions are validated to prevent
   * illegal changes.
   * </p>
   *
   * @param uid    the unique identifier of the user whose status is being updated
   * @param status the new {@link UserStatus} to apply to the user
   * @return a {@link ResultResponse} containing a message indicating the outcome
   *         of the operation (success or error)
   */
  ResultResponse<String> updateStatus(String uid, UserStatus status);

}
