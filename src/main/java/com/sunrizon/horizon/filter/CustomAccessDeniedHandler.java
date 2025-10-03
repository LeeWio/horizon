package com.sunrizon.horizon.filter;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.sunrizon.horizon.enums.ResponseCode;
import com.sunrizon.horizon.utils.ResultResponse;

import cn.hutool.json.JSONUtil;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
  /**
   * Handles an access denied failure.
   * 
   * @param request               that resulted in an
   *                              <code>AccessDeniedException</code>
   * @param response              so that the user agent can be advised of the
   *                              failure
   * @param accessDeniedException that caused the invocation
   * @throws IOException      in the event of an IOException
   * @throws ServletException in the event of a ServletException
   */
  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException, ServletException {

    log.error("accessDenied");

    try {
      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentType("application/json;charset=UTF-8");

      ResultResponse<Object> resultResponse = ResultResponse.error(ResponseCode.SUCCESS,
          accessDeniedException.getMessage());

      response.getWriter().write(JSONUtil.toJsonStr(resultResponse));
      response.getWriter().flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
