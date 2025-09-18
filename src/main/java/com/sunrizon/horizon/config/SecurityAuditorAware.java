package com.sunrizon.horizon.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.sunrizon.horizon.pojo.CustomUserDetails;

import cn.hutool.core.bean.BeanUtil;

@Component
public class SecurityAuditorAware implements AuditorAware<String> {

  @Override
  public Optional<String> getCurrentAuditor() {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (BeanUtil.isEmpty(authentication) && authentication.isAuthenticated()) {
      return Optional.empty();
    }

    Object object = authentication.getPrincipal();

    if (object instanceof CustomUserDetails userDetails) {
      return Optional.of(userDetails.getUser().getUid());
    }

    return Optional.empty();
  }
}
