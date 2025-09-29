package com.sunrizon.horizon.utils;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for sending emails with support for both plain text and HTML content.
 * Provides methods for sending various types of emails including OTP verification,
 * registration confirmations, and other HTML formatted emails.
 */
@Component
@Slf4j
public class MailUtil {

  @Resource
  private JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String from;

  /**
   * Sends a simple plain text email.
   *
   * @param to the recipient's email address
   * @param subject the email subject
   * @param text the email content in plain text
   */
  public void sendSimpleMessage(String to, String subject, String text) {
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
      
      helper.setFrom(from);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(text, false); // false means it's plain text
      
      mailSender.send(mimeMessage);
      log.info("Plain text email sent successfully to: {}", to);
    } catch (Exception e) {
      log.error("Error sending plain text email to: {}", to, e);
      throw new RuntimeException("Failed to send email", e);
    }
  }

  /**
   * Sends an HTML formatted email.
   *
   * @param to the recipient's email address
   * @param subject the email subject
   * @param htmlContent the email content in HTML format
   */
  public void sendHtmlMessage(String to, String subject, String htmlContent) {
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
      
      helper.setFrom(from);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(htmlContent, true); // true means it's HTML
      
      mailSender.send(mimeMessage);
      log.info("HTML email sent successfully to: {}", to);
    } catch (Exception e) {
      log.error("Error sending HTML email to: {}", to, e);
      throw new RuntimeException("Failed to send HTML email", e);
    }
  }

  /**
   * Sends an OTP (One-Time Password) email with HTML formatting.
   *
   * @param to the recipient's email address
   * @param otpCode the OTP code to include in the email
   * @param subject the email subject
   * @return true if the email was sent successfully, false otherwise
   */
  public boolean sendOtpEmail(String to, String otpCode, String subject) {
    try {
      String htmlContent = buildOtpEmailContent(otpCode);
      sendHtmlMessage(to, subject, htmlContent);
      return true;
    } catch (Exception e) {
      log.error("Error sending OTP email to: {}", to, e);
      return false;
    }
  }

  /**
   * Builds the HTML content for an OTP email.
   *
   * @param otpCode the OTP code to include in the email
   * @return HTML formatted email content
   */
  private String buildOtpEmailContent(String otpCode) {
    return String.format("""
        <!DOCTYPE html>
        <html>
        <head>
            <title>OTP Verification</title>
            <meta charset="UTF-8">
            <style>
                body { font-family: Arial, sans-serif; background-color: #f5f5f5; margin: 0; padding: 0; }
                .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                .header { background-color: #4CAF50; color: white; text-align: center; padding: 30px 20px; }
                .content { padding: 30px; text-align: center; }
                .otp-code { display: inline-block; font-size: 24px; font-weight: bold; letter-spacing: 4px; margin: 20px 0; padding: 15px 30px; background-color: #f0f0f0; border-radius: 5px; }
                .footer { background-color: #f9f9f9; text-align: center; padding: 20px; font-size: 12px; color: #666; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>OTP Verification</h1>
                </div>
                <div class="content">
                    <h2>Your Verification Code</h2>
                    <p>Please use the following code to verify your account:</p>
                    <div class="otp-code">%s</div>
                    <p>This code will expire in 10 minutes.</p>
                    <p>If you didn't request this code, please ignore this email.</p>
                </div>
                <div class="footer">
                    <p>&copy; 2024 Horizon Blog Platform. All rights reserved.</p>
                </div>
            </div>
        </body>
        </html>
        """, otpCode);
  }

  /**
   * Sends a registration confirmation email with HTML formatting.
   *
   * @param to the recipient's email address
   * @param username the username of the new user
   * @param subject the email subject
   * @return true if the email was sent successfully, false otherwise
   */
  public boolean sendRegistrationConfirmationEmail(String to, String username, String subject) {
    try {
      String htmlContent = buildRegistrationConfirmationEmailContent(username);
      sendHtmlMessage(to, subject, htmlContent);
      return true;
    } catch (Exception e) {
      log.error("Error sending registration confirmation email to: {}", to, e);
      return false;
    }
  }

  /**
   * Builds the HTML content for a registration confirmation email.
   *
   * @param username the username of the new user
   * @return HTML formatted email content
   */
  private String buildRegistrationConfirmationEmailContent(String username) {
    return String.format("""
        <!DOCTYPE html>
        <html>
        <head>
            <title>Registration Confirmation</title>
            <meta charset="UTF-8">
            <style>
                body { font-family: Arial, sans-serif; background-color: #f5f5f5; margin: 0; padding: 0; }
                .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                .header { background-color: #4CAF50; color: white; text-align: center; padding: 30px 20px; }
                .content { padding: 30px; text-align: center; }
                .welcome { color: #4CAF50; font-size: 24px; margin-bottom: 20px; }
                .footer { background-color: #f9f9f9; text-align: center; padding: 20px; font-size: 12px; color: #666; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>Welcome to Horizon!</h1>
                </div>
                <div class="content">
                    <div class="welcome">Welcome, %s!</div>
                    <p>Your account has been successfully created.</p>
                    <p>We're excited to have you on board. Start exploring our platform and create amazing content!</p>
                    <p>If you have any questions, feel free to reach out to our support team.</p>
                </div>
                <div class="footer">
                    <p>&copy; 2024 Horizon Blog Platform. All rights reserved.</p>
                </div>
            </div>
        </body>
        </html>
        """, username);
  }

  /**
   * Sends a password reset email with HTML formatting.
   *
   * @param to the recipient's email address
   * @param resetLink the password reset link
   * @param subject the email subject
   * @return true if the email was sent successfully, false otherwise
   */
  public boolean sendPasswordResetEmail(String to, String resetLink, String subject) {
    try {
      String htmlContent = buildPasswordResetEmailContent(resetLink);
      sendHtmlMessage(to, subject, htmlContent);
      return true;
    } catch (Exception e) {
      log.error("Error sending password reset email to: {}", to, e);
      return false;
    }
  }

  /**
   * Builds the HTML content for a password reset email.
   *
   * @param resetLink the password reset link
   * @return HTML formatted email content
   */
  private String buildPasswordResetEmailContent(String resetLink) {
    return String.format("""
        <!DOCTYPE html>
        <html>
        <head>
            <title>Password Reset</title>
            <meta charset="UTF-8">
            <style>
                body { font-family: Arial, sans-serif; background-color: #f5f5f5; margin: 0; padding: 0; }
                .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                .header { background-color: #FF9800; color: white; text-align: center; padding: 30px 20px; }
                .content { padding: 30px; text-align: center; }
                .btn { display: inline-block; padding: 12px 30px; background-color: #FF9800; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                .footer { background-color: #f9f9f9; text-align: center; padding: 20px; font-size: 12px; color: #666; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>Password Reset</h1>
                </div>
                <div class="content">
                    <h2>Password Reset Request</h2>
                    <p>You have requested to reset your password. Click the button below to proceed:</p>
                    <a href="%s" class="btn">Reset Password</a>
                    <p>If you didn't request this, you can safely ignore this email.</p>
                    <p>This link will expire in 24 hours for security reasons.</p>
                </div>
                <div class="footer">
                    <p>&copy; 2024 Horizon Blog Platform. All rights reserved.</p>
                </div>
            </div>
        </body>
        </html>
        """, resetLink);
  }
}
