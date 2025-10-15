package com.sunrizon.horizon.service;

import com.sunrizon.horizon.dto.WebSocketNotificationMessage;

/**
 * WebSocket Notification Service Interface
 * 
 * WebSocket实时通知服务接口
 */
public interface IWebSocketNotificationService {

  /**
   * 向指定用户发送通知
   *
   * @param userId  接收用户ID
   * @param message 通知消息
   */
  void sendToUser(String userId, WebSocketNotificationMessage message);

  /**
   * 向所有在线用户广播通知
   *
   * @param message 通知消息
   */
  void broadcastToAll(WebSocketNotificationMessage message);

  /**
   * 向指定角色的用户发送通知
   *
   * @param role    用户角色
   * @param message 通知消息
   */
  void sendToRole(String role, WebSocketNotificationMessage message);

  /**
   * 检查用户是否在线
   *
   * @param userId 用户ID
   * @return 是否在线
   */
  boolean isUserOnline(String userId);

  /**
   * 获取在线用户数量
   *
   * @return 在线用户数量
   */
  int getOnlineUserCount();
}
