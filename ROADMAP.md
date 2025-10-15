# Horizon 博客系统功能规划路线图

> 本文档列出了将 Horizon 打造成专业博客系统所需的全部功能模块

---

## 📊 项目现状

### ✅ 已完成功能
- 用户注册、登录、JWT 认证
- 用户状态管理（PENDING, ACTIVE, INACTIVE, BANNED, DELETED）
- 基于角色的权限控制 (RBAC)
- OTP 邮件验证
- 文章创建、查询、分页
- 分类、标签、系列基础管理
- RabbitMQ 消息队列
- Redis 缓存
- Spring Security 安全框架
- Swagger API 文档
- **评论系统基础功能（CRUD、回复、分页）** ✨
- **文章编辑和删除功能** ✨
- **文章浏览量统计（PV/UV）** ✨
- **互动功能（点赞、收藏、分享）** ✨
- **用户关注系统（关注/取关、粉丝列表）** ✨ 新增

---

## 🔴 核心功能缺失

### 1. 评论系统 ⭐⭐⭐

**优先级**: 🔥 高

**功能点**:
- [x] 文章评论基础功能（CRUD） ✅
- [x] 评论回复（支持多级嵌套） ✅
- [ ] 评论点赞/点踩
- [ ] 评论审核机制
- [ ] 敏感词过滤
- [ ] 评论通知（邮件/站内信）
- [ ] 评论排序（热门、最新、最早）
- [x] 评论分页加载 ✅
- [ ] 评论@提及用户

**技术实现**:
- 实体类: `Comment`
- 表设计: `comment` (支持父子关系)
- 服务: `ICommentService`, `CommentServiceImpl`
- 控制器: `CommentController`

---

### 2. 文章功能增强 ⭐⭐⭐

**优先级**: 🔥 高

**功能点**:
- [x] 文章编辑功能（PUT `/api/article/{id}`） ✅
- [x] 文章删除功能（软删除/硬删除） ✅
- [ ] 文章草稿自动保存
- [ ] 文章版本控制/历史记录
- [ ] 文章全文搜索（Elasticsearch）
- [ ] 文章排序（按时间、热度、阅读量）
- [ ] 文章推荐算法
- [ ] 相关文章推荐
- [ ] 文章预览功能
- [ ] 文章定时发布
- [ ] 文章置顶功能
- [ ] 文章归档功能
- [ ] 文章导出（PDF/Markdown）

**技术实现**:
- 引入 Elasticsearch 进行全文搜索
- 使用 Quartz/XXL-Job 实现定时发布

---

### 3. 互动功能 ⭐⭐⭐ ✅ **已完成**

**优先级**: 🔥 高

**功能点**:
- [x] 文章点赞/取消点赞 ✅
- [x] 文章收藏/取消收藏 ✅
- [x] 文章分享功能 ✅
- [x] 点赞数/收藏数/分享数统计 ✅
- [x] 用户点赞列表查询 ✅
- [x] 用户收藏列表查询 ✅
- [x] 收藏夹管理（通过metadata字段） ✅
- [x] 检查用户互动状态 ✅
- [x] 文章互动用户列表 ✅
- [x] 用户关注/取关 ✅
- [x] 我的关注列表 ✅
- [x] 粉丝列表 ✅
- [x] 关注状态检查 ✅
- [x] 关注数/粉丝数统计 ✅
- [x] 防止自关注和重复关注 ✅
- [ ] 阅读历史记录
- [ ] 用户浏览记录
- [ ] 点赞通知
- [ ] 关注通知

