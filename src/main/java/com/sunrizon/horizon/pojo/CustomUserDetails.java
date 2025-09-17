package com.sunrizon.horizon.pojo;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.sunrizon.horizon.enums.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;
import java.util.Set;

/**
 * Custom implementation of Spring Security's UserDetails.
 * <p>
 * This class wraps a {@link User} entity and exposes its details
 * to Spring Security for authentication and authorization.
 */
@AllArgsConstructor
@Data
public class CustomUserDetails implements UserDetails {

  /** The underlying User entity */
  private final User user;

  /** The set of granted authorities (roles and permissions) */
  private final Set<GrantedAuthority> authorities;

  /**
   * Returns the authorities granted to the user.
   *
   * @return a collection of granted authorities
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  /**
   * Returns the password used to authenticate the user.
   *
   * @return the password
   */
  @Override
  public String getPassword() {
    return user.getPassword();
  }

  /**
   * Returns the username used to authenticate the user.
   * In this system, we use the email as the username.
   *
   * @return the user's email
   */
  @Override
  public String getUsername() {
    return user.getEmail();
  }

  /**
   * Indicates whether the user's account has expired.
   * Accounts are considered expired if they are INACTIVE, BANNED, or DELETED.
   *
   * @return true if the account is non-expired
   */
  @Override
  public boolean isAccountNonExpired() {
    return switch (user.getStatus()) {
      case ACTIVE, PENDING -> true;
      case INACTIVE, BANNED, DELETED -> false;
    };
  }

  /**
   * Indicates whether the user is locked or unlocked.
   * Accounts with BANNED status are considered locked.
   *
   * @return true if the account is not locked
   */
  @Override
  public boolean isAccountNonLocked() {
    return user.getStatus() != UserStatus.BANNED;
  }

  /**
   * Indicates whether the user's credentials (password) has expired.
   * Credentials are considered expired if the account is BANNED or DELETED.
   *
   * @return true if credentials are non-expired
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return user.getStatus() != UserStatus.BANNED && user.getStatus() != UserStatus.DELETED;
  }

  /**
   * Indicates whether the user is enabled or disabled.
   * Only users with ACTIVE status are considered enabled.
   *
   * @return true if the account is enabled
   */
  @Override
  public boolean isEnabled() {
    return user.getStatus() == UserStatus.ACTIVE;
  }
}
