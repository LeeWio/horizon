package com.sunrizon.horizon.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sunrizon.horizon.dto.*;
import com.sunrizon.horizon.enums.UserStatus;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.AuthVO;
import com.sunrizon.horizon.vo.UserVO;

public interface IUserService {

  /**
   * Create a new user.
   *
   * @param request DTO with user creation info
   * @return ResultResponse containing the created UserVO
   */
  ResultResponse<UserVO> createUser(CreateUserRequest request);

  /**
   * Get a user by ID.
   *
   * @param uid unique user ID
   * @return ResultResponse containing the UserVO if found
   */
  ResultResponse<UserVO> getUser(String uid);

  /**
   * Get a paginated list of users.
   *
   * @param pageable pagination info (page number, size, sort)
   * @return ResultResponse containing a page of UserVO
   */
  ResultResponse<Page<UserVO>> getUsers(Pageable pageable);

  /**
   * Delete a user by ID.
   *
   * @param uid unique user ID
   * @return ResultResponse with success or error message
   */
  ResultResponse<String> deleteUser(String uid);

  /**
   * Update a user's information.
   *
   * @param uid     unique user ID
   * @param request DTO with updated user fields
   * @return ResultResponse with success or error message
   */
  ResultResponse<String> updateUser(String uid, UpdateUserRequest request);

  /**
   * Authenticate a user.
   *
   * @param request DTO with login credentials
   * @return ResultResponse containing AuthVO if login succeeds
   */
  ResultResponse<AuthVO> login(LoginUserRequest request);

  /**
   * Update a user's status.
   *
   * @param uid    unique user ID
   * @param status new UserStatus
   * @return ResultResponse with success or error message
   */
  ResultResponse<String> updateStatus(String uid, UserStatus status);

}
