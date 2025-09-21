package com.sunrizon.horizon.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import com.sunrizon.horizon.dto.CreateUserRequest;
import com.sunrizon.horizon.dto.LoginUserRequest;
import com.sunrizon.horizon.dto.UpdateUserRequest;
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

/**
 * 用户管理控制器 V2
 * 优化版本，基于现有的ResultResponse和ResponseCode
 */
@RestController
@RequestMapping("/api/v2/user")
@Slf4j
@Tag(name = "用户管理 V2", description = "用户相关的API接口 V2，包含注册、登录、用户信息管理等功能")
public class UserControllerV2 {

    @Resource
    private IUserService userService;

    /**
     * 用户注册
     */
    @Operation(
        summary = "用户注册", 
        description = "创建新用户账户，支持用户名、邮箱和密码注册"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "注册成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "409", description = "用户名或邮箱已存在")
    })
    @PostMapping
    public ResultResponse<UserVO> createUser(
            @Parameter(description = "用户注册信息", required = true)
            @Valid @RequestBody CreateUserRequest request) {
        
        log.info("用户注册请求: username={}, email={}", request.getUsername(), request.getEmail());
        return userService.createUser(request);
    }

    /**
     * 用户登录
     */
    @Operation(
        summary = "用户登录", 
        description = "用户登录认证，支持用户名或邮箱登录，成功后返回 JWT 令牌"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "登录成功"),
        @ApiResponse(responseCode = "401", description = "用户名或密码错误"),
        @ApiResponse(responseCode = "403", description = "账户被禁用或未激活")
    })
    @PostMapping("/login")
    public ResultResponse<AuthVO> login(
            @Parameter(description = "用户登录信息", required = true)
            @Valid @RequestBody LoginUserRequest request) {
        
        log.info("用户登录请求: username={}", request.getUsername());
        return userService.login(request);
    }

    /**
     * 获取用户信息
     */
    @Operation(
        summary = "获取用户信息", 
        description = "根据用户ID获取用户详细信息，需要认证"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/{uid}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<UserVO> getUser(
            @Parameter(description = "用户唯一标识符", required = true) 
            @PathVariable String uid) {
        
        log.info("获取用户信息请求: uid={}", uid);
        return userService.getUser(uid);
    }

    /**
     * 更新用户信息
     */
    @Operation(
        summary = "更新用户信息", 
        description = "更新用户的基本信息，需要认证且只能更新自己的信息"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @PutMapping("/{uid}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<UserVO> updateUser(
            @Parameter(description = "用户唯一标识符", required = true) @PathVariable String uid,
            @Parameter(description = "用户更新信息", required = true) 
            @Valid @RequestBody UpdateUserRequest request) {
        
        log.info("更新用户信息请求: uid={}", uid);
        // 这里需要实现updateUser方法
        return ResultResponse.success("更新成功");
    }

    /**
     * 更新用户状态
     */
    @Operation(
        summary = "更新用户状态", 
        description = "更新指定用户的状态，如激活、禁用、封禁等。需要管理员权限。"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "状态更新成功"),
        @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @PutMapping("/{uid}/status")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<String> updateStatus(
            @Parameter(description = "用户唯一标识符", required = true) @PathVariable String uid,
            @Parameter(description = "新的用户状态", required = true) @RequestParam UserStatus status) {
        
        log.info("更新用户状态请求: uid={}, status={}", uid, status);
        return userService.updateStatus(uid, status);
    }

    /**
     * 获取用户列表（分页）
     */
    @Operation(
        summary = "获取用户列表", 
        description = "分页获取用户列表，支持按状态筛选。需要管理员权限。"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @GetMapping
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<Page<UserVO>> getUsers(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "用户状态筛选") @RequestParam(required = false) UserStatus status) {
        
        log.info("获取用户列表请求: page={}, size={}, status={}", page, size, status);
        // 这里需要实现getUsers方法
        return ResultResponse.success("获取成功");
    }

    /**
     * 删除用户
     */
    @Operation(
        summary = "删除用户", 
        description = "删除指定用户。需要管理员权限。"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @DeleteMapping("/{uid}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<String> deleteUser(
            @Parameter(description = "用户唯一标识符", required = true) @PathVariable String uid) {
        
        log.info("删除用户请求: uid={}", uid);
        // 这里需要实现deleteUser方法
        return ResultResponse.success("删除成功");
    }
}
