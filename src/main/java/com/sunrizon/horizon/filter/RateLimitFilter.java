package com.sunrizon.horizon.filter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Rate limiting filter using Resilience4j
 * <p>
 * Applies different rate limits to different endpoints:
 * - Login: 5 requests per minute
 * - Registration: 3 requests per minute
 * - API endpoints: 50 requests per second
 * - Default: 100 requests per second
 */
@Component
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

  private final RateLimiterRegistry rateLimiterRegistry;

  public RateLimitFilter(RateLimiterRegistry rateLimiterRegistry) {
    this.rateLimiterRegistry = rateLimiterRegistry;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String requestURI = request.getRequestURI();
    String clientIP = getClientIP(request);

    // Determine which rate limiter to use based on endpoint
    String rateLimiterName = determineRateLimiter(requestURI);
    RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter(rateLimiterName);

    try {
      // Try to acquire permission
      rateLimiter.acquirePermission();

      // Log successful request
      log.debug("Rate limit check passed for {} from IP: {}", requestURI, clientIP);

      // Continue with the request
      filterChain.doFilter(request, response);

    } catch (RequestNotPermitted e) {
      // Rate limit exceeded
      log.warn("Rate limit exceeded for {} from IP: {} (limiter: {})", 
          requestURI, clientIP, rateLimiterName);

      // Return 429 Too Many Requests
      response.setStatus(429);  // HttpServletResponse.SC_TOO_MANY_REQUESTS (available in Servlet 6.0+)
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      response.getWriter().write(
          String.format("{\"status\":429,\"message\":\"Too many requests. Please try again later.\",\"path\":\"%s\"}", 
              requestURI));
    }
  }

  /**
   * Determine which rate limiter to use based on request URI
   */
  private String determineRateLimiter(String requestURI) {
    if (requestURI.contains("/authenticate") || requestURI.contains("/login")) {
      return "login";
    } else if (requestURI.contains("/register") || requestURI.contains("/user/create")) {
      return "registration";
    } else if (requestURI.startsWith("/api/")) {
      return "api";
    } else {
      return "default";
    }
  }

  /**
   * Get client IP address from request
   */
  private String getClientIP(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("X-Real-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    // Take first IP if there are multiple (comma-separated)
    if (ip != null && ip.contains(",")) {
      ip = ip.split(",")[0].trim();
    }
    return ip;
  }
}
