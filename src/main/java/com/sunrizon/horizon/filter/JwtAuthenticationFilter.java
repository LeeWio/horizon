package com.sunrizon.horizon.filter;

import java.io.IOException;
import java.lang.module.ModuleDescriptor.Requires;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sunrizon.horizon.pojo.CustomUserDetails;
import com.sunrizon.horizon.pojo.User;
import com.sunrizon.horizon.service.IUserService;
import com.sunrizon.horizon.utils.JwtUtil;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Resource
  private UserDetailsService userDetailsService;

  @Resource
  private IUserService userService;

  @Resource
  private JwtUtil jwtUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String authorization = getAuthorization(request);

    if (StrUtil.isNotBlank(authorization) && jwtUtil.validateAuthorization(authorization)) {

      String email = jwtUtil.getEmail(authorization);

      UserDetails userDetails = userDetailsService.loadUserByUsername(email);

      UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
          userDetails, null, userDetails.getAuthorities());

      // TODO: get ip
      WebAuthenticationDetails webAuthenticationDetails = new WebAuthenticationDetailsSource().buildDetails(request);
      usernamePasswordAuthenticationToken.setDetails(webAuthenticationDetails);

      SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

    filterChain.doFilter(request, response);
  }

  private String getAuthorization(HttpServletRequest request) {
    String bearerAuthorization = request.getHeader("Authorization");

    if (StrUtil.isNotBlank(bearerAuthorization) && bearerAuthorization.startsWith("Bearer ")) {
      return bearerAuthorization.substring(7);
    }

    return bearerAuthorization;
  }
}
