package com.sunrizon.horizon.filter;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.sunrizon.horizon.enums.ResponseCode;
import com.sunrizon.horizon.utils.ResultResponse;

import cn.hutool.json.JSONUtil;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  /**
   * Commences an authentication scheme.
   * <p>
   * <code>ExceptionTranslationFilter</code> will populate the
   * <code>HttpSession</code>
   * attribute named
   * <code>AbstractAuthenticationProcessingFilter.SPRING_SECURITY_SAVED_REQUEST_KEY</code>
   * with the requested target URL before calling this method.
   * <p>
   * Implementations should modify the headers on the <code>ServletResponse</code>
   * as
   * necessary to commence the authentication process.
   * 
   * @param request       that resulted in an <code>AuthenticationException</code>
   * @param response      so that the user agent can begin authentication
   * @param authException that caused the invocation
   */
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
      throws IOException, ServletException {
    try {
      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentType("application/json;charset=UTF-8");

      ResultResponse<Object> resultResponse = ResultResponse.error(ResponseCode.UNAUTHORIZED,
          authException.getMessage());

      response.getWriter().write(JSONUtil.toJsonStr(resultResponse));
      response.getWriter().flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
