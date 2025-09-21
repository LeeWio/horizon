package com.sunrizon.horizon.controller;

import org.springframework.security.access.prepost.PreAuthorize;
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
import com.sunrizon.horizon.service.IUserService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.AuthVO;
import com.sunrizon.horizon.vo.UserVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user")
@Slf4j
@Tag(name = "用户管理", description = "用户相关的API接口，包括注册、登录、用户信息管理等功能")
public class UserController {

  @Resource
  private IUserService userService;

  @Operation(summary = "用户注册", description = "创建新用户账户")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "注册成功"),
      @ApiResponse(responseCode = "400", description = "请求参数错误"),
      @ApiResponse(responseCode = "409", description = "用户名或邮箱已存在")
  })
  @PostMapping
  @PreAuthorize("hasAuthority('USER_CREATE')")
  public ResultResponse<UserVO> createUser(
      @Parameter(description = "用户注册信息", required = true) @Valid @RequestBody CreateUserRequest request) {
    return userService.createUser(request);
  }

  @Operation(summary = "用户登录", description = "用户登录认证，成功后返回 JWT 令牌")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "登录成功"),
      @ApiResponse(responseCode = "401", description = "用户名或密码错误"),
      @ApiResponse(responseCode = "403", description = "账户被禁用或未激活")
  })
  @PostMapping("/login")
  public ResultResponse<AuthVO> login(
      @Parameter(description = "用户登录信息", required = true) @Valid @RequestBody LoginUserRequest request) {
    return userService.login(request);
  }

  @Operation(summary = "更新用户状态", description = "更新指定用户的状态，如激活、禁用、封禁等。需要管理员权限。", security = @SecurityRequirement(name = "Bearer Authentication"))
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "状态更新成功"),
      @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
      @ApiResponse(responseCode = "403", description = "权限不足"),
      @ApiResponse(responseCode = "404", description = "用户不存在")
  })
  @PutMapping("/{uid}/status")
  @PreAuthorize("hasAuthority('USER_UPDATE_STATUS')")
  public ResultResponse<String> updateStatus(
      @Parameter(description = "用户唯一标识符", required = true) @PathVariable String uid,
      @Parameter(description = "新的用户状态", required = true) @RequestParam UserStatus status) {
    return userService.updateStatus(uid, status);
  }

  @Operation(summary = "获取用户信息", description = "根据用户ID获取用户详细信息。需要认证。", security = @SecurityRequirement(name = "Bearer Authentication"))
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "获取成功"),
      @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
      @ApiResponse(responseCode = "404", description = "用户不存在")
  })
  @GetMapping("/{uid}")
  public ResultResponse<UserVO> getUser(
      @Parameter(description = "用户唯一标识符", required = true) @PathVariable("uid") String uid) {
    return userService.getUser(uid);
  }

}
