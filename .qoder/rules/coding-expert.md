---
trigger: always_on
alwaysApply: true
---

# Horizon 项目开发规范指南

> 本文档规定了 Horizon 博客系统的开发流程、编码规范和最佳实践，所有开发人员必须严格遵守。

---

## 📚 目录

1. [核心原则](#核心原则)
2. [项目架构](#项目架构)
3. [命名规范](#命名规范)
4. [编码规范](#编码规范)
5. [开发流程](#开发流程)
6. [自测规范](#自测规范)
7. [注意事项](#注意事项)

---

## 核心原则

### ⚠️ 关键规则

1. **JPA 自动建表**: 
   - ✅ **必须**使用 JPA 实体类自动生成数据库表
   - ❌ **禁止**手动编写 SQL 建表语句
   - 配置: `spring.jpa.hibernate.ddl-auto=update`

2. **依赖注入**:
   - ✅ **必须**使用 `@Resource` 注解
   - ❌ **避免**使用 `@Autowired`

3. **工具库**:
   - ✅ **优先**使用 Hutool 工具类（`StrUtil`, `BeanUtil`, `RandomUtil`等）
   - ❌ **避免**使用 Apache Commons 或其他重复功能的库

4. **分层架构**:
   - ✅ **严格遵守** Controller → Service → Repository
   - ❌ **禁止**跨层调用（Controller 直接调用 Repository）

---

## 项目架构

### 标准目录结构

```
com.sunrizon.horizon
├── config/          # 配置类（SecurityConfig, RedisConfig等）
├── constants/       # 常量类（RedisConstants, RabbitConstants）
├── controller/      # 控制器层（接收请求、返回响应）
├── dto/            # 数据传输对象（CreateXxxRequest, UpdateXxxRequest）
├── enums/          # 枚举类（UserStatus, ResponseCode等）
├── exception/      # 异常处理（GlobalExceptionHandler, BusinessException）
├── filter/         # 过滤器（JwtAuthenticationFilter）
├── messaging/      # 消息队列监听器（OtpListener）
├── pojo/           # 实体类（User, Article, Category等）
├── repository/     # 数据访问层（UserRepository, ArticleRepository）
├── service/        # 业务逻辑层
│   ├── impl/      # 业务实现类（UserServiceImpl, ArticleServiceImpl）
│   └── I*.java    # 业务接口（IUserService, IArticleService）
├── utils/          # 工具类（JwtUtil, RedisUtil, ResultResponse）
└── vo/             # 视图对象（UserVO, ArticleVO）
```

### 层级职责

| 层级 | 职责 | 禁止操作 |
|------|------|----------|
| **Controller** | 接收请求、参数验证、调用Service、返回响应 | ❌ 编写业务逻辑、直接调用Repository |
| **Service** | 业务逻辑处理、事务管理、数据转换 | ❌ 直接操作Request/Response |
| **Repository** | 数据访问、查询定义 | ❌ 包含业务逻辑 |

---

## 命名规范

### 1. 包命名
- 全部小写，不使用下划线
- 示例: `com.sunrizon.horizon.service.impl`

### 2. 类命名 (PascalCase)

| 类型 | 命名模板 | 示例 |
|------|----------|------|
| 实体类 | `{模块名}` | `User`, `Article`, `Category` |
| 服务接口 | `I{模块名}Service` | `IUserService`, `IArticleService` |
| 服务实现 | `{模块名}ServiceImpl` | `UserServiceImpl`, `ArticleServiceImpl` |
| 控制器 | `{模块名}Controller` | `UserController`, `ArticleController` |
| Repository | `{模块名}Repository` | `UserRepository`, `ArticleRepository` |
| 请求DTO | `{操作}{模块名}Request` | `CreateUserRequest`, `UpdateArticleRequest` |
| 响应VO | `{模块名}VO` | `UserVO`, `ArticleVO` |
| 枚举 | `{含义}` | `UserStatus`, `ArticleStatus`, `ResponseCode` |
| 常量类 | `{模块名}Constants` | `RedisConstants`, `RabbitConstants` |

### 3. 方法命名 (camelCase)

| 操作 | 命名模板 | 示例 |
|------|----------|------|
| 创建 | `create{Entity}` | `createUser`, `createArticle` |
| 更新 | `update{Entity}` | `updateUser`, `updateCategory` |
| 删除 | `delete{Entity}` | `deleteUser`, `deleteArticle` |
| 查询单个 | `get{Entity}ById` | `getUserById`, `getArticleById` |
| 查询列表 | `getAll{Entities}` | `getAllUsers`, `getAllArticles` |
| 分页查询 | `get{Entities}` | `getUsers(Pageable)`, `getArticles(Pageable)` |
| 条件查询 | `get{Entity}By{Condition}` | `getUserByEmail`, `getCategoryBySlug` |
| 判断存在 | `existsBy{Condition}` | `existsByEmail`, `existsBySlug` |
| 工具方法 | `generate{Thing}From{Source}` | `generateSlugFromTitle` |

### 4. 变量命名 (camelCase)

```java
// ✅ 正确
private String userId;
private ArticleRepository articleRepository;
private CreateUserRequest request;

// ❌ 错误
private String user_id;
private String UserID;
```

### 5. 常量命名 (UPPER_SNAKE_CASE)

```java
// ✅ 正确
public static final String OTP_PREFIX = "otp:";
public static final String OTP_KEY_FORMAT = "otp:%s";
private static final long serialVersionUID = -6249791470254664710L;

// ❌ 错误
public static final String otpPrefix = "otp:";
```

---

## 编码规范

### 1. 实体类 (Entity) 规范

#### 必须包含的元素

```java
@Table(name = "user")                              // 1. 表名映射
@Entity                                            // 2. JPA实体标记
@Setter                                            // 3. Lombok Setter
@Getter                                            // 4. Lombok Getter
@EntityListeners(AuditingEntityListener.class)     // 5. 审计监听器
public class User implements Serializable {

    // 6. 序列化版本号（必须）
    private static final long serialVersionUID = -6249791470254664710L;

    // 7. 主键（UUID策略）
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uid", nullable = false, length = 36, updatable = false)
    private String uid;

    // 8. 业务字段
    @Column(name = "username", nullable = false, length = 50, unique = true)
    private String username;

    // 9. 枚举类型
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status = UserStatus.PENDING;

    // 10. 审计字段
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 11. 关联关系
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "uid", referencedColumnName = "uid"),
        inverseJoinColumns = @JoinColumn(name = "rid", referencedColumnName = "rid")
    )
    private Set<Role> roles;
}
```

#### 字段规范

1. **主键字段**:
   - ✅ 必须使用 `GenerationType.UUID`
   - ✅ 字段名格式: `{表名首字母}id` (如 `uid`, `aid`, `cid`)
   - ✅ 长度: 36
   - ✅ 不可更新: `updatable = false`

2. **字符串字段**:
   - ✅ 必须指定 `length`
   - ✅ 必须指定 `nullable`
   - ✅ 唯一字段加 `unique = true`

3. **枚举字段**:
   - ✅ 必须使用 `@Enumerated(EnumType.STRING)`
   - ❌ 禁止使用 `EnumType.ORDINAL`

4. **时间字段**:
   - ✅ 类型: `LocalDateTime`
   - ✅ 创建时间: `@CreatedDate` + `updatable = false`
   - ✅ 更新时间: `@LastModifiedDate`

5. **关联关系**:
   - ✅ 多对多: `@ManyToMany` + `@JoinTable`
   - ✅ 延迟加载: 多用 `FetchType.LAZY`
   - ✅ 级联操作: `{CascadeType.PERSIST, CascadeType.MERGE}`

---

### 2. Service 层规范

#### 接口定义

```java
package com.sunrizon.horizon.service;

public interface IArticleService {

    /**
     * 创建文章
     *
     * @param request 文章创建请求
     * @return 包含创建的文章VO的响应
     */
    ResultResponse<ArticleVO> createArticle(CreateArticleRequest request);

    ResultResponse<ArticleVO> getArticleById(String id);
    
    ResultResponse<Page<ArticleVO>> getArticles(Pageable pageable);
}
```

#### 实现类规范

```java
/**
 * Implementation of IArticleService.
 * <p>
 * Handles article management operations.
 */
@Service
@Slf4j
public class ArticleServiceImpl implements IArticleService {

    @Resource
    private ArticleRepository articleRepository;

    /**
     * 创建新文章
     *
     * @param request 文章创建请求
     * @return {@link ResultResponse} 包含创建的 {@link ArticleVO}
     */
    @Override
    @Transactional
    public ResultResponse<ArticleVO> createArticle(CreateArticleRequest request) {
        // 1. 参数验证
        if (StrUtil.isNotBlank(request.getSlug()) && articleRepository.existsBySlug(request.getSlug())) {
            return ResultResponse.error(ResponseCode.ARTICLE_SLUG_EXISTS);
        }

        // 2. DTO 转 Entity (使用 BeanUtil)
        Article article = BeanUtil.copyProperties(request, Article.class);

        // 3. 业务逻辑处理
        if (StrUtil.isBlank(article.getSlug())) {
            article.setSlug(generateSlugFromTitle(request.getTitle()));
        }

        // 4. 保存数据
        Article savedArticle = articleRepository.save(article);

        // 5. Entity 转 VO
        ArticleVO articleVO = BeanUtil.copyProperties(savedArticle, ArticleVO.class);

        // 6. 返回统一响应
        return ResultResponse.success(ResponseCode.SUCCESS, articleVO);
    }
}
```

#### Service 编码要点

1. **方法结构**: `验证参数 → DTO转Entity → 业务逻辑 → 保存数据 → Entity转VO → 返回响应`
2. **使用 Hutool**:
   - `BeanUtil.copyProperties()` - 对象转换
   - `StrUtil.isBlank()` / `StrUtil.isNotBlank()` - 字符串判断
   - `StrUtil.equals()` - 字符串比较
3. **事务管理**: 写操作必须加 `@Transactional`
4. **异常处理**: 使用 `orElseThrow()` 或返回 `ResultResponse.error()`

---

### 3. Controller 层规范

```java
@RestController
@RequestMapping("/api/article")
@Slf4j
public class ArticleController {

    @Resource
    private IArticleService articleService;

    @PostMapping("/create")
    public ResultResponse<ArticleVO> createArticle(@Valid @RequestBody CreateArticleRequest request) {
        return articleService.createArticle(request);
    }

    @GetMapping("/{id}")
    public ResultResponse<ArticleVO> getArticleById(@PathVariable("id") String id) {
        return articleService.getArticleById(id);
    }

    @GetMapping
    public ResultResponse<Page<ArticleVO>> getArticles(Pageable pageable) {
        return articleService.getArticles(pageable);
    }
}
```

#### URL 映射规范

| 操作 | HTTP方法 | URL模板 | 示例 |
|------|----------|---------|------|
| 创建 | POST | `/api/{module}/create` | `POST /api/article/create` |
| 查询单个 | GET | `/api/{module}/{id}` | `GET /api/article/123` |
| 查询列表（分页） | GET | `/api/{module}` | `GET /api/article?page=0&size=10` |
| 查询全部 | GET | `/api/{module}/all` | `GET /api/article/all` |
| 更新 | PUT | `/api/{module}/{id}` | `PUT /api/article/123` |
| 删除 | DELETE | `/api/{module}/{id}` | `DELETE /api/article/123` |

---

### 4. Repository 层规范

```java
@Repository
public interface ArticleRepository extends JpaRepository<Article, String> {
    
    // 方法命名查询
    List<Article> findByStatus(ArticleStatus status);
    Page<Article> findByAuthorId(String authorId, Pageable pageable);
    boolean existsBySlug(String slug);
    
    // JPQL 查询
    @Query("SELECT a FROM Article a WHERE a.title LIKE %:keyword%")
    Page<Article> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
```

#### Repository 方法命名

- `findBy{字段名}` - 查询
- `existsBy{字段名}` - 判断存在
- `countBy{字段名}` - 统计数量
- `deleteBy{字段名}` - 删除

---

### 5. DTO/VO 规范

#### DTO (Request)

```java
@Data
public class CreateArticleRequest {

    @NotBlank(message = "文章标题不能为空")
    @Size(max = 100, message = "文章标题不能超过100个字符")
    private String title;

    @NotBlank(message = "文章内容不能为空")
    private String content;

    private String coverImage;
    private ArticleStatus status = ArticleStatus.DRAFT;
}
```

#### VO (Response)

```java
@Data
public class ArticleVO {
    private String aid;
    private String title;
    private String content;
    private ArticleStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

---

### 6. 枚举类规范

#### 简单枚举

```java
public enum ArticleStatus {
    DRAFT,      // 草稿
    PUBLISHED,  // 已发布
    ARCHIVED    // 已归档
}
```

#### 带逻辑的枚举

```java
public enum UserStatus {
    ACTIVE, INACTIVE, PENDING, BANNED, DELETED;

    public Optional<String> transitionTo(UserStatus targetStatus) {
        switch (this) {
            case PENDING:
                if (targetStatus == ACTIVE || targetStatus == BANNED) {
                    return Optional.empty();
                }
                return Optional.of("待审核用户只能激活或禁用");
            // ...
        }
    }
}
```

#### 带字段的枚举

```java
@Getter
public enum ResponseCode {
    SUCCESS(200, "请求成功"),
    BAD_REQUEST(400, "请求参数错误"),
    USER_NOT_FOUND(1001, "用户不存在");

    private final int status;
    private final String message;

    ResponseCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
```

---

### 7. 常量类规范

```java
/**
 * Redis键常量
 */
public class RedisConstants {

    // OTP 验证常量
    public static final String OTP_PREFIX = "otp:";
    public static final String OTP_KEY_FORMAT = "otp:%s"; // %s = email

    // 用户会话常量
    public static final String USER_SESSION_PREFIX = "session:";
    public static final String USER_SESSION_KEY_FORMAT = "session:%s"; // %s = userId
}
```

---

## 开发流程

### 新功能开发标准流程

#### 1️⃣ 需求分析
- 查阅 `ROADMAP.md` 确认功能优先级
- 创建 GitHub Issue

#### 2️⃣ 数据库设计
- 定义实体类 (Entity)
- 启动项目，JPA 自动生成表
- 检查数据库表结构

#### 3️⃣ 创建基础结构（按顺序）

```
1. pojo/       → 实体类
2. enums/      → 枚举（状态、类型）
3. dto/        → 请求DTO
4. vo/         → 响应VO
5. repository/ → Repository接口
6. service/    → Service接口 + 实现类
7. controller/ → Controller类
```

#### 4️⃣ 实现业务逻辑
- Repository 方法定义
- Service 实现
- Controller 实现

#### 5️⃣ 测试验证
- 使用 Swagger 测试 API
- 验证数据库数据

#### 6️⃣ 自测完成（必须）
- **完成功能自测**（详见[自测规范](#自测规范)）
- 验证所有 API 接口
- 检查数据库表结构
- 测试异常场景

#### 7️⃣ 提交代码
- Git 提交（遵循提交规范）
- 创建 Pull Request

---

### 示例：实现评论功能

#### Step 1: 创建实体类

```java
// pojo/Comment.java
@Table(name = "comment")
@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Comment implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "cid", nullable = false, length = 36, updatable = false)
    private String cid;

    @Column(name = "article_id", nullable = false, length = 36)
    private String articleId;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "parent_id", length = 36)
    private String parentId;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

#### Step 2: 创建 DTO/VO

```java
// dto/CreateCommentRequest.java
@Data
public class CreateCommentRequest {
    @NotBlank(message = "文章ID不能为空")
    private String articleId;

    @NotBlank(message = "评论内容不能为空")
    private String content;

    private String parentId; // 父评论ID（可选）
}

// vo/CommentVO.java
@Data
public class CommentVO {
    private String cid;
    private String articleId;
    private String userId;
    private String content;
    private String parentId;
    private LocalDateTime createdAt;
}
```

#### Step 3: 创建 Repository

```java
// repository/CommentRepository.java
@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    List<Comment> findByArticleId(String articleId);
    Page<Comment> findByArticleId(String articleId, Pageable pageable);
}
```

#### Step 4: 创建 Service

```java
// service/ICommentService.java
public interface ICommentService {
    ResultResponse<CommentVO> createComment(CreateCommentRequest request);
    ResultResponse<List<CommentVO>> getCommentsByArticleId(String articleId);
}

// service/impl/CommentServiceImpl.java
@Service
@Slf4j
public class CommentServiceImpl implements ICommentService {

    @Resource
    private CommentRepository commentRepository;

    @Override
    @Transactional
    public ResultResponse<CommentVO> createComment(CreateCommentRequest request) {
        Comment comment = BeanUtil.copyProperties(request, Comment.class);
        Comment saved = commentRepository.save(comment);
        CommentVO vo = BeanUtil.copyProperties(saved, CommentVO.class);
        return ResultResponse.success(vo);
    }

    @Override
    public ResultResponse<List<CommentVO>> getCommentsByArticleId(String articleId) {
        List<Comment> comments = commentRepository.findByArticleId(articleId);
        List<CommentVO> vos = comments.stream()
            .map(c -> BeanUtil.copyProperties(c, CommentVO.class))
            .collect(Collectors.toList());
        return ResultResponse.success(vos);
    }
}
```

#### Step 5: 创建 Controller

```java
// controller/CommentController.java
@RestController
@RequestMapping("/api/comment")
@Slf4j
public class CommentController {

    @Resource
    private ICommentService commentService;

    @PostMapping("/create")
    public ResultResponse<CommentVO> createComment(@Valid @RequestBody CreateCommentRequest request) {
        return commentService.createComment(request);
    }

    @GetMapping("/article/{articleId}")
    public ResultResponse<List<CommentVO>> getCommentsByArticleId(@PathVariable String articleId) {
        return commentService.getCommentsByArticleId(articleId);
    }
}
```

---

## 自测规范

### ⚠️ 关键原则

> **每个功能开发完成后，开发者必须进行完整的自测,确保功能正常工作后才能提交代码。**

---

### 📋 自测流程

#### 0. 用户认证（需要时）

对于需要用户认证的接口，先获取 JWT Token：

```bash
# 用户登录获取 Token
curl -X POST "http://localhost:8080/api/user/authenticate" \
  -H "Content-Type: application/json" \
  -d '{"email":"3499508634@qq.com","password":"Wei.Li.Laba01"}' | jq .

# 响应示例
{
  "data": {
    "authorization": "eyJhbGciOiJIUzI1NiJ9...",
    "uid": "98f3d225-e397-490e-9918-b767e0119f65",
    "email": "3499508634@qq.com",
    "username": "wei.li.updated"
  },
  "status": 10012,
  "message": "Login successfully"
}

# 使用 Token 调用需要认证的接口
curl -X POST "http://localhost:8080/api/interaction/like" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -d '{"articleId":"xxx"}' | jq .
```

**测试账号**：
- Email: `3499508634@qq.com`
- Password: `Wei.Li.Laba01`
- User ID: `98f3d225-e397-490e-9918-b767e0119f65`

**获取 Token 的标准命令**：
```bash
curl -X POST "http://localhost:8080/api/user/authenticate" \
  -H "Content-Type: application/json" \
  -d '{"email":"3499508634@qq.com","password":"Wei.Li.Laba01"}' | jq .
```

#### 1. 启动项目

```bash
mvn spring-boot:run
```

**检查**：
- ✅ 项目启动成功，无错误日志
- ✅ JPA 自动创建数据库表成功

#### 2. 验证数据库表

```sql
SHOW TABLES;  -- 查看新增的表
DESC 表名;    -- 检查表结构
```

**检查**：
- ✅ 主键类型为 VARCHAR(36)
- ✅ 枚举字段为 VARCHAR（非 INT）

#### 3. 测试 API 接口

访问 Swagger：`http://localhost:8080/swagger-ui.html`

**正常场景**：
- ✅ 创建操作 (POST)
- ✅ 查询操作 (GET)
- ✅ 更新操作 (PUT)
- ✅ 删除操作 (DELETE)
- ✅ 分页查询

**异常场景**：
- ✅ 参数缺失或无效
- ✅ 资源不存在 (404)
- ✅ 重复创建（slug/名称冲突）

#### 4. 数据库验证

```sql
-- 查看最新记录
SELECT * FROM 表名 ORDER BY created_at DESC LIMIT 5;

-- 验证时间自动生成
SELECT created_at, updated_at FROM 表名;
```

---

### ✅ 自测完成标准

**在提交代码前，确认**：

- [ ] 项目启动成功
- [ ] 数据库表结构正确
- [ ] CRUD 操作全部测试通过
- [ ] 至少 3 个异常场景测试通过
- [ ] 数据库数据存储正确
- [ ] 分页功能正常
- [ ] 日志无异常

---

## 实体类关系开发流程

### 📖 关系类型说明

实体类之间的关系分为三种：**一对一**、**一对多**、**多对多**。根据业务场景选择合适的关系类型。

---

### 1️⃣ 一对一关系 (One-to-One)

**场景示例**: 用户 (User) 和 用户详情 (UserProfile)

#### 特点
- 一个 User 对应一个 UserProfile
- 一个 UserProfile 只属于一个 User

#### 实现方式

**方式一：简单字段引用（推荐）**

```java
// UserProfile.java
@Table(name = "user_profile")
@Entity
@Setter
@Getter
public class UserProfile implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "profile_id", nullable = false, length = 36)
    private String profileId;

    // 简单字段引用 User
    @Column(name = "user_id", nullable = false, length = 36, unique = true)
    private String userId;

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;
}
```

**方式二：JPA 关联（需要延迟加载时使用）**

```java
// UserProfile.java
@Table(name = "user_profile")
@Entity
@Setter
@Getter
public class UserProfile implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "profile_id", nullable = false, length = 36)
    private String profileId;

    // JPA 一对一关联
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "uid")
    private User user;

    @Column(name = "bio", length = 500)
    private String bio;
}
```

**何时选择**:
- ✅ **推荐简单字段引用**: 性能更好，避免 N+1 查询问题
- ⚠️ **使用 JPA 关联**: 需要级联操作或延迟加载时

---

### 2️⃣ 一对多关系 (One-to-Many)

**场景示例**: 文章 (Article) 和 评论 (Comment)

#### 特点
- 一个 Article 可以有多个 Comment
- 一个 Comment 只属于一个 Article

#### 实现方式

**方式一：简单字段引用（推荐）**

参考 [`Comment`](file:///Users/wei.li/Developer/blog/horizon/src/main/java/com/sunrizon/horizon/pojo/Comment.java) 和 [`Interaction`](file:///Users/wei.li/Developer/blog/horizon/src/main/java/com/sunrizon/horizon/pojo/Interaction.java) 的设计：

```java
// Comment.java (多方)
@Table(name = "comment")
@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Comment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "cid", nullable = false, length = 36)
    private String cid;

    // 简单字段引用 Article
    @Column(name = "article_id", nullable = false, length = 36)
    private String articleId;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}

// Article.java (一方) - 不需要定义反向引用
@Table(name = "article")
@Entity
@Setter
@Getter
public class Article implements Serializable {
    
    @Id
    @Column(name = "aid", nullable = false, length = 36)
    private String aid;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    // 不需要定义 List<Comment> comments
    // 通过 CommentRepository.findByArticleId(articleId) 查询
}
```

**Repository 查询**:

```java
@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    List<Comment> findByArticleId(String articleId);
    Page<Comment> findByArticleId(String articleId, Pageable pageable);
    long countByArticleId(String articleId);
}
```

**方式二：JPA 双向关联（不推荐）**

```java
// Comment.java (多方)
@Table(name = "comment")
@Entity
public class Comment {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", referencedColumnName = "aid")
    private Article article;
}

// Article.java (一方)
@Entity
public class Article {
    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY)
    private List<Comment> comments;
}
```

**何时选择**:
- ✅ **推荐简单字段引用**: 避免循环依赖、N+1 查询、JSON 序列化问题
- ❌ **避免 JPA 双向关联**: 容易出现性能问题和维护困难

**最佳实践**:
1. 在 **多方** (Comment) 存储 **一方** (Article) 的 ID
2. 在 Article 实体中添加统计字段（如 `commentCount`）
3. 通过 Repository 方法查询关联数据

---

### 3️⃣ 多对多关系 (Many-to-Many)

**场景示例**: 文章 (Article) 和 标签 (Tag)

#### 特点
- 一个 Article 可以有多个 Tag
- 一个 Tag 可以属于多个 Article
- 需要中间表 `article_tag`

#### 实现方式

**方式一：JPA 关联 + 中间表（推荐）**

参考 [`Article`](file:///Users/wei.li/Developer/blog/horizon/src/main/java/com/sunrizon/horizon/pojo/Article.java) 和 [`Tag`](file:///Users/wei.li/Developer/blog/horizon/src/main/java/com/sunrizon/horizon/pojo/Tag.java) 的设计：

```java
// Article.java (主控方)
@Table(name = "article")
@Entity
@Setter
@Getter
public class Article implements Serializable {

    @Id
    @Column(name = "aid", nullable = false, length = 36)
    private String aid;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    // 多对多关联
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "article_tag",                              // 中间表名
        joinColumns = @JoinColumn(name = "aid", referencedColumnName = "aid"),
        inverseJoinColumns = @JoinColumn(name = "tid", referencedColumnName = "tid")
    )
    private Set<Tag> tags;
}

// Tag.java (被控方)
@Table(name = "tag")
@Entity
@Setter
@Getter
public class Tag implements Serializable {

    @Id
    @Column(name = "tid", nullable = false, length = 36)
    private String tid;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    // 反向引用（可选）
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<Article> articles;
}
```

**中间表结构**（JPA 自动生成）:

```sql
CREATE TABLE article_tag (
    aid VARCHAR(36) NOT NULL,
    tid VARCHAR(36) NOT NULL,
    PRIMARY KEY (aid, tid),
    FOREIGN KEY (aid) REFERENCES article(aid),
    FOREIGN KEY (tid) REFERENCES tag(tid)
);
```

**方式二：中间实体类（需要额外字段时）**

当中间表需要额外字段（如创建时间、排序）时：

```java
// ArticleTag.java (中间实体)
@Table(name = "article_tag")
@Entity
@Setter
@Getter
public class ArticleTag implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "article_id", nullable = false, length = 36)
    private String articleId;

    @Column(name = "tag_id", nullable = false, length = 36)
    private String tagId;

    // 额外字段
    @Column(name = "sort_order")
    private Integer sortOrder;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
```

**何时选择**:
- ✅ **使用 JPA @ManyToMany**: 中间表只有两个外键，无额外字段
- ✅ **使用中间实体类**: 中间表需要额外字段（时间、排序、状态等）

**最佳实践**:
1. 使用 `Set` 而非 `List`（避免重复）
2. 使用 `FetchType.LAZY`（延迟加载）
3. 级联操作选择 `{CascadeType.PERSIST, CascadeType.MERGE}`
4. 避免使用 `CascadeType.REMOVE`（防止误删）

---

### 📊 关系类型选择流程图

```
开始
  |
  v
需要关联吗?
  ├─ 否 → 独立实体（无关联）
  └─ 是
      |
      v
  一个实体对应几个?
      |
      ├─ 一个 → 一对一
      |         |
      |         ├─ 简单场景 → 简单字段引用
      |         └─ 需要级联 → @OneToOne
      |
      ├─ 多个 → 一对多
      |         |
      |         ├─ 推荐 → 简单字段引用 (多方存一方ID)
      |         └─ 特殊需求 → @ManyToOne + @OneToMany
      |
      └─ 多对多 → 多对多
                |
                ├─ 中间表无额外字段 → @ManyToMany + @JoinTable
                └─ 中间表有额外字段 → 中间实体类
```

---

### ⚡ 性能优化建议

1. **优先使用简单字段引用**:
   - ✅ 避免 N+1 查询问题
   - ✅ 减少 JOIN 复杂度
   - ✅ 更容易优化查询

2. **使用延迟加载**:
   ```java
   @ManyToMany(fetch = FetchType.LAZY)  // ✅ 推荐
   @ManyToMany(fetch = FetchType.EAGER) // ❌ 避免
   ```

3. **添加统计字段**:
   ```java
   // Article.java
   @Column(name = "comment_count", nullable = false)
   private Long commentCount = 0L;  // 冗余字段，提升查询性能
   ```

4. **使用 DTO/VO 投影查询**:
   ```java
   @Query("SELECT new com.sunrizon.horizon.vo.ArticleVO(a.aid, a.title) FROM Article a")
   List<ArticleVO> findAllSimple();
   ```

---

### 📝 开发检查清单

**一对一关系**:
- [ ] 选择了合适的实现方式（简单字段 vs JPA 关联）
- [ ] 添加了 `unique = true` 约束
- [ ] 考虑了级联删除策略

**一对多关系**:
- [ ] 在多方存储了一方的 ID
- [ ] 创建了 Repository 查询方法
- [ ] 在一方添加了统计字段（可选）
- [ ] 避免了循环依赖

**多对多关系**:
- [ ] 确定了主控方和被控方
- [ ] 定义了中间表名称和列名
- [ ] 使用了 `Set` 而非 `List`
- [ ] 设置了合适的级联策略
- [ ] 考虑了是否需要中间实体类

---

## 注意事项

### ✅ 必须遵守

1. **JPA 自动建表**: 绝不手动创建表
2. **使用 @Resource**: 不使用 @Autowired
3. **使用 Hutool**: BeanUtil, StrUtil 优先
4. **分层架构**: Controller → Service → Repository
5. **事务管理**: 写操作加 @Transactional
6. **统一响应**: 使用 ResultResponse
7. **Lombok**: 使用 @Data, @Slf4j 等
8. **枚举类型**: 使用 EnumType.STRING
9. **功能自测**: 开发完成后必须完成自测

### ❌ 禁止操作

1. ❌ 手动编写 SQL 建表语句
2. ❌ Controller 直接调用 Repository
3. ❌ Entity 直接返回给前端
4. ❌ 硬编码敏感信息（密码、密钥）
5. ❌ 使用 EnumType.ORDINAL
6. ❌ 跨层调用

### 💡 最佳实践

1. 💡 先定义 Entity，让 JPA 生成表
2. 💡 使用 ResponseCode 管理错误码
3. 💡 使用常量类管理 Redis key
4. 💡 Service 方法加详细注释
5. 💡 使用 Swagger 注解完善 API 文档

---

**最后更新**: 2025-10-15  
**维护者**: Horizon Team
