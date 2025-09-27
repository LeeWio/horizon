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

import com.sunrizon.horizon.dto.CreateUserRequest;
import com.sunrizon.horizon.dto.LoginUserRequest;
import com.sunrizon.horizon.enums.UserStatus;
import com.sunrizon.horizon.repository.UserRepository;
import com.sunrizon.horizon.service.IUserService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.AuthVO;
import com.sunrizon.horizon.vo.UserVO;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {

  @Resource
  private IUserService userService;

  /**
   * Create a new user.
   *
   * @param request DTO containing user creation data
   * @return ResultResponse wrapping the created UserVO
   */
  @PostMapping
  // @PreAuthorize("hasAuthority('USER_CREATE')")
  public ResultResponse<UserVO> createUser(@Valid @RequestBody CreateUserRequest request) {
    return userService.createUser(request);
  }

  /**
   * Authenticate a user and return a JWT token.
   *
   * @param request DTO containing login credentials (email, password)
   * @return ResultResponse wrapping AuthVO containing JWT token and user info
   */
  @PostMapping("/login")
  public ResultResponse<AuthVO> login(@Valid @RequestBody LoginUserRequest request) {
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

  @GetMapping
  public ResultResponse<Page<UserVO>> getUsers(Pageable pageable) {
    return userService.getUsers(pageable);
  }

}