**技术实现**:
- ✅ 实体类: `Interaction` (统一管理点赞、收藏、分享)
- ✅ 枚举类: `InteractionType` (LIKE, FAVORITE, SHARE)
- ✅ 表设计: `interaction` (唯一约束: article_id + user_id + type)
- ✅ Article 实体添加统计字段: `likeCount`, `favoriteCount`, `shareCount`
- ✅ 服务: `IInteractionService`, `InteractionServiceImpl`
- ✅ 控制器: `InteractionController` (完整RESTful API)
- ✅ 数据库表已自动创建并验证
- ✅ 所有API接口已测试通过
- ✅ 实体类: `Follow` (用户关注关系)
- ✅ 表设计: `follow` (唯一约束: follower_id + following_id)
- ✅ User 实体添加统计字段: `followingCount`, `followersCount`
- ✅ 服务: `IFollowService`, `FollowServiceImpl`
- ✅ 控制器: `FollowController` (完整RESTful API)
- ✅ 引入 SecurityContextUtil 统一认证机制
- ✅ 所有关注功能API接口已测试通过

---

### 4. 统计分析 ⭐⭐⭐ ✅ **已部分完成**

**优先级**: 🔥 高

**功能点**:
- [x] 文章浏览量统计（PV/UV） ✅
- [x] 热门文章排行（日/周/月/全部） ✅
- [x] 按浏览量排行 ✅
- [x] 按点赞数排行 ✅
- [x] 按收藏数排行 ✅
- [ ] 文章阅读时长统计
- [ ] 用户活跃度分析
- [ ] 用户增长趋势
- [ ] 文章发布趋势
- [ ] 数据可视化图表
- [ ] 实时在线用户统计
- [ ] 地域分布统计
- [ ] 设备/浏览器统计

**技术实现**:
- ✅ ArticleRepository 新增按浏览量/点赞/收藏数排序查询
- ✅ 支持时间范围筛选（DAY, WEEK, MONTH, ALL）
- ✅ ArticleServiceImpl 实现三种排行方法
- ✅ ArticleController 统一热门文章API接口
- 待使用 Redis HyperLogLog 统计 UV
- 待使用 Redis Sorted Set 实现排行榜
- 待定时任务聚合统计数据
- 待引入时序数据库（InfluxDB）存储趋势数据

---

### 5. 用户功能完善 ⭐⭐ ✅ **已部分完成**

**优先级**: ⚡ 中

**功能点**:
- [x] 用户资料查看（GET `/api/user/profile`） ✅
- [x] 用户资料编辑（PUT `/api/user/profile`） ✅
- [x] 修改密码功能（PUT `/api/user/password`） ✅
- [x] 密码强度验证 ✅
- [x] 密码找回功能 ✅
  - [x] 发送重置邮件（POST `/api/user/forgot-password`） ✅
  - [x] 重置密码（POST `/api/user/reset-password`） ✅
- [ ] 用户头像上传
- [ ] 用户个人主页
- [ ] 用户动态/时间线
- [ ] 用户成就系统
- [ ] 用户等级系统
- [ ] 密码找回功能
- [ ] 绑定邮箱/手机
- [ ] 第三方登录（GitHub, Google, 微信等）
- [ ] 双因素认证（2FA）
- [ ] 账号注销功能

**技术实现**:
- ✅ 完善 `UserServiceImpl.updateUser()` 方法
- ✅ 新增 `UserServiceImpl.changePassword()` 方法
- ✅ UserController 添加资料管理接口
- ✅ 使用 SecurityContextUtil 获取当前用户
- ✅ 密码强度验证（8位+字母+数字）
- ✅ 防止重复username/email
- 待新增 `Achievement`, `UserLevel` 实体
- 待使用 Spring Social 实现第三方登录
- 待使用 Google Authenticator 实现 2FA

---

### 6. 通知系统 ⭐⭐ ✅ **核心功能已完成**

**优先级**: ⚡ 中

