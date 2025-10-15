package com.sunrizon.horizon.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Custom health indicator for application-specific checks
 * <p>
 * This health indicator can be extended to check:
 * - External API availability
 * - File system access
 * - Critical business metrics
 */
@Component("custom")
public class CustomHealthIndicator implements HealthIndicator {

  @Override
  public Health health() {
    try {
      // Add custom health checks here
      // Example: Check if critical directories are writable
      // Example: Check if external APIs are reachable
      // Example: Check if database connections are available

      // For now, return UP status
      return Health.up()
          .withDetail("app", "Horizon Blog System")
          .withDetail("status", "All systems operational")
          .build();
    } catch (Exception e) {
      return Health.down()
          .withDetail("error", e.getMessage())
          .build();
    }
  }
}
