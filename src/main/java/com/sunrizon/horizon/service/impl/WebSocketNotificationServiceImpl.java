package com.sunrizon.horizon.service.impl;

import com.sunrizon.horizon.dto.WebSocketNotificationMessage;
import com.sunrizon.horizon.service.IWebSocketNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * WebSocket Notification Service Implementation
 * 
 * WebSocket实时通知服务实现
 */
@Service
@Slf4j
public class WebSocketNotificationServiceImpl implements IWebSocketNotificationService {

  @Resource
  private SimpMessagingTemplate messagingTemplate;

  @Resource
  private SimpUserRegistry userRegistry;

  /**
   * 向指定用户发送通知
   * 
   * 目的地：/user/{userId}/queue/notifications
   * 用户需要订阅：/user/queue/notifications
   */
  @Override
  public void sendToUser(String userId, WebSocketNotificationMessage message) {
    try {
      log.info("Sending WebSocket notification to user: {}, type: {}", userId, message.getType());
      
      // 发送到用户专属队列
      messagingTemplate.convertAndSendToUser(
          userId,
          "/queue/notifications",
          message
      );
      
      log.debug("WebSocket notification sent successfully to user: {}", userId);
    } catch (Exception e) {
      log.error("Failed to send WebSocket notification to user: {}", userId, e);
    }
  }

  /**
   * 向所有在线用户广播通知
   * 
   * 目的地：/topic/notifications
   * 用户需要订阅：/topic/notifications
   */
  @Override
  public void broadcastToAll(WebSocketNotificationMessage message) {
    try {
      log.info("Broadcasting WebSocket notification to all users, type: {}", message.getType());
      
      // 广播到所有订阅者
      messagingTemplate.convertAndSend("/topic/notifications", message);
      
      log.debug("WebSocket notification broadcasted successfully");
    } catch (Exception e) {
      log.error("Failed to broadcast WebSocket notification", e);
    }
  }

  /**
   * 向指定角色的用户发送通知
   * 
   * 目的地：/topic/role/{role}
   * 用户需要订阅：/topic/role/{role}
   */
  @Override
  public void sendToRole(String role, WebSocketNotificationMessage message) {
    try {
      log.info("Sending WebSocket notification to role: {}, type: {}", role, message.getType());
      
      // 发送到角色主题
      messagingTemplate.convertAndSend("/topic/role/" + role, message);
      
      log.debug("WebSocket notification sent successfully to role: {}", role);
    } catch (Exception e) {
      log.error("Failed to send WebSocket notification to role: {}", role, e);
    }
  }

  /**
   * 检查用户是否在线
   * 
   * 通过检查用户是否有活跃的WebSocket会话来判断
   */
  @Override
  public boolean isUserOnline(String userId) {
    if (userId == null) {
      return false;
    }
    
    try {
      return userRegistry.getUser(userId) != null;
    } catch (Exception e) {
      log.error("Failed to check if user is online: {}", userId, e);
      return false;
    }
  }

  /**
   * 获取在线用户数量
   */
  @Override
  public int getOnlineUserCount() {
    try {
      return userRegistry.getUserCount();
    } catch (Exception e) {
      log.error("Failed to get online user count", e);
      return 0;
    }
  }
}
