package com.sunrizon.horizon.messaging;

import cn.hutool.core.util.StrUtil;
import com.sunrizon.horizon.constants.RabbitContants;
import com.sunrizon.horizon.pojo.User;
import com.sunrizon.horizon.repository.UserRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * User audit notification listener
 * <p>
 * Listens for new user registration events and notifies online admins for approval.
 * In production, this would integrate with WebSocket or email notification systems.
 */
@Component
@Slf4j
public class UserAuditListener {

  @Resource
  private UserRepository userRepository;

  /**
   * Handles user audit notification messages
   * <p>
   * Current implementation logs the notification for demonstration.
   * In production, this would:
   * 1. Send WebSocket notifications to online admins
   * 2. Send email notifications to admin email addresses
   * 3. Create admin notification records in database
   *
   * @param message the user audit message
   */
  @RabbitListener(
      bindings = @QueueBinding(
          value = @Queue(value = RabbitContants.USER_AUDIT_QUEUE, durable = "true"),
          exchange = @Exchange(value = RabbitContants.USER_AUDIT_EXCHANGE),
          key = RabbitContants.USER_AUDIT_ROUTING_KEY
      )
  )
  public void handleUserAuditNotification(UserAuditMessage message) {
    try {
      log.info("Received user audit notification: {}", message);

      // Verify user exists and is in PENDING status
      Optional<User> userOpt = userRepository.findById(message.getUserId());
      if (userOpt.isEmpty()) {
        log.warn("User not found for audit notification: {}", message.getUserId());
        return;
      }

      User user = userOpt.get();

      // Log notification details
      log.info("====== NEW USER REGISTRATION PENDING AUDIT ======");
      log.info("User ID: {}", user.getUid());
      log.info("Username: {}", user.getUsername());
      log.info("Email: {}", user.getEmail());
      log.info("Status: {}", user.getStatus());
      log.info("Registered At: {}", user.getCreatedAt());
      log.info("===============================================");

      // TODO: In production, implement the following:
      // 1. Send WebSocket notification to all online admins
      //    - webSocketService.sendToAdmins("NEW_USER_REGISTRATION", message);
      //
      // 2. Send email to admin email addresses
      //    - emailService.sendToAdmins("New User Registration", emailTemplate);
      //
      // 3. Create notification record for admins
      //    - notificationService.createNotificationForAdmins(NotificationType.SYSTEM, message);

      log.info("User audit notification processed successfully");

    } catch (Exception e) {
      log.error("Failed to process user audit notification: {}", message, e);
    }
  }
}
