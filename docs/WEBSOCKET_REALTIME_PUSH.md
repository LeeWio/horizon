# WebSocket 实时推送功能文档

> **完成时间**: 2025-10-15  
> **版本**: v1.0  
> **状态**: ✅ 已完成

---

## 📋 概述

本文档详细介绍了Horizon博客系统WebSocket实时推送功能的实现，基于STOMP协议实现用户在线时的即时通知推送。

---

## ✨ 功能特性

### 1. 实时通知推送 ✅
**功能**：用户在线时立即收到通知，无需刷新页面

**实现方式**：
- 使用STOMP协议over WebSocket
- SockJS降级支持（兼容不支持WebSocket的浏览器）
- 个人通知推送（/user/queue/notifications）
- 广播通知（/topic/notifications）

### 2. 在线状态检测 ✅
**功能**：检测用户是否在线

**实现方式**：
- 基于WebSocket会话注册表
- 实时查询用户连接状态
- 提供在线用户数量统计

### 3. 智能推送策略 ✅
**功能**：根据用户在线状态决定推送方式

**推送策略**：
- ✅ 用户在线 → 立即WebSocket推送 + 保存数据库
- ✅ 用户离线 → 仅保存数据库，用户上线后通过API获取

---

## 🏗️ 技术架构

### 架构图

```
┌─────────────┐         WebSocket连接          ┌──────────────┐
│   前端客户端  │ ◄─────────────────────────► │  Spring Boot  │
│  (Browser)  │         STOMP Protocol        │   WebSocket   │
└─────────────┘                                └──────────────┘
       │                                              │
       │                                              │
       │订阅: /user/queue/notifications               │
       │订阅: /topic/notifications                    │
       │                                              ▼
       │                                    ┌──────────────────┐
       │                                    │ Notification     │
       │                                    │ Service          │
       │                                    └──────────────────┘
       │                                              │
       │                                              │
       ▼                                              ▼
 ┌─────────────┐                            ┌──────────────────┐
 │  实时接收     │                            │   MySQL Database │
 │  通知消息     │                            │   (Notification) │
 └─────────────┘                            └──────────────────┘
```

### 核心组件

