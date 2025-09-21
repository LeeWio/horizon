package com.sunrizon.horizon.filter;

import com.sunrizon.horizon.config.RateLimitConfig;
import com.sunrizon.horizon.enums.ResponseCode;
import com.sunrizon.horizon.utils.ResultResponse;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 请求频率限制过滤器
 * 基于 IP 地址进行限流
 */
@Component
@Order(1)
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    @Value("${rate-limit.enabled:false}")
    private boolean rateLimitEnabled;

    @Resource
    private RateLimitConfig.RateLimiter rateLimiter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        // 如果限流未启用，直接跳过
        if (!rateLimitEnabled) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 获取客户端 IP 地址
        String clientIp = getClientIpAddress(request);
        
        // 检查是否允许请求
        if (!rateLimiter.isAllowed(clientIp)) {
            log.warn("请求频率限制触发，IP: {}, URI: {}", clientIp, request.getRequestURI());
            
            // 设置响应头
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            
            // 添加限流相关响应头
            response.setHeader("X-RateLimit-Limit", String.valueOf(100)); // 每分钟最大请求数
            response.setHeader("X-RateLimit-Remaining", "0");
            response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() + 60000));
            response.setHeader("Retry-After", "60"); // 重试间隔（秒）
            
            // 返回限流错误响应
            ResultResponse<Object> errorResponse = ResultResponse.error(
                ResponseCode.TOO_MANY_REQUESTS, 
                "请求过于频繁，请稍后再试"
            );
            
            response.getWriter().write(errorResponse.toString());
            return;
        }
        
        // 添加限流信息到响应头
        long remaining = rateLimiter.getRemainingRequests(clientIp);
        response.setHeader("X-RateLimit-Limit", String.valueOf(100));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));
        
        // 继续处理请求
        filterChain.doFilter(request, response);
    }

    /**
     * 获取客户端真实 IP 地址
     * 考虑代理和负载均衡的情况
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            // 多次反向代理后会有多个IP值，第一个IP才是真实IP
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        String xForwardedProto = request.getHeader("X-Forwarded-Proto");
        if (xForwardedProto != null && !xForwardedProto.isEmpty()) {
            return request.getRemoteAddr();
        }
        
        return request.getRemoteAddr();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 排除不需要限流的路径
        String path = request.getRequestURI();
        return path.startsWith("/actuator/") || 
               path.startsWith("/v3/api-docs") || 
               path.startsWith("/swagger-ui") ||
               path.equals("/favicon.ico");
    }
}
