package com.sunrizon.horizon.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Custom metrics configuration for business monitoring
 * <p>
 * Provides custom metrics for:
 * - User registration count
 * - Article creation count
 * - Login attempts
 * - API response times
 */
@Component
@Slf4j
public class CustomMetrics {

  private final MeterRegistry meterRegistry;

  // Counters
  private Counter userRegistrationCounter;
  private Counter articleCreationCounter;
  private Counter loginAttemptCounter;
  private Counter loginSuccessCounter;
  private Counter loginFailureCounter;

  // Timers
  private Timer articleQueryTimer;
  private Timer userQueryTimer;

  public CustomMetrics(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  @PostConstruct
  public void init() {
    // Initialize counters
    userRegistrationCounter = Counter.builder("horizon.user.registration")
        .description("Total number of user registrations")
        .tag("type", "user")
        .register(meterRegistry);

    articleCreationCounter = Counter.builder("horizon.article.creation")
        .description("Total number of articles created")
        .tag("type", "article")
        .register(meterRegistry);

    loginAttemptCounter = Counter.builder("horizon.login.attempts")
        .description("Total number of login attempts")
        .tag("type", "auth")
        .register(meterRegistry);

    loginSuccessCounter = Counter.builder("horizon.login.success")
        .description("Successful login count")
        .tag("type", "auth")
        .tag("result", "success")
        .register(meterRegistry);

    loginFailureCounter = Counter.builder("horizon.login.failure")
        .description("Failed login count")
        .tag("type", "auth")
        .tag("result", "failure")
        .register(meterRegistry);

    // Initialize timers
    articleQueryTimer = Timer.builder("horizon.article.query.time")
        .description("Time taken to query articles")
        .tag("operation", "query")
        .register(meterRegistry);

    userQueryTimer = Timer.builder("horizon.user.query.time")
        .description("Time taken to query users")
        .tag("operation", "query")
        .register(meterRegistry);

    log.info("Custom metrics initialized successfully");
  }

  // Counter methods
  public void incrementUserRegistration() {
    userRegistrationCounter.increment();
  }

  public void incrementArticleCreation() {
    articleCreationCounter.increment();
  }

  public void incrementLoginAttempt() {
    loginAttemptCounter.increment();
  }

  public void incrementLoginSuccess() {
    loginSuccessCounter.increment();
  }

  public void incrementLoginFailure() {
    loginFailureCounter.increment();
  }

  // Timer methods
  public Timer.Sample startArticleQueryTimer() {
    return Timer.start(meterRegistry);
  }

  public void stopArticleQueryTimer(Timer.Sample sample) {
    sample.stop(articleQueryTimer);
  }

  public Timer.Sample startUserQueryTimer() {
    return Timer.start(meterRegistry);
  }

  public void stopUserQueryTimer(Timer.Sample sample) {
    sample.stop(userQueryTimer);
  }
}
