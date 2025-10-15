package com.sunrizon.horizon.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunrizon.horizon.dto.ChangePasswordRequest;
import com.sunrizon.horizon.dto.CreateUserRequest;
import com.sunrizon.horizon.dto.LoginUserRequest;
import com.sunrizon.horizon.dto.ResetPasswordRequest;
import com.sunrizon.horizon.dto.UpdateUserRequest;
import com.sunrizon.horizon.enums.UserStatus;
import com.sunrizon.horizon.service.IUserService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.utils.SecurityContextUtil;
import com.sunrizon.horizon.vo.AuthVO;
import com.sunrizon.horizon.vo.UserVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user")
@Slf4j
@Tag(name = "User Management", description = "User management APIs")
public class UserController {

  @Resource
  private IUserService userService;

  @Resource
  private SecurityContextUtil securityContextUtil;

  /**
   * Create a new user.
   *
   * @param request DTO containing user creation data
   * @return ResultResponse wrapping the created UserVO
   */
  @PostMapping("/create")
  // @PreAuthorize("hasAuthority('USER_CREATE')")
  public ResultResponse<UserVO> createUser(@Valid @RequestBody CreateUserRequest request) {
    return userService.createUser(request);
  }

  /**
   * Send OTP to user's email for verification.
   *
   * @param email the email address to send OTP to
   * @return ResultResponse with success or error message
   */
  @PostMapping("/send-otp")
  public ResultResponse<String> sendOtp(@RequestParam String email) {
    return userService.sendOtp(email);
  }

  /**
   * Verify OTP sent to user's email.
   *
   * @param email the email address associated with the OTP
   * @param otp   the OTP code to verify
   * @return ResultResponse containing verification result (true if valid, false
   *         otherwise)
   */
  @PostMapping("/verify-otp")
  public ResultResponse<Boolean> verifyOtp(@RequestParam String email, @RequestParam String otp) {
    return userService.verifyOtp(email, otp);
  }

  /**
   * Authenticate a user and return a JWT token.
   * 
   * @param request DTO containing login credentials (email, password)
   * @return ResultResponse wrapping AuthVO containing JWT token and user info
   */
  @PostMapping("/authenticate")
  public ResultResponse<AuthVO> authenticateUser(@Valid @RequestBody LoginUserRequest request) {
    return userService.login(request);
  }

  /**
   * Update the status of an existing user.
   *
   * @param uid    Unique identifier of the user
   * @param status New status to apply (ACTIVE, INACTIVE, BANNED, etc.)
   * @return ResultResponse containing success or error message
   */
  @PutMapping("/{uid}/status")
  // @PreAuthorize("hasAuthority('USER_UPDATE_STATUS')")
  public ResultResponse<String> updateStatus(@PathVariable String uid,
      @RequestParam UserStatus status) {
    return userService.updateStatus(uid, status);
  }

  /**
   * Retrieve a user by their unique ID.
   *
   * @param uid Unique identifier of the user
   * @return ResultResponse wrapping UserVO if found, or error if not found
   */
  @GetMapping("/{uid}")
  public ResultResponse<UserVO> getUser(@PathVariable("uid") String uid) {
    return userService.getUser(uid);
  }

  @DeleteMapping("/{uid}")
  public ResultResponse<String> deleteUser(@PathVariable("uid") String uid) {
    return userService.deleteUser(uid);
  }

  /**
   * Retrieve all users (non-paginated).
   * 
   * @return ResultResponse wrapping list of all UserVO
   */
  @GetMapping("/all")
  public ResultResponse<java.util.List<UserVO>> getAllUsers() {
    return userService.getAllUsers();
  }

  @GetMapping
  @Operation(summary = "Get users with pagination")
  public ResultResponse<Page<UserVO>> getUsers(Pageable pageable) {
    return userService.getUsers(pageable);
  }

  /**
   * Get current user profile.
   *
   * @return ResultResponse wrapping current user's UserVO
   */
  @GetMapping("/profile")
  @Operation(summary = "Get current user profile")
  public ResultResponse<UserVO> getCurrentUserProfile() {
    String uid = securityContextUtil.getCurrentUserId().orElse(null);
    return userService.getUser(uid);
  }

  /**
   * Update current user profile.
   *
   * @param request DTO containing profile update data
   * @return ResultResponse with success or error message
   */
  @PutMapping("/profile")
  @Operation(summary = "Update current user profile")
  public ResultResponse<String> updateProfile(@Valid @RequestBody UpdateUserRequest request) {
    String uid = securityContextUtil.getCurrentUserId().orElse(null);
    return userService.updateUser(uid, request);
  }

  /**
   * Change current user password.
   *
   * @param request DTO containing old and new passwords
   * @return ResultResponse with success or error message
   */
  @PutMapping("/password")
  @Operation(summary = "Change current user password")
  public ResultResponse<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
    String uid = securityContextUtil.getCurrentUserId().orElse(null);
    return userService.changePassword(uid, request.getOldPassword(), request.getNewPassword());
  }

  /**
   * Send password reset email.
   *
   * @param email user's email address
   * @return ResultResponse with success or error message
   */
  @PostMapping("/forgot-password")
  @Operation(summary = "Send password reset email")
  public ResultResponse<String> forgotPassword(@RequestParam String email) {
    return userService.sendPasswordResetEmail(email);
  }

  /**
   * Reset password using OTP.
   *
   * @param request DTO containing email, OTP, and new password
   * @return ResultResponse with success or error message
   */
  @PostMapping("/reset-password")
  @Operation(summary = "Reset password using OTP")
  public ResultResponse<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
    return userService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
  }

}
