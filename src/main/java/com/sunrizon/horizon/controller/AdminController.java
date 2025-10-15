package com.sunrizon.horizon.controller;

import com.sunrizon.horizon.dto.AuditUserRequest;
import com.sunrizon.horizon.service.IUserService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin management controller
 * <p>
 * Provides admin-only endpoints for user management, content moderation, and system administration.
 */
@RestController
@RequestMapping("/api/admin")
@Slf4j
@Tag(name = "Admin Management", description = "Admin-only APIs for system management")
public class AdminController {

  @Resource
  private IUserService userService;

  /**
   * Get all pending users waiting for audit
   *
   * @param pageable pagination parameters
   * @return Page of pending users
   */
  @GetMapping("/users/pending")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Get pending users", description = "Retrieve all users with PENDING status waiting for admin approval")
  public ResultResponse<Page<UserVO>> getPendingUsers(Pageable pageable) {
    log.info("Admin requesting pending users list");
    return userService.getPendingUsers(pageable);
  }

  /**
   * Audit a user (approve or reject)
   *
   * @param request audit request containing user ID, status, and optional reason
   * @return Success or error message
   */
  @PostMapping("/users/audit")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Audit user", description = "Approve (ACTIVE) or reject (BANNED) a pending user registration")
  public ResultResponse<String> auditUser(@Valid @RequestBody AuditUserRequest request) {
    log.info("Admin auditing user: {} with status: {}", request.getUserId(), request.getStatus());
    return userService.auditUser(request.getUserId(), request.getStatus(), request.getReason());
  }
}