**功能点**:
- [x] 站内消息/通知中心 ✅
- [x] 通知类型（评论、点赞、关注、收藏、系统） ✅
- [x] 消息已读/未读状态 ✅
- [x] 消息删除/清空 ✅
- [x] 未读数量统计 ✅
- [x] 按类型筛选通知 ✅
- [x] 单个通知标记已读 ✅
- [x] 批量标记全部已读 ✅
- [x] 删除单个通知 ✅
- [x] 删除已读通知 ✅
- [x] 删除全部通知 ✅
- [x] 通知分页查询 ✅
- [x] VO自动填充发送者信息 ✅
- [ ] 实时通知（WebSocket 推送）
- [ ] 邮件通知订阅设置
- [ ] 评论@提及通知
- [ ] 系统公告
- [ ] 通知聚合（避免重复）
- [ ] 通知推送渠道配置（站内/邮件/短信）

**技术实现**:
- ✅ 实体类: `Notification`
- ✅ 枚举类: `NotificationType` (5种类型)
- ✅ 表设计: `notification` (含3个索引)
- ✅ NotificationRepository 含 @Query 优化
- ✅ NotificationServiceImpl 完整业务逻辑
- ✅ NotificationController RESTful API (9个接口)
- ✅ 数据库索引优化（user_id, is_read, created_at）
- ✅ 简单字段引用设计（避免循环依赖）
- ✅ 使用 @Modifying 批量更新操作
- ✅ VO自动填充发送者username和avatar
- ✅ 完整测试通过（正常场景9个 + 异常场景2个）
- 待使用现有 WebSocket 配置
- 待使用 Redis Pub/Sub 实现实时推送
- 待使用 RabbitMQ 异步发送通知

---

### 7. 文件管理 ⭐⭐

**优先级**: 🔥 高

**功能点**:
- [x] 图片上传功能（完善现有 `File` 实体） ✅
- [x] 文件基础CRUD操作 ✅
- [x] 文件存储到本地目录 ✅
- [ ] 图片压缩/缩略图生成
- [ ] 文件存储服务（OSS/S3/MinIO）
- [ ] Markdown 图片粘贴上传
- [ ] 视频上传支持
- [ ] 附件管理
- [ ] 文件预览
- [ ] 文件下载统计
- [ ] 存储空间管理
- [ ] 图片水印

**技术实现**:
- ✅ 已实现 `FileRepository` 完整查询方法
- ✅ 已实现 `IFileService` 接口
- ✅ 已实现 `FileServiceImpl` 完整业务逻辑
- ✅ 已实现 `FileController` RESTful API
- ✅ 已实现文件上传、查询、删除（软删除/硬删除）
- 待引入 MinIO 对象存储
- 待使用 Thumbnailator 生成缩略图
- 待使用 FFmpeg 处理视频

---

### 8. 内容管理增强 ⭐⭐

**优先级**: ⚡ 中

**功能点**:
- [ ] 标签热度统计
- [ ] 标签合并功能
- [ ] 分类树形结构（支持多级分类）
- [ ] 系列文章排序/调整顺序
- [x] 用户注册审核工作流 ✅
- [ ] 文章内容审核
- [ ] 敏感内容检测
- [ ] SEO 优化（meta标签、sitemap）
- [ ] 文章别名/短链接
- [ ] 内容违规举报

**技术实现**:
- ✅ 已实现 `HorizonApplication` 中 TODO 的用户审核功能
- ✅ 使用 RabbitMQ 通知管理员审核
- ✅ AdminController 提供审核 API
- 待引入百度/阿里云内容安全 API
- 待生成 sitemap.xml

---

## 🟡 高级功能

### 9. 搜索功能 ⭐⭐⭐

**优先级**: 🔥 高

**功能点**:
- [ ] Elasticsearch 全文搜索
- [ ] 搜索历史记录
- [ ] 热门搜索关键词
- [ ] 搜索建议/自动补全
- [ ] 高级搜索（按标签、分类、作者筛选）
- [ ] 搜索结果高亮
- [ ] 搜索分词优化
- [ ] 搜索排序（相关度/时间）

**技术实现**:
- 引入 Spring Data Elasticsearch
- 实体类: `SearchHistory`
- 表设计: `search_history`
- 使用 IK 分词器
- 服务: `ISearchService`

