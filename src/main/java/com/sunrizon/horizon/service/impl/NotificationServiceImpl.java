package com.sunrizon.horizon.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.sunrizon.horizon.dto.WebSocketNotificationMessage;
import com.sunrizon.horizon.enums.NotificationType;
import com.sunrizon.horizon.enums.ResponseCode;
import com.sunrizon.horizon.pojo.Notification;
import com.sunrizon.horizon.pojo.User;
import com.sunrizon.horizon.repository.NotificationRepository;
import com.sunrizon.horizon.repository.UserRepository;
import com.sunrizon.horizon.service.INotificationService;
import com.sunrizon.horizon.service.IWebSocketNotificationService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.NotificationVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Notification service implementation
 */
@Service
@Slf4j
public class NotificationServiceImpl implements INotificationService {

  @Resource
  private NotificationRepository notificationRepository;

  @Resource
  private UserRepository userRepository;

  @Resource
  private IWebSocketNotificationService webSocketNotificationService;

  @Override
  @Transactional
  public ResultResponse<NotificationVO> createNotification(String userId, NotificationType type, String title,
      String content, String relatedId, String senderId) {

    // Validate inputs
    if (StrUtil.isBlank(userId) || type == null) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    // Create notification
    Notification notification = new Notification();
    notification.setUserId(userId);
    notification.setType(type);
    notification.setTitle(title);
    notification.setContent(content);
    notification.setRelatedId(relatedId);
    notification.setSenderId(senderId);
    notification.setIsRead(false);

    Notification saved = notificationRepository.save(notification);

    // Convert to VO
    NotificationVO vo = convertToVO(saved);

    // WebSocket实时推送
    try {
      if (webSocketNotificationService.isUserOnline(userId)) {
        // 用户在线，推送实时通知
        WebSocketNotificationMessage wsMessage = convertToWebSocketMessage(vo);
        
        // 获取未读通知数量
        Long unreadCount = notificationRepository.countByUserIdAndIsRead(userId, false);
        wsMessage.setUnreadCount(unreadCount);
        
        webSocketNotificationService.sendToUser(userId, wsMessage);
        log.info("WebSocket notification sent to online user: {}", userId);
      } else {
        log.debug("User is offline, notification saved to database: {}", userId);
      }
    } catch (Exception e) {
      // WebSocket推送失败不影响通知创建
      log.warn("Failed to send WebSocket notification to user: {}", userId, e);
    }

    return ResultResponse.success(ResponseCode.NOTIFICATION_CREATED, vo);
  }

  @Override
  public ResultResponse<Page<NotificationVO>> getNotifications(String userId, Pageable pageable) {
    if (StrUtil.isBlank(userId)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    Page<Notification> notificationPage = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    Page<NotificationVO> voPage = notificationPage.map(this::convertToVO);

    return ResultResponse.success(voPage);
  }

  @Override
  public ResultResponse<Page<NotificationVO>> getUnreadNotifications(String userId, Pageable pageable) {
    if (StrUtil.isBlank(userId)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    Page<Notification> notificationPage = notificationRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(userId,
        false, pageable);
    Page<NotificationVO> voPage = notificationPage.map(this::convertToVO);

    return ResultResponse.success(voPage);
  }

  @Override
  public ResultResponse<Page<NotificationVO>> getNotificationsByType(String userId, NotificationType type,
      Pageable pageable) {
    if (StrUtil.isBlank(userId) || type == null) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    Page<Notification> notificationPage = notificationRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, type,
        pageable);
    Page<NotificationVO> voPage = notificationPage.map(this::convertToVO);

    return ResultResponse.success(voPage);
  }

  @Override
  public ResultResponse<Long> getUnreadCount(String userId) {
    if (StrUtil.isBlank(userId)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, 0L);
    }

    Long count = notificationRepository.countByUserIdAndIsRead(userId, false);
    return ResultResponse.success(count);
  }

  @Override
  @Transactional
  public ResultResponse<String> markAsRead(String userId, String nid) {
    if (StrUtil.isBlank(userId) || StrUtil.isBlank(nid)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    Optional<Notification> notificationOpt = notificationRepository.findById(nid);
    if (notificationOpt.isEmpty()) {
      return ResultResponse.error(ResponseCode.NOTIFICATION_NOT_FOUND);
    }

    Notification notification = notificationOpt.get();

    // Verify ownership
    if (!notification.getUserId().equals(userId)) {
      return ResultResponse.error(ResponseCode.FORBIDDEN);
    }

    notification.setIsRead(true);
    notificationRepository.save(notification);

    return ResultResponse.success(ResponseCode.NOTIFICATION_MARKED_READ);
  }

  @Override
  @Transactional
  public ResultResponse<String> markAllAsRead(String userId) {
    if (StrUtil.isBlank(userId)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    notificationRepository.markAllAsRead(userId);
    return ResultResponse.success(ResponseCode.ALL_NOTIFICATIONS_MARKED_READ);
  }

  @Override
  @Transactional
  public ResultResponse<String> deleteNotification(String userId, String nid) {
    if (StrUtil.isBlank(userId) || StrUtil.isBlank(nid)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    Optional<Notification> notificationOpt = notificationRepository.findById(nid);
    if (notificationOpt.isEmpty()) {
      return ResultResponse.error(ResponseCode.NOTIFICATION_NOT_FOUND);
    }

    Notification notification = notificationOpt.get();

    // Verify ownership
    if (!notification.getUserId().equals(userId)) {
      return ResultResponse.error(ResponseCode.FORBIDDEN);
    }

    notificationRepository.delete(notification);
    return ResultResponse.success(ResponseCode.NOTIFICATION_DELETED);
  }

  @Override
  @Transactional
  public ResultResponse<String> deleteReadNotifications(String userId) {
    if (StrUtil.isBlank(userId)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    notificationRepository.deleteReadNotifications(userId);
    return ResultResponse.success(ResponseCode.ALL_NOTIFICATIONS_DELETED);
  }

  @Override
  @Transactional
  public ResultResponse<String> deleteAllNotifications(String userId) {
    if (StrUtil.isBlank(userId)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    notificationRepository.deleteByUserId(userId);
    return ResultResponse.success(ResponseCode.ALL_NOTIFICATIONS_DELETED);
  }

  /**
   * Convert Notification entity to VO
   */
  private NotificationVO convertToVO(Notification notification) {
    NotificationVO vo = BeanUtil.copyProperties(notification, NotificationVO.class);

    // Populate sender information if available
    if (StrUtil.isNotBlank(notification.getSenderId())) {
      Optional<User> senderOpt = userRepository.findById(notification.getSenderId());
      senderOpt.ifPresent(user -> {
        vo.setSenderUsername(user.getUsername());
        vo.setSenderAvatar(user.getAvatar());
      });
    }

    return vo;
  }

  /**
   * Convert NotificationVO to WebSocketNotificationMessage
   */
  private WebSocketNotificationMessage convertToWebSocketMessage(NotificationVO vo) {
    WebSocketNotificationMessage message = new WebSocketNotificationMessage();
    message.setNid(vo.getNid());
    message.setType(vo.getType());
    message.setTitle(vo.getTitle());
    message.setContent(vo.getContent());
    message.setRelatedId(vo.getRelatedId());
    message.setSenderId(vo.getSenderId());
    message.setSenderUsername(vo.getSenderUsername());
    message.setSenderAvatar(vo.getSenderAvatar());
    message.setCreatedAt(vo.getCreatedAt());
    return message;
  }
}
