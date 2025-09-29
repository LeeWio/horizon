package com.sunrizon.horizon.messaging;

import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.sunrizon.horizon.constants.RabbitContants;
import com.sunrizon.horizon.constants.RedisContants;
import com.sunrizon.horizon.utils.MailUtil;
import com.sunrizon.horizon.utils.RedisUtil;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * RabbitMQ listener for handling OTP verification requests.
 * Processes email addresses received from the queue and sends OTP codes.
 */
@Component
@Slf4j
public class OtpListener {

  @Resource
  private RedisUtil redisUtil;

  @Resource
  private MailUtil mailUtil;

  /**
   * Handles OTP verification requests from the RabbitMQ queue.
   * Generates an OTP code, stores it in Redis with expiration, and sends it via
   * email.
   *
   * @param email the recipient's email address
   */
  @RabbitListener(bindings = @QueueBinding(value = @Queue(value = RabbitContants.OTP_VERIFICATION_QUEUE, durable = "true"), exchange = @Exchange(value = RabbitContants.OTP_VERIFICATION_EXCHANGE), key = RabbitContants.OTP_VERIFICATION_ROUTING_KEY))
  public void handleOtpRequest(String email) {
    try {
      // Validate email
      if (StrUtil.isBlank(email)) {
        log.error("Received empty email for OTP request");
        return;
      }

      // Generate 6-digit OTP code
      String otpCode = RandomUtil.randomNumbers(6);

      // Store OTP in Redis with expiration (10 minutes)
      String redisKey = String.format(RedisContants.OTP_KEY_FORMAT, email);

      boolean stored = redisUtil.set(redisKey, otpCode, 10, TimeUnit.MINUTES);

      if (!stored) {
        log.error("Failed to store OTP in Redis for email: {}", email);
        return;
      }

      // Send OTP email to the user
      boolean emailSent = mailUtil.sendOtpEmail(email, otpCode, "Your OTP Verification Code");

      if (emailSent) {
        log.info("OTP sent successfully to email: {}", email);
      } else {
        log.error("Failed to send OTP email to: {}", email);
      }
    } catch (Exception e) {
      log.error("Error processing OTP request for email: {}", email, e);
    }
  }

}