---

### 10. 缓存优化 ⭐⭐

**优先级**: ⚡ 中

**功能点**:
- [ ] 热门文章缓存
- [ ] 用户信息缓存
- [ ] 分页数据缓存
- [ ] 缓存预热
- [ ] 缓存失效策略
- [ ] 多级缓存（本地缓存 + Redis）
- [ ] 缓存穿透/击穿/雪崩防护
- [ ] 缓存监控

**技术实现**:
- 使用 Spring Cache 抽象
- 使用 Caffeine 本地缓存
- 使用 Redisson 分布式锁
- 使用 Redis Pipeline 批量操作

---

### 11. 性能优化 ⭐⭐

**优先级**: ⚡ 中

**功能点**:
- [ ] 数据库索引优化
- [ ] 慢查询监控
- [ ] API 接口限流
- [ ] CDN 静态资源加速
- [ ] 数据库读写分离
- [ ] 分库分表支持
- [ ] 异步任务处理
- [ ] 数据库连接池优化
- [ ] SQL 优化（N+1 问题）

**技术实现**:
- 使用 Resilience4j 限流
- 使用 ShardingSphere 分库分表
- 使用 Druid 连接池监控
- 使用 JPA 懒加载优化

---

### 12. 管理后台 ⭐⭐⭐ ✅ **部分完成**

**优先级**: 🔥 高

**功能点**:
- [x] 用户管理界面（审核、禁用、删除） ✅ 部分完成（审核功能）
- [ ] 文章管理界面（审核、编辑、删除）
- [ ] 评论管理界面（审核、删除）
- [ ] 数据统计面板（Dashboard）
- [ ] 系统配置管理
- [ ] 日志查看（操作日志、错误日志）
- [ ] 操作审计
- [ ] 权限管理界面
- [ ] 角色管理界面
- [ ] 备份恢复

**技术实现**:
- ✅ 已创建 `AdminController`
- ✅ 已实现 TODO 中的用户审核功能
- ✅ 使用 RabbitMQ 通知管理员
- ✅ 提供 2 个管理接口：
  - GET /api/admin/users/pending: 获取待审核用户
  - POST /api/admin/users/audit: 审核用户
- 待新增 `OperationLog` 实体
- 待使用 Spring AOP 记录操作日志

---

### 13. 安全增强 ⭐⭐

**优先级**: ⚡ 中

**功能点**:
- [ ] IP 黑名单/白名单
- [ ] 防刷机制
- [ ] CAPTCHA 验证码
- [ ] XSS 防护
- [ ] SQL 注入防护（JPA 已自动防护）
- [ ] CSRF 防护（按需启用）
- [ ] 敏感操作二次验证
- [ ] API 调用频率限制
- [ ] 防暴力破解
- [ ] 登录日志记录
- [ ] 异常登录检测

**技术实现**:
- 使用 Kaptcha 生成验证码
- 使用 Resilience4j RateLimiter
- 使用 Redis 存储黑名单
- 使用 Spring Security CSRF Token
- 使用 Jsoup 过滤 XSS

---

### 14. RSS/订阅 ⭐

**优先级**: 💡 低

**功能点**:
- [ ] RSS Feed 生成（XML）
- [ ] Atom Feed 生成
- [ ] 邮件订阅功能
- [ ] 新文章推送（邮件）
- [ ] 订阅管理
- [ ] 退订功能

**技术实现**:
- 使用 Rome 库生成 RSS
- 新增 `Subscription` 实体
- 使用 RabbitMQ 异步发送邮件

---

### 15. 多语言支持 ⭐

**优先级**: 💡 低

**功能点**:
- [ ] i18n 国际化
- [ ] 多语言文章支持
- [ ] 自动语言检测
- [ ] 翻译建议

**技术实现**:
- 使用 Spring MessageSource
- 使用 `messages_zh_CN.properties`, `messages_en_US.properties`

