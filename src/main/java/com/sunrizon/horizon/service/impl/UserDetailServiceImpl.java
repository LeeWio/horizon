package com.sunrizon.horizon.service.impl;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.sunrizon.horizon.exception.UsernameNotFoundException;
import com.sunrizon.horizon.pojo.CustomUserDetails;
import com.sunrizon.horizon.pojo.User;
import com.sunrizon.horizon.repository.UserRepository;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of Spring Security's UserDetailsService.
 *
 * <p>
 * This service is responsible for loading user-specific data during
 * authentication. It maps a User entity to Spring Security's UserDetails,
 * including roles and permissions.
 */
@Service
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService {

  @Resource
  private UserRepository userRepository;

  /**
   * Loads the user by their email (used as username in this system).
   *
   * @param username the email of the user
   * @return a fully populated UserDetails object with roles and permissions
   * @throws UsernameNotFoundException if the user cannot be found
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // Fetch the user by email
    log.error("UserDetailServiceImpl: {}", username);

    User user = userRepository.findUserByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with emaail" + username));

    // Collect authorities from roles and permissions
    Set<GrantedAuthority> authorities = user.getRoles().stream()
        .flatMap(role -> {
          // Map each permission of the role to a SimpleGrantedAuthority
          Set<GrantedAuthority> roleAuthorities = role.getPermissions().stream()
              .map(permission -> new SimpleGrantedAuthority(permission.getName().name()))
              .collect(Collectors.toSet());

          // Add role with ROLE_ prefix for Spring Security
          roleAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName().name()));

          return roleAuthorities.stream();
        })
        .collect(Collectors.toSet());

    // Return a CustomUserDetails object for Spring Security
    return new CustomUserDetails(user, authorities);
  }
}
