package com.sunrizon.horizon.filter;

import com.sunrizon.horizon.utils.XssUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * XSS过滤器
 * <p>
 * 自动过滤所有HTTP请求参数中的XSS攻击代码
 */
@Component
@Order(1) // 确保在其他过滤器之前执行
@Slf4j
public class XssFilter implements Filter {

  /**
   * 白名单路径：这些路径不进行XSS过滤（如文章内容）
   */
  private static final String[] WHITELIST_PATHS = {
      "/api/article/create",
      "/api/article/update"
  };

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String requestURI = httpRequest.getRequestURI();

    // 检查是否在白名单中
    boolean isWhitelisted = Arrays.stream(WHITELIST_PATHS)
        .anyMatch(requestURI::contains);

    if (isWhitelisted) {
      log.debug("Skipping XSS filter for whitelisted path: {}", requestURI);
      chain.doFilter(request, response);
      return;
    }

    // 包装请求，过滤XSS
    XssHttpServletRequestWrapper wrappedRequest = new XssHttpServletRequestWrapper(httpRequest);
    chain.doFilter(wrappedRequest, response);
  }

  /**
   * HttpServletRequest包装类，自动清理XSS
   */
  private static class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
      super(request);
    }

    @Override
    public String[] getParameterValues(String parameter) {
      String[] values = super.getParameterValues(parameter);
      if (values == null) {
        return null;
      }

      String[] cleanedValues = new String[values.length];
      for (int i = 0; i < values.length; i++) {
        cleanedValues[i] = cleanXss(values[i]);
      }
      return cleanedValues;
    }

    @Override
    public String getParameter(String parameter) {
      String value = super.getParameter(parameter);
      return cleanXss(value);
    }

    @Override
    public String getHeader(String name) {
      String value = super.getHeader(name);
      return cleanXss(value);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
      Map<String, String[]> originalMap = super.getParameterMap();
      return originalMap.entrySet().stream()
          .collect(Collectors.toMap(
              Map.Entry::getKey,
              entry -> {
                String[] values = entry.getValue();
                String[] cleanedValues = new String[values.length];
                for (int i = 0; i < values.length; i++) {
                  cleanedValues[i] = cleanXss(values[i]);
                }
                return cleanedValues;
              }
          ));
    }

    /**
     * 清理XSS攻击代码
     *
     * @param value 原始值
     * @return 清理后的值
     */
    private String cleanXss(String value) {
      if (value == null || value.isEmpty()) {
        return value;
      }

      // 对于普通参数，使用基础清理（移除所有HTML）
      // 如果需要保留某些HTML，可在Controller层使用XssUtil.cleanRelaxed()
      return XssUtil.cleanUserInput(value);
    }
  }
}
