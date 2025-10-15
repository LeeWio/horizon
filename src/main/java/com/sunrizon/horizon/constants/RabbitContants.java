package com.sunrizon.horizon.constants;

public class RabbitContants {
  // OTP verification service constants
  public static final String OTP_VERIFICATION_ROUTING_KEY = "otp.verify:";
  public static final String OTP_VERIFICATION_EXCHANGE = "otp.verification.exchange";
  public static final String OTP_VERIFICATION_QUEUE = "otp.verification.queue";

  // User audit service constants
  public static final String USER_AUDIT_ROUTING_KEY = "user.audit";
  public static final String USER_AUDIT_EXCHANGE = "user.audit.exchange";
  public static final String USER_AUDIT_QUEUE = "user.audit.queue";
}