---

## 🟢 扩展功能

### 16. 社区功能

**优先级**: 💡 低

**功能点**:
- [ ] 用户私信
- [ ] 用户群组/圈子
- [ ] 话题讨论
- [ ] 问答模块（类似 Stack Overflow）
- [ ] 投票功能
- [ ] 用户勋章系统

**技术实现**:
- 实体类: `Message`, `Group`, `Topic`, `Question`
- 使用 WebSocket 实现即时通讯

---

### 17. 内容变现

**优先级**: 💡 低

**功能点**:
- [ ] 付费文章
- [ ] 会员系统
- [ ] 打赏功能
- [ ] 广告管理
- [ ] 订单管理
- [ ] 支付集成（微信/支付宝）

**技术实现**:
- 实体类: `Order`, `Payment`, `Membership`
- 集成支付宝 SDK、微信支付 SDK

---

### 18. AI 功能

**优先级**: 💡 低

**功能点**:
- [ ] AI 文章摘要生成
- [ ] AI 标签推荐
- [ ] AI 内容审核
- [ ] AI 写作助手
- [ ] AI 纠错
- [ ] 智能推荐

**技术实现**:
- 集成 OpenAI API
- 集成阿里云 NLP
- 使用推荐算法（协同过滤）

---

### 19. 移动端支持

**优先级**: 💡 低

**功能点**:
- [ ] 移动端 API 优化
- [ ] 推送通知（APNs/FCM）
- [ ] APP 专用接口
- [ ] 图片自适应

**技术实现**:
- 使用响应式设计
- 集成极光推送

---

### 20. 运维监控 ✅ **核心功能已完成**

**优先级**: ⚡ 中

**功能点**:
- [x] 健康检查接口（完善 Actuator） ✅
- [x] Prometheus 监控指标 ✅
- [x] 自定义健康检查指标 ✅
- [x] 自定义业务指标 ✅
- [ ] 日志聚合（ELK Stack）
- [ ] 告警机制
- [ ] 性能监控（APM）
- [ ] 链路追踪
- [ ] 错误追踪（Sentry）

**技术实现**:
- ✅ 已启用 Spring Boot Actuator
- ✅ 已集成 Micrometer Prometheus
- ✅ 已创建 CustomHealthIndicator
- ✅ 已创建 CustomMetrics（用户注册、文章创建、登录统计等）
- ✅ 已配置 6 个 Actuator 端点（health, metrics, prometheus, info, env, loggers）
- ✅ 共 118 个指标可用
- 待集成 Prometheus + Grafana
- 待集成 ELK（Elasticsearch + Logstash + Kibana）
- 待集成 SkyWalking 链路追踪

---

## 📋 实施优先级

### 🔥 P0 - 必须立即实现（核心功能）

1. ✅ **文章编辑/删除功能** - 补全基础 CRUD（已完成）
2. ✅ **评论系统** - 博客核心互动功能（已完成）
3. ✅ **文件上传** - 图片管理（已完成）
4. ✅ **互动功能** - 点赞/收藏/分享（已完成）
5. ✅ **浏览量统计** - 基础数据统计（已完成）
6. ✅ **用户关注系统** - 社交功能基础（已完成）
7. ✅ **用户资料编辑** - 完善用户功能（已完成）
8. **文章搜索** - 用户体验关键

**预计工期**: 2-3 周

---

### ⚡ P1 - 高优先级（增强体验）

1. ✅ **点赞/收藏功能** - 已完成
2. ✅ **关注/粉丝系统** - 已完成
3. ✅ **通知系统核心功能** - 已完成
4. ✅ **热门文章排行** - 已完成
5. ✅ **密码找回** - 已完成
6. ✅ **用户审核功能（实现 TODO）** - 已完成
7. **管理后台** - 待开发（仅剩此功能）
8. **通知系统高级功能**（WebSocket实时推送、邮件订阅） - 待开发（P2移至）

