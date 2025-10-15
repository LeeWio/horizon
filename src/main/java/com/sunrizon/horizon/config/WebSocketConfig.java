package com.sunrizon.horizon.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket Configuration
 * 
 * 启用STOMP协议的WebSocket消息代理
 * 支持实时通知推送
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Value("${websocket.allowed-origins:http://localhost:3000,http://localhost:8080}")
  private String allowedOrigins;

  /**
   * 注册STOMP端点
   * 客户端连接地址：/ws
   */
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
        .setAllowedOrigins(allowedOrigins.split(","))
        .withSockJS(); // 启用SockJS降级支持
  }

  /**
   * 配置消息代理
   * - 客户端发送消息目的地前缀：/app
   * - 服务端广播消息目的地前缀：/topic (多人), /queue (个人)
   */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    // 客户端发送消息目的地前缀
    config.setApplicationDestinationPrefixes("/app");
    
    // 启用简单消息代理
    // /topic - 广播消息（多人订阅）
    // /queue - 点对点消息（个人订阅）
    config.enableSimpleBroker("/topic", "/queue");
    
    // 用户目的地前缀（个人通知）
    config.setUserDestinationPrefix("/user");
  }
}
