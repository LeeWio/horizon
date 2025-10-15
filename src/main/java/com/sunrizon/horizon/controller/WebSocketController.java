package com.sunrizon.horizon.controller;

import com.sunrizon.horizon.service.IWebSocketNotificationService;
import com.sunrizon.horizon.utils.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * WebSocket Controller
 * 
 * WebSocket连接管理和测试接口
 */
@RestController
@RequestMapping("/api/websocket")
@Tag(name = "WebSocket Management", description = "WebSocket连接管理和测试")
public class WebSocketController {

  @Resource
  private IWebSocketNotificationService webSocketNotificationService;

  /**
   * 检查用户是否在线
   */
  @GetMapping("/online/{userId}")
  @Operation(summary = "检查用户是否在线", description = "检查指定用户是否有活跃的WebSocket连接")
  public ResultResponse<Boolean> checkUserOnline(@PathVariable String userId) {
    boolean online = webSocketNotificationService.isUserOnline(userId);
    return ResultResponse.success(online);
  }

  /**
   * 获取在线用户数量
   */
  @GetMapping("/online/count")
  @Operation(summary = "获取在线用户数量", description = "获取当前在线用户总数")
  public ResultResponse<Integer> getOnlineUserCount() {
    int count = webSocketNotificationService.getOnlineUserCount();
    return ResultResponse.success(count);
  }
}