**预计工期**: 剩余 0.5-1 周

---

### 💡 P2 - 中优先级（优化提升）

1. **缓存优化** - 待开发
2. **安全增强** - 待开发
3. **性能优化** - 待开发
4. ✅ **运维监控** - 已完成 ✨ 新增
5. **内容审核** - 待开发
6. **SEO 优化** - 待开发

**预计工期**: 剩余 1.5-2 周

---

### 🌟 P3 - 低优先级（锦上添花）

1. **第三方登录**
2. **RSS 订阅**
3. **内容变现**
4. **AI 功能**
5. **多语言支持**
6. **社区功能**

**预计工期**: 按需安排

---

## 🛠️ 技术架构补充

### 需要引入的技术栈

#### 搜索引擎
- **Elasticsearch** - 全文搜索
- **Spring Data Elasticsearch**

#### 对象存储
- **MinIO** - 开源对象存储
- **阿里云 OSS** / **AWS S3** - 云存储

#### 缓存增强
- **Caffeine** - 本地缓存
- **Redisson** - 分布式锁、限流

#### 任务调度
- **Quartz** - 定时任务
- **XXL-Job** - 分布式任务调度

#### 限流熔断
- **Resilience4j** - 限流、熔断、降级

#### 监控运维
- **Spring Boot Actuator** - 健康检查
- **Prometheus** + **Grafana** - 监控可视化
- **ELK Stack** - 日志聚合
- **SkyWalking** - 链路追踪

#### 消息推送
- **WebSocket** - 实时推送（已有）
- **Redis Pub/Sub** - 发布订阅

#### 图片处理
- **Thumbnailator** - 图片压缩
- **ImageMagick** - 图片处理

#### 安全工具
- **Kaptcha** - 验证码
- **Jsoup** - XSS 过滤

---

## 📊 数据库设计补充

### 需要新增的表

