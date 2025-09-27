package com.sunrizon.horizon.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import com.sunrizon.horizon.utils.SecurityContextUtil;

import jakarta.annotation.Resource;

@Component
public class SecurityAuditorAware implements AuditorAware<String> {

  @Resource
  private SecurityContextUtil securityContextUtil;

  @Override
  public Optional<String> getCurrentAuditor() {
    return securityContextUtil.getCurrentUserId();
  }
}