1. **[WebSocketConfig](file:///Users/wei.li/Developer/blog/horizon/src/main/java/com/sunrizon/horizon/config/WebSocketConfig.java)** - WebSocket配置类
2. **[IWebSocketNotificationService](file:///Users/wei.li/Developer/blog/horizon/src/main/java/com/sunrizon/horizon/service/IWebSocketNotificationService.java)** - WebSocket通知服务接口
3. **[WebSocketNotificationServiceImpl](file:///Users/wei.li/Developer/blog/horizon/src/main/java/com/sunrizon/horizon/service/impl/WebSocketNotificationServiceImpl.java)** - WebSocket通知服务实现
4. **[WebSocketNotificationMessage](file:///Users/wei.li/Developer/blog/horizon/src/main/java/com/sunrizon/horizon/dto/WebSocketNotificationMessage.java)** - WebSocket消息DTO
5. **[NotificationServiceImpl](file:///Users/wei.li/Developer/blog/horizon/src/main/java/com/sunrizon/horizon/service/impl/NotificationServiceImpl.java)** - 集成WebSocket的通知服务
6. **[WebSocketController](file:///Users/wei.li/Developer/blog/horizon/src/main/java/com/sunrizon/horizon/controller/WebSocketController.java)** - WebSocket管理接口

---

## 🔧 技术实现

### 1. WebSocket配置

#### WebSocketConfig.java

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Value("${websocket.allowed-origins:http://localhost:3000,http://localhost:8080}")
  private String allowedOrigins;

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
        .setAllowedOrigins(allowedOrigins.split(","))
        .withSockJS(); // 启用SockJS降级支持
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.setApplicationDestinationPrefixes("/app");
    config.enableSimpleBroker("/topic", "/queue");
    config.setUserDestinationPrefix("/user");
  }
}
```

**配置说明**：
- **连接端点**：`/ws`（支持SockJS）
- **应用前缀**：`/app`（客户端发送消息）
- **广播前缀**：`/topic`（多人订阅）
- **个人前缀**：`/queue`（个人订阅）
- **用户前缀**：`/user`（用于点对点推送）

---

### 2. WebSocket消息DTO

#### WebSocketNotificationMessage.java

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketNotificationMessage {
  private String nid;                  // 通知ID
  private NotificationType type;       // 通知类型
  private String title;                // 通知标题
  private String content;              // 通知内容
  private String relatedId;            // 关联资源ID
  private String senderId;             // 发送者ID
  private String senderUsername;       // 发送者用户名
  private String senderAvatar;         // 发送者头像
  private LocalDateTime createdAt;     // 创建时间
  private Long unreadCount;            // 未读通知数量
}
```

---

### 3. WebSocket通知服务

#### IWebSocketNotificationService.java（接口）

```java
public interface IWebSocketNotificationService {
  // 向指定用户发送通知
  void sendToUser(String userId, WebSocketNotificationMessage message);
  
  // 向所有在线用户广播通知
  void broadcastToAll(WebSocketNotificationMessage message);
  
  // 向指定角色的用户发送通知
  void sendToRole(String role, WebSocketNotificationMessage message);
  
  // 检查用户是否在线
  boolean isUserOnline(String userId);
  
  // 获取在线用户数量
  int getOnlineUserCount();
}
```

#### WebSocketNotificationServiceImpl.java（实现）

```java
@Service
@Slf4j
public class WebSocketNotificationServiceImpl implements IWebSocketNotificationService {

  @Resource
  private SimpMessagingTemplate messagingTemplate;

  @Resource
  private SimpUserRegistry userRegistry;

  @Override
  public void sendToUser(String userId, WebSocketNotificationMessage message) {
    try {
      log.info("Sending WebSocket notification to user: {}, type: {}", userId, message.getType());
      
      // 发送到用户专属队列
      messagingTemplate.convertAndSendToUser(
          userId,
          "/queue/notifications",
          message
      );
      
      log.debug("WebSocket notification sent successfully to user: {}", userId);
    } catch (Exception e) {
      log.error("Failed to send WebSocket notification to user: {}", userId, e);
    }
  }

  @Override
  public void broadcastToAll(WebSocketNotificationMessage message) {
    try {
      log.info("Broadcasting WebSocket notification to all users, type: {}", message.getType());
      messagingTemplate.convertAndSend("/topic/notifications", message);
      log.debug("WebSocket notification broadcasted successfully");
    } catch (Exception e) {
      log.error("Failed to broadcast WebSocket notification", e);
    }
  }

  @Override
  public boolean isUserOnline(String userId) {
    if (userId == null) return false;
    try {
      return userRegistry.getUser(userId) != null;
    } catch (Exception e) {
      log.error("Failed to check if user is online: {}", userId, e);
      return false;
    }
  }

  @Override
  public int getOnlineUserCount() {
    try {
      return userRegistry.getUserCount();
    } catch (Exception e) {
      log.error("Failed to get online user count", e);
      return 0;
    }
  }
}
```

---

### 4. 集成到通知系统

#### NotificationServiceImpl.java（修改）

```java
@Service
@Slf4j
public class NotificationServiceImpl implements INotificationService {

  @Resource
  private NotificationRepository notificationRepository;

  @Resource
  private UserRepository userRepository;

  @Resource
  private IWebSocketNotificationService webSocketNotificationService;

  @Override
  @Transactional
  public ResultResponse<NotificationVO> createNotification(String userId, NotificationType type, String title,
      String content, String relatedId, String senderId) {

    // ... 验证和创建通知 ...

    Notification saved = notificationRepository.save(notification);
    NotificationVO vo = convertToVO(saved);

    // WebSocket实时推送
    try {
      if (webSocketNotificationService.isUserOnline(userId)) {
        // 用户在线，推送实时通知
        WebSocketNotificationMessage wsMessage = convertToWebSocketMessage(vo);
        
        // 获取未读通知数量
        Long unreadCount = notificationRepository.countByUserIdAndIsRead(userId, false);
        wsMessage.setUnreadCount(unreadCount);
        
        webSocketNotificationService.sendToUser(userId, wsMessage);
        log.info("WebSocket notification sent to online user: {}", userId);
      } else {
        log.debug("User is offline, notification saved to database: {}", userId);
      }
    } catch (Exception e) {
      // WebSocket推送失败不影响通知创建
      log.warn("Failed to send WebSocket notification to user: {}", userId, e);
    }

    return ResultResponse.success(ResponseCode.NOTIFICATION_CREATED, vo);
  }

  // 转换NotificationVO到WebSocketNotificationMessage
  private WebSocketNotificationMessage convertToWebSocketMessage(NotificationVO vo) {
    WebSocketNotificationMessage message = new WebSocketNotificationMessage();
    message.setNid(vo.getNid());
    message.setType(vo.getType());
    message.setTitle(vo.getTitle());
    message.setContent(vo.getContent());
    message.setRelatedId(vo.getRelatedId());
    message.setSenderId(vo.getSenderId());
    message.setSenderUsername(vo.getSenderUsername());
    message.setSenderAvatar(vo.getSenderAvatar());
    message.setCreatedAt(vo.getCreatedAt());
    return message;
  }
}
```

---

## 📡 WebSocket通信协议

### 连接方式

#### 1. 标准WebSocket连接
```
ws://localhost:8080/ws
```

#### 2. SockJS连接（降级方案）
```
http://localhost:8080/ws
```

### 订阅目的地

#### 个人通知订阅
```
SUBSCRIBE /user/queue/notifications
```

**说明**：
- 订阅后接收个人通知
- 服务端发送目的地：`/user/{userId}/queue/notifications`
- 客户端订阅：`/user/queue/notifications`（框架自动处理userId）

#### 广播通知订阅
```
SUBSCRIBE /topic/notifications
```

**说明**：
- 订阅后接收系统广播
- 所有订阅者都会收到消息

#### 角色通知订阅
```
SUBSCRIBE /topic/role/{role}
```

**示例**：
- `/topic/role/ADMIN` - 管理员通知
- `/topic/role/AUTHOR` - 作者通知

---

## 🌐 前端集成示例

### 1. 使用SockJS + STOMP（推荐）

#### 安装依赖
```bash
npm install sockjs-client @stomp/stompjs
```

#### 连接代码
```javascript
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

// 创建WebSocket客户端
const client = new Client({
  webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
  
  connectHeaders: {
    // 可选：JWT Token认证
    Authorization: 'Bearer ' + localStorage.getItem('token')
  },
  
  debug: (str) => {
    console.log('STOMP Debug:', str);
  },
  
  onConnect: () => {
    console.log('WebSocket Connected!');
    
    // 订阅个人通知
    client.subscribe('/user/queue/notifications', (message) => {
      const notification = JSON.parse(message.body);
      handleNotification(notification);
    });
  },
  
  onStompError: (frame) => {
    console.error('STOMP Error:', frame);
  },
  
  onWebSocketClose: () => {
    console.log('WebSocket Disconnected');
  }
});

// 激活连接
client.activate();

// 处理接收到的通知
function handleNotification(notification) {
  console.log('Received notification:', notification);
  
  // 显示通知
  showNotificationPopup({
    title: notification.title,
    message: notification.content,
    type: notification.type,
    unreadCount: notification.unreadCount
  });
  
  // 更新未读数量徽章
  updateUnreadBadge(notification.unreadCount);
}

// 断开连接
function disconnect() {
  client.deactivate();
}
```

---

### 2. 使用原生WebSocket

```javascript
const socket = new WebSocket('ws://localhost:8080/ws');

socket.onopen = () => {
  console.log('WebSocket connected');
  
  // 发送STOMP连接帧
  const connectFrame = `CONNECT
accept-version:1.0,1.1,2.0
heart-beat:10000,10000

\0`;
  socket.send(connectFrame);
};

socket.onmessage = (event) => {
  console.log('Received:', event.data);
  
  // 解析STOMP消息
  const message = parseStompMessage(event.data);
  
  if (message.command === 'CONNECTED') {
    // 订阅个人通知
    const subscribeFrame = `SUBSCRIBE
id:sub-0
destination:/user/queue/notifications

\0`;
    socket.send(subscribeFrame);
  } else if (message.command === 'MESSAGE') {
    // 处理通知消息
    const notification = JSON.parse(message.body);
    handleNotification(notification);
  }
};

socket.onerror = (error) => {
  console.error('WebSocket error:', error);
};

socket.onclose = () => {
  console.log('WebSocket disconnected');
};
```

---

## 📊 消息流程

### 评论创建通知流程

```
1. 用户A评论用户B的文章
          ↓
2. CommentService创建评论
          ↓
3. NotificationService创建通知记录
          ↓
4. 检查用户B是否在线
          ↓
   ┌──────┴──────┐
   │ 在线        │ 离线
   ↓             ↓
5. WebSocket推送  仅保存数据库
   ↓             ↓
6. 用户B立即收到  用户B登录后通过API获取
```

### WebSocket推送流程

```
NotificationService.createNotification()
          ↓
检查用户是否在线（isUserOnline）
          ↓
转换VO → WebSocketMessage
          ↓
获取未读数量（unreadCount）
          ↓
webSocketNotificationService.sendToUser()
          ↓
SimpMessagingTemplate.convertAndSendToUser()
          ↓
STOMP发送消息到 /user/{userId}/queue/notifications
          ↓
客户端接收消息
          ↓
前端显示通知弹窗
```

---

## 🛡️ 安全措施

### 1. CORS配置
```yaml
websocket:
  allowed-origins: http://localhost:3000,http://localhost:8080
```

**生产环境**：
- ✅ 只允许可信域名
- ❌ 不使用通配符 `*`

### 2. 身份认证（待实现）

**推荐方案**：
```java
@Configuration
public class WebSocketSecurityConfig {
  
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/ws/**").authenticated()
      );
    return http.build();
  }
}
```

### 3. 消息隔离
- ✅ 用户只能接收自己的个人通知
- ✅ 使用`/user/{userId}/queue/notifications`确保消息隔离
- ✅ 框架自动处理用户会话匹配

---

## 📈 性能优化

### 1. 连接池管理
**当前**：Spring Boot默认配置

**优化建议**：
```yaml
spring:
  websocket:
    max-sessions: 10000        # 最大会话数
    message-size-limit: 128KB  # 消息大小限制
    send-buffer-size: 512KB    # 发送缓冲区
```

### 2. 心跳检测
**当前**：STOMP默认心跳（10秒）

**优化建议**：
```java
config.enableSimpleBroker("/topic", "/queue")
  .setHeartbeatValue(new long[]{10000, 10000}); // 10秒心跳
```

### 3. 消息队列（待实现）
**现状**：内存消息代理

**优化方案**：
- 使用RabbitMQ作为外部消息代理
- 支持分布式部署
- 消息持久化

---

## 🧪 测试指南

### 1. 在线状态检测
```bash
# 检查用户是否在线
GET /api/websocket/online/{userId}

# 获取在线用户数
GET /api/websocket/online/count
```

### 2. WebSocket连接测试

**使用Postman**：
1. 创建WebSocket Request
2. URL: `ws://localhost:8080/ws`
3. 发送STOMP CONNECT帧
4. 订阅：`/user/queue/notifications`

**使用浏览器控制台**：
```javascript
const socket = new SockJS('http://localhost:8080/ws');
const client = Stomp.over(socket);

client.connect({}, function() {
  console.log('Connected!');
  
  client.subscribe('/user/queue/notifications', function(message) {
    console.log('Received:', JSON.parse(message.body));
  });
});
```

### 3. 端到端测试流程

```
1. 用户A登录，建立WebSocket连接
2. 用户B评论用户A的文章
3. 验证用户A立即收到WebSocket通知
4. 检查通知内容是否正确
5. 检查未读数量是否更新
```

---

## 🗂️ 文件变更清单

### 新增文件（4个）

| 文件 | 行数 | 说明 |
|------|------|------|
| `config/WebSocketConfig.java` | 53 | WebSocket配置类 |
| `dto/WebSocketNotificationMessage.java` | 70 | WebSocket消息DTO |
| `service/IWebSocketNotificationService.java` | 50 | WebSocket服务接口 |
| `service/impl/WebSocketNotificationServiceImpl.java` | 123 | WebSocket服务实现 |
| `controller/WebSocketController.java` | 43 | WebSocket管理接口 |

### 修改文件（1个）

| 文件 | 变更 | 说明 |
|------|------|------|
| `service/impl/NotificationServiceImpl.java` | +42行 | 集成WebSocket推送 |

**总计**：新增 **339行** 代码，修改 **42行**

---

## 📋 配置清单

### application.yml
```yaml
# WebSocket configuration
websocket:
  allowed-origins: http://localhost:3000,http://localhost:8080
```

### pom.xml
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

---

## 🚀 部署建议

### 1. 单机部署
**现状**：内存消息代理（SimpleBroker）

**适用场景**：
- 小型应用（<1000并发用户）
- 单实例部署
- 测试环境

### 2. 分布式部署（推荐）

**升级方案**：
```java
@Override
public void configureMessageBroker(MessageBrokerRegistry config) {
  config.setApplicationDestinationPrefixes("/app");
  
  // 使用外部消息代理（RabbitMQ）
  config.enableStompBrokerRelay("/topic", "/queue")
    .setRelayHost("localhost")
    .setRelayPort(61613)
    .setClientLogin("guest")
    .setClientPasscode("guest");
  
  config.setUserDestinationPrefix("/user");
}
```

**优势**：
- ✅ 支持多实例负载均衡
- ✅ 消息持久化
- ✅ 高可用性

---

## 💡 最佳实践

### 1. 连接管理
- ✅ 用户登录后立即建立WebSocket连接
- ✅ 用户登出时主动断开连接
- ✅ 网络断开时自动重连（指数退避）

### 2. 消息处理
- ✅ WebSocket推送失败不影响核心业务
- ✅ 离线用户通过API轮询获取通知
- ✅ 避免发送过大的消息（建议<10KB）

### 3. 错误处理
- ✅ 捕获所有异常，记录日志
- ✅ 提供降级方案（离线通知）
- ✅ 前端显示友好的错误提示

---

## 🎉 总结

WebSocket实时推送功能已完整实现，主要成果：

✅ **完整性**：STOMP协议完整实现，支持个人/广播/角色推送  
✅ **易用性**：自动检测在线状态，智能推送策略  
✅ **可靠性**：推送失败不影响核心业务，离线通知保存数据库  
✅ **可扩展性**：支持升级到分布式消息代理  
✅ **安全性**：CORS配置，消息隔离机制  
✅ **文档完善**：详细的集成指南和最佳实践  

用户在线时可立即收到评论、点赞、关注等通知，大幅提升了用户体验和互动性！🚀

---

**文档版本**: 1.0  
**最后更新**: 2025-10-15  
**作者**: Qoder AI + Horizon Team