```sql
-- 评论表
CREATE TABLE comment (
  cid VARCHAR(36) PRIMARY KEY,
  article_id VARCHAR(36) NOT NULL,
  user_id VARCHAR(36) NOT NULL,
  parent_id VARCHAR(36),
  content TEXT NOT NULL,
  status VARCHAR(20) DEFAULT 'APPROVED',
  likes_count INT DEFAULT 0,
  created_at DATETIME,
  updated_at DATETIME,
  INDEX idx_article_id (article_id),
  INDEX idx_user_id (user_id),
  INDEX idx_parent_id (parent_id)
);

-- 点赞表
CREATE TABLE like_record (
  id VARCHAR(36) PRIMARY KEY,
  user_id VARCHAR(36) NOT NULL,
  target_id VARCHAR(36) NOT NULL,
  target_type VARCHAR(20) NOT NULL, -- ARTICLE, COMMENT
  created_at DATETIME,
  UNIQUE KEY uk_user_target (user_id, target_id, target_type)
);

-- 收藏表
CREATE TABLE favorite (
  id VARCHAR(36) PRIMARY KEY,
  user_id VARCHAR(36) NOT NULL,
  article_id VARCHAR(36) NOT NULL,
  folder_name VARCHAR(50),
  created_at DATETIME,
  UNIQUE KEY uk_user_article (user_id, article_id)
);

-- 关注表
CREATE TABLE follow (
  id VARCHAR(36) PRIMARY KEY,
  follower_id VARCHAR(36) NOT NULL,
  following_id VARCHAR(36) NOT NULL,
  created_at DATETIME,
  UNIQUE KEY uk_follower_following (follower_id, following_id)
);

-- 通知表
CREATE TABLE notification (
  nid VARCHAR(36) PRIMARY KEY,
  user_id VARCHAR(36) NOT NULL,
  type VARCHAR(20) NOT NULL, -- COMMENT, LIKE, FOLLOW, SYSTEM
  content TEXT,
  is_read BOOLEAN DEFAULT FALSE,
  related_id VARCHAR(36),
  created_at DATETIME,
  INDEX idx_user_id (user_id),
  INDEX idx_is_read (is_read)
);

-- 阅读历史表
CREATE TABLE reading_history (
  id VARCHAR(36) PRIMARY KEY,
  user_id VARCHAR(36) NOT NULL,
  article_id VARCHAR(36) NOT NULL,
  read_time INT, -- 阅读时长（秒）
  created_at DATETIME,
  updated_at DATETIME,
  INDEX idx_user_id (user_id),
  INDEX idx_article_id (article_id)
);

-- 搜索历史表
CREATE TABLE search_history (
  id VARCHAR(36) PRIMARY KEY,
  user_id VARCHAR(36),
  keyword VARCHAR(200) NOT NULL,
  created_at DATETIME,
  INDEX idx_user_id (user_id),
  INDEX idx_keyword (keyword)
);

-- 系统配置表
CREATE TABLE system_config (
  id VARCHAR(36) PRIMARY KEY,
  config_key VARCHAR(100) NOT NULL UNIQUE,
  config_value TEXT,
  description VARCHAR(255),
  created_at DATETIME,
  updated_at DATETIME
);

-- 操作日志表
CREATE TABLE operation_log (
  id VARCHAR(36) PRIMARY KEY,
  user_id VARCHAR(36),
  operation VARCHAR(100),
  method VARCHAR(50),
  params TEXT,
  ip VARCHAR(50),
  created_at DATETIME,
  INDEX idx_user_id (user_id),
  INDEX idx_created_at (created_at)
);

-- 文章版本表
CREATE TABLE article_version (
  id VARCHAR(36) PRIMARY KEY,
  article_id VARCHAR(36) NOT NULL,
  version INT NOT NULL,
  title VARCHAR(100),
  content TEXT,
  created_by VARCHAR(36),
  created_at DATETIME,
  INDEX idx_article_id (article_id)
);

-- 订阅表
CREATE TABLE subscription (
  id VARCHAR(36) PRIMARY KEY,
  email VARCHAR(100) NOT NULL UNIQUE,
  is_active BOOLEAN DEFAULT TRUE,
  created_at DATETIME,
  updated_at DATETIME
);
```

---

## 📅 开发计划

### 第一阶段（1-2 个月）- 核心功能完善
- 文章 CRUD 完整实现
- 评论系统
- 文件上传
- 基础统计

### 第二阶段（1-2 个月）- 互动功能
- 点赞/收藏
- 关注系统
- 通知系统
- 搜索功能

### 第三阶段（1 个月）- 管理后台
- 用户管理
- 内容审核
- 数据统计
- 系统配置

### 第四阶段（1 个月）- 性能优化
- 缓存优化
- 性能调优
- 安全加固
- 监控部署

### 第五阶段（按需）- 高级功能
- AI 功能
- 内容变现
- 社区功能
- 移动端

---

## 📝 备注

1. **敏感信息处理**: 
   - 将 `application.yml` 中的数据库密码、邮箱密码、JWT 密钥迁移到环境变量
   - 生产环境禁止硬编码敏感信息

2. **CORS 配置**: 
   - 生产环境限制允许的域名，不使用 `*`

3. **权限控制**: 
   - 启用 Controller 中注释的 `@PreAuthorize` 注解
   - 完善权限管理

4. **单元测试**: 
   - 为每个 Service 编写单元测试
   - 达到 80% 以上代码覆盖率

5. **API 文档**: 
   - 完善 Swagger 注解
   - 添加请求/响应示例

6. **日志规范**: 
   - 统一日志格式
   - 添加链路追踪 ID

---

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

开发新功能前，请：
1. 查看本 ROADMAP 确认功能优先级
2. 创建 Issue 讨论实现方案
3. Fork 项目并创建特性分支
4. 编写单元测试
5. 提交 Pull Request

---

**最后更新**: 2025-10-15
**维护者**: Horizon Team
