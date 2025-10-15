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
   * Send OTP (One-Time Password) to the specified email address.
   * Generates and sends a verification code to the user's email for authentication purposes.
   *
   * @param email the email address to send the OTP to
   * @return ResultResponse containing success message if OTP was sent successfully, error message otherwise
   */
  ResultResponse<String> sendOtp(String email);

  /**
   * Verify the OTP (One-Time Password) provided by the user.
   * Validates the OTP code against the one stored in the system for the given email.
   *
   * @param email the email address associated with the OTP
   * @param otp the OTP code to verify
   * @return ResultResponse containing true if OTP is valid, false otherwise
   */
  ResultResponse<Boolean> verifyOtp(String email, String otp);

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

  /**
   * Get all users (non-paginated).
   *
   * @return ResultResponse containing a list of all UserVO
   */
  ResultResponse<java.util.List<UserVO>> getAllUsers();

  /**
   * Change user password.
   *
   * @param uid         unique user ID
   * @param oldPassword old password
   * @param newPassword new password
   * @return ResultResponse with success or error message
   */
  ResultResponse<String> changePassword(String uid, String oldPassword, String newPassword);

  /**
   * Send password reset email.
   *
   * @param email user's email address
   * @return ResultResponse with success or error message
   */
  ResultResponse<String> sendPasswordResetEmail(String email);

  /**
   * Reset password using OTP.
   *
   * @param email       user's email address
   * @param otp         OTP code
   * @param newPassword new password
   * @return ResultResponse with success or error message
   */
  ResultResponse<String> resetPassword(String email, String otp, String newPassword);

}
