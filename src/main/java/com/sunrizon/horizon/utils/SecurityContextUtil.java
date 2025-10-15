package com.sunrizon.horizon.utils;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.sunrizon.horizon.pojo.CustomUserDetails;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SecurityContextUtil {

  public @NotNull Optional<String> getCurrentUserId() {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    log.debug("Current authentication: {}", authentication);

    if (authentication == null || !authentication.isAuthenticated()) {
      return Optional.empty();
    }

    Object object = authentication.getPrincipal();

    if (object instanceof CustomUserDetails) {
      return Optional.of(((CustomUserDetails) object).getUser().getUid());
    }

    return Optional.empty();

  }
}
