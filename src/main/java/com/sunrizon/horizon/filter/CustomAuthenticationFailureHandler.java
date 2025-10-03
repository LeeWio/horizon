package com.sunrizon.horizon.filter;

import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.AuthenticationException;

@Component
@Slf4j
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
  /**
   * Called when an authentication attempt fails.
   * 
   * @param request   the request during which the authentication attempt
   *                  occurred.
   * @param response  the response.
   * @param exception the exception which was thrown to reject the authentication
   *                  request.
   */
  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException, ServletException {
    log.error("CustomAuthenticationFailureHandler");
    // 设置响应状态为 401 Unauthorized
    response.setStatus(HttpServletResponse.SC_OK); // 401 Unauthorized
    response.setContentType("application/json;charset=UTF-8");

    // 返回自定义的错误信息
    response.getWriter().write("{\"error\": \"Authentication failed: " + exception.getMessage() + "\"}");
    response.getWriter().flush();
  }
}
