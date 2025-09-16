
package com.sunrizon.horizon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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

  /**
   * <p>
   * Provides a {@link PasswordEncoder} bean using BCrypt.
   * </p>
   *
   * <p>
   * This encoder hashes passwords using the BCrypt algorithm.
   * It is the recommended way to store user passwords securely.
   * </p>
   *
   * @return a {@link BCryptPasswordEncoder} instance for password hashing.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * <p>
   * Exposes the current {@link SecurityContext} as a Spring bean.
   * </p>
   *
   * <p>
   * This allows other components to access the authentication information,
   * such as the current user’s principal or roles, through dependency injection.
   * </p>
   *
   * @return the current {@link SecurityContext}.
   */
  @Bean
  public SecurityContext securityContextHolder() {
    return SecurityContextHolder.getContext();
  }

  /**
   * <p>
   * Defines the main {@link SecurityFilterChain} for the application.
   * </p>
   *
   * <p>
   * This method:
   * <ul>
   * <li>Disables CSRF protection for stateless REST APIs.</li>
   * <li>Configures CORS to allow all origins, headers, and methods (adjust for
   * production).</li>
   * <li>Sets authorization rules, allowing unauthenticated access to the
   * {@code auth/authenticate} endpoint and permitting all other requests.</li>
   * </ul>
   * </p>
   *
   * @param http the {@link HttpSecurity} object to customize.
   * @return a built {@link SecurityFilterChain} instance.
   * @throws Exception if any error occurs during configuration.
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // Disable CSRF (useful for APIs)
        .csrf(AbstractHttpConfigurer::disable)
        // Configure CORS
        .cors((cors) -> {
          UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
          CorsConfiguration configuration = new CorsConfiguration();
          configuration.addAllowedHeader("*");
          configuration.addAllowedMethod("*");
          configuration.addAllowedOriginPattern("*");
          source.registerCorsConfiguration("/**", configuration);
          cors.configurationSource(source);
        })
        // Authorization rules
        .authorizeHttpRequests((authorize) -> authorize
            .requestMatchers(HttpMethod.POST, "auth/authenticate").permitAll()
            .anyRequest().permitAll());

    return http.build();
  }
}
