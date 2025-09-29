
package com.sunrizon.horizon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.sunrizon.horizon.filter.JwtAuthenticationFilter;

import jakarta.annotation.Resource;

/**
 * <p>
 * Global security configuration class.
 * </p>
 *
 * <p>
 * This class defines the security-related beans and rules for the application.
 * It:
 * <ul>
 * <li>Enables JPA auditing for automatic handling of created/modified
 * fields.</li>
 * <li>Enables method-level security annotations like {@code @PreAuthorize} and
 * {@code @Secured}.</li>
 * <li>Configures password encryption using BCrypt.</li>
 * <li>Configures CORS and HTTP security rules.</li>
 * </ul>
 * </p>
 */
@Configuration
@EnableMethodSecurity
@EnableJpaAuditing
public class SecurityConfig {

  @Resource
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  /**
   * Password encoder using BCrypt.
   *
   * @return a {@link BCryptPasswordEncoder} instance for secure password hashing.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Exposes the current {@link SecurityContext} as a Spring bean.
   *
   * @return the current {@link SecurityContext}.
   */
  @Bean
  public SecurityContext currentSecurityContext() {
    return SecurityContextHolder.getContext();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
      throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public SecurityContext securityContextHolder() {
    SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    return SecurityContextHolder.getContext();
  }

  /**
   * Main security filter chain configuration.
   *
   * - Disables CSRF (suitable for stateless REST APIs)
   * - Configures CORS (allow all origins/headers/methods for now)
   * - Sets authorization rules (adjust for production)
   *
   * @param http the {@link HttpSecurity} object
   * @return configured {@link SecurityFilterChain}
   * @throws Exception if configuration fails
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> {
          UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
          CorsConfiguration config = new CorsConfiguration();
          config.addAllowedHeader("*");
          config.addAllowedMethod("*");
          config.addAllowedOriginPattern("*"); // TODO: restrict in production
          config.setAllowCredentials(true);
          source.registerCorsConfiguration("/**", config);
          cors.configurationSource(source);
        })
        .authorizeHttpRequests(auth -> auth
            // Allow unauthenticated access to login endpoint
            // .requestMatchers("/api/user/login").permitAll()
            // Allow unauthenticated access to registration endpoint (optional)
            // .requestMatchers(HttpMethod.POST, "/api/user").permitAll()
            // All other /api/** endpoints require authentication
            // .requestMatchers("/api/**").authenticated()
            // Static resources or other requests
            .anyRequest().permitAll());

    http.sessionManagement(session -> session
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
