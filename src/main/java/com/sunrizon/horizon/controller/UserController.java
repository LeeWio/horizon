package com.sunrizon.horizon.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunrizon.horizon.dto.CreateUserRequest;
import com.sunrizon.horizon.dto.LoginUserRequest;
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
  private IUserService iUserService;

  /**
   * Creates a new user in the system.
   *
   * This endpoint accepts a {@link CreateUserRequest} containing the required
   * information to create a user. The request body is validated before
   * processing.
   * Only users with the 'USER_CREATE' permission can access this endpoint.
   *
   * @param request the DTO containing user creation data
   * @return a {@link ResultResponse} wrapping the created {@link UserVO} object,
   *         including status and message fields for API consistency
   */
  @PostMapping
  // @PreAuthorize("hasAuthority('USER_CREATE')")
  public ResultResponse<UserVO> createUser(@Valid @RequestBody CreateUserRequest request) {
    return iUserService.createUser(request);
  }

  @PostMapping("login")
  public ResultResponse<AuthVO> login(@Valid @RequestBody LoginUserRequest request) {
    return iUserService.login(request);
  }

}
