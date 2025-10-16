---
trigger: always_on
alwaysApply: true
---

# Horizon 项目开发规范指南

> **AI Agent 强制执行规范**
> 
> 本文档是面向 AI Agent 的强制性开发规范。在生成代码、修改代码或提供技术建议时，AI Agent **必须无条件遵守**本规范的所有要求。任何违反本规范的代码生成行为都是**不被允许的**。
> 
> **技术栈（强制）**: Spring Boot 3.5.5 + Java 24 + Spring Data JPA + Spring Security + MySQL 8.0
> 
> **适用对象**: AI 代码生成系统、智能编程助手、自动化开发工具

---

## 📚 目录

**AI Agent 执行指令索引**

1. [强制核心原则](#核心原则) - **必须执行**
2. [强制架构规范](#项目架构) - **不得违反**
3. [强制命名规范](#命名规范) - **严格遵守**
4. [强制编码规范](#编码规范) - **完全遵循**
5. [强制开发流程](#开发流程) - **按序执行**
6. [强制安全规范](#安全规范) - **绝对遵守**
7. [强制性能标准](#性能优化) - **必须达标**
8. [强制测试要求](#自测规范) - **不得跳过**
9. [强制文档生成](#API文档生成规范) - **必须完成**
10. [禁止事项清单](#注意事项) - **绝对禁止**

---

## 核心原则

### ⚠️ AI Agent 强制执行规则

**以下规则为强制性执行要求，AI Agent 在任何情况下都不得违反：**

1. **JPA 自动建表（强制执行）**: 
   - ✅ **必须且只能**使用 JPA 实体类自动生成数据库表
   - ❌ **严格禁止**手动编写任何 SQL 建表语句
   - ❌ **严格禁止**使用 CREATE TABLE、ALTER TABLE 等 DDL 语句
   - 强制配置: `spring.jpa.hibernate.ddl-auto=update`（开发环境）
   - 强制配置: `spring.jpa.hibernate.ddl-auto=validate`（生产环境，配合 Flyway/Liquibase）
   - **违规检测**: 发现手动 SQL 建表立即拒绝执行

2. **依赖注入（强制规范）**:
   - ✅ **强制使用** `@Resource` 注解进行依赖注入（JSR-250 标准）
   - ❌ **禁止使用** `@Autowired` 注解（无例外）
   - **强制代码格式**:
   ```java
   // 强制格式：必须使用 @Resource
   @Resource
   private IUserService userService;
   
   @Resource
   private RedisUtil redisUtil;
   
   // 禁止格式：不得使用 @Autowired
   // @Autowired  ← 此代码将被拒绝
   ```
   - **违规处理**: 发现 @Autowired 立即替换为 @Resource

3. **工具库（强制标准）**:
   - ✅ **必须且只能**使用 Hutool 工具类（`StrUtil`, `BeanUtil`, `RandomUtil`, `Validator`）
   - ❌ **严格禁止**使用 Apache Commons Lang、Commons Collections 等库
   - ❌ **严格禁止**使用 Guava 的字符串和集合工具方法
   - **强制映射规则**:
     - 字符串操作 → **必须**使用 `StrUtil`
     - 对象拷贝 → **必须**使用 `BeanUtil.copyProperties()`
     - 随机数生成 → **必须**使用 `RandomUtil`
     - 参数验证 → **必须**使用 `Validator`
   - **违规处理**: 发现其他工具库立即替换为 Hutool

4. **分层架构（强制执行）**:
   - ✅ **强制遵守三层架构**: Controller → Service → Repository
   - ❌ **绝对禁止**跨层调用（如 Controller 直接调用 Repository）
   - ❌ **绝对禁止**反向调用（如 Service 调用 Controller）
   - ✅ **强制执行单一职责原则**（SRP）：每层只能做其职责范围内的事
   - **违规检测规则**:
     - Controller 中出现 Repository 注入 → **立即拒绝**
     - Repository 中出现业务逻辑 → **立即拒绝**
     - Service 中操作 HttpServletRequest/Response → **立即拒绝**

5. **事务管理（强制要求）**:
   - ✅ **强制要求**：Service 层所有写操作（INSERT/UPDATE/DELETE）方法必须添加 `@Transactional`
   - ✅ **强制格式**：`@Transactional(rollbackFor = Exception.class)`
   - ❌ **绝对禁止**在 Controller 层使用 `@Transactional` 注解
   - ❌ **绝对禁止**在 Repository 层使用 `@Transactional` 注解
   - **强制检测**:
     - create*/update*/delete* 方法缺少 @Transactional → **立即添加**
     - Controller 出现 @Transactional → **立即移除**

6. **API 响应规范（强制统一）**:
   - ✅ **强制使用** `ResultResponse<T>` 包装所有 API 返回结果
   - ✅ **强制使用** `ResponseCode` 枚举管理所有错误码和成功码
   - ❌ **绝对禁止**直接返回实体类（Entity）给前端
   - ❌ **绝对禁止**返回裸对象或 Map 结构
   - ❌ **绝对禁止**自定义响应格式
   - **强制格式**:
     ```java
     // 唯一允许的返回格式
     public ResultResponse<UserVO> createUser(...) {
         return ResultResponse.success(ResponseCode.USER_CREATED, userVO);
     }
     ```
   - **违规处理**: 发现非 ResultResponse 返回类型立即拒绝

---

## 项目架构

### 标准目录结构

```
com.sunrizon.horizon
├── config/          # 配置类（Spring 配置、安全配置、中间件配置）
│   ├── SecurityConfig.java         # Spring Security 配置
│   ├── RedisConfig.java            # Redis 配置
│   ├── RabbitConfig.java           # RabbitMQ 配置
│   ├── OpenApiConfig.java          # Swagger/OpenAPI 配置
│   └── WebConfig.java              # Web MVC 配置（CORS、拦截器等）
├── constants/       # 常量类（集中管理常量）
│   ├── RedisConstants.java         # Redis 键前缀常量
│   └── RabbitConstants.java        # RabbitMQ 队列/交换机常量
├── controller/      # 控制器层（接收请求、返回响应）
│   ├── UserController.java         # 用户管理 API
│   ├── ArticleController.java      # 文章管理 API
│   └── CategoryController.java     # 分类管理 API
├── dto/            # 数据传输对象（请求/响应包装）
│   ├── CreateUserRequest.java      # 创建用户请求
│   ├── UpdateArticleRequest.java   # 更新文章请求
│   └── LoginUserRequest.java       # 登录请求
├── enums/          # 枚举类（状态、类型等）
│   ├── UserStatus.java             # 用户状态（ACTIVE, INACTIVE, PENDING）
│   ├── ArticleStatus.java          # 文章状态（DRAFT, PUBLISHED）
│   └── ResponseCode.java           # API 响应码
├── exception/      # 异常处理（全局异常处理器、自定义异常）
│   ├── GlobalExceptionHandler.java # 全局异常拦截器
│   ├── BusinessException.java      # 业务异常
│   └── UsernameNotFoundException.java
├── filter/         # 过滤器（请求/响应拦截处理）
│   ├── JwtAuthenticationFilter.java    # JWT 认证过滤器
│   ├── XssFilter.java                  # XSS 防护过滤器
│   └── CustomAccessDeniedHandler.java  # 访问拒绝处理器
├── messaging/      # 消息队列监听器（RabbitMQ 消费者）
│   ├── OtpListener.java            # OTP 验证码监听器
│   └── UserAuditListener.java      # 用户审核监听器
├── pojo/           # 实体类（JPA Entity，映射数据库表）
│   ├── User.java                   # 用户实体
│   ├── Article.java                # 文章实体
│   ├── Category.java               # 分类实体
│   └── Role.java                   # 角色实体
├── repository/     # 数据访问层（JPA Repository接口）
│   ├── UserRepository.java         # 用户数据访问
│   ├── ArticleRepository.java      # 文章数据访问
│   └── CategoryRepository.java     # 分类数据访问
├── service/        # 业务逻辑层
│   ├── IUserService.java           # 用户服务接口
│   ├── IArticleService.java        # 文章服务接口
│   └── impl/                       # 业务实现类
│       ├── UserServiceImpl.java
│       └── ArticleServiceImpl.java
├── utils/          # 工具类（通用工具方法）
│   ├── JwtUtil.java                # JWT 工具类
│   ├── RedisUtil.java              # Redis 工具类
│   ├── ResultResponse.java         # 统一响应包装类
│   └── SecurityContextUtil.java    # 安全上下文工具
└── vo/             # 视图对象（返回给前端的数据）
    ├── UserVO.java                 # 用户视图对象
    ├── ArticleVO.java              # 文章视图对象
    └── AuthVO.java                 # 认证响应对象
```

### 层级职责（强制执行标准）

**AI Agent 必须严格按照下表执行，不得有任何偏离：**

| 层级 | 强制职责 | 强制允许操作 | 绝对禁止操作 |
|------|------|----------|----------|
| **Controller** | 接收请求、参数验证、调用Service、返回ResultResponse | • **必须**使用 `@Valid` 验证<br>• **必须**调用 Service 方法<br>• **必须**返回 `ResultResponse` | ❌ **禁止**编写业务逻辑<br>❌ **禁止**直接调用 Repository<br>❌ **禁止**直接返回 Entity<br>❌ **禁止**使用 @Transactional |
| **Service** | 业务逻辑处理、事务管理、数据转换 | • **必须**调用 Repository<br>• **必须**进行 DTO ⇔ Entity 转换<br>• **必须**进行 Entity ⇔ VO 转换<br>• **必须**执行业务逻辑校验<br>• **必须**管理事务 | ❌ **禁止**操作 Request/Response<br>❌ **禁止**跨 Service 调用其他 Repository<br>❌ **禁止**直接返回 Entity |
| **Repository** | 数据访问、查询定义 | • **必须**继承 `JpaRepository`<br>• **允许**自定义查询方法<br>• **允许**使用 JPQL/原生 SQL | ❌ **禁止**包含业务逻辑<br>❌ **禁止**调用其他 Repository<br>❌ **禁止**使用 @Transactional |

### 层级调用规则（强制执行）

**以下调用链为唯一允许的调用方式：**

```
Controller (必须这里开始)
    ↓ 必须调用
  Service (不得跨过)
    ↓ 必须调用
 Repository (不得跨过)
    ↓ 必须访问
  Database (终点)
```

**强制执行原则**:
1. ✅ **强制要求**：单向依赖，上层只能调用直接下层，下层绝对不能调用上层
2. ❌ **绝对禁止**：跨层调用（例：Controller 直接调用 Repository）
3. ❌ **绝对禁止**：反向调用（例：Repository 调用 Service）
4. ✅ **允许但需谨慎**：Service 之间调用，但必须防止循环依赖

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

**AI Agent 必须严格遵守以下编码规范，不得有任何偏离：**

### 1. 实体类 (Entity) 规范（强制标准）

#### 强制包含的元素

**以下元素为强制要求，缺一不可：**

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

#### 字段规范（强制执行）

**AI Agent 必须按照以下规则处理每个字段：**

1. **主键字段（强制规范）**:
   - ✅ **必须且只能**使用 `GenerationType.UUID`
   - ❌ **绝对禁止**使用 AUTO、IDENTITY、SEQUENCE 等其他策略
   - ✅ **强制命名格式**: `{entity_prefix}id` (如 `uid`, `aid`, `cid`)
   - ✅ **强制长度**: 36 字符
   - ✅ **强制属性**: `updatable = false`
   - ✅ **强制属性**: `nullable = false`

2. **字符串字段（强制规范）**:
   - ✅ **必须**指定 `length` 属性（不得省略）
   - ✅ **必须**指定 `nullable` 属性（不得省略）
   - ✅ **必须**为唯一字段添加 `unique = true`
   - ❌ **禁止**使用默认值，必须明确声明

3. **枚举字段（强制规范）**:
   - ✅ **必须且只能**使用 `@Enumerated(EnumType.STRING)`
   - ❌ **绝对禁止**使用 `EnumType.ORDINAL`
   - ❌ **绝对禁止**省略 @Enumerated 注解
   - ✅ **强制要求**：必须指定 `length` 属性

4. **时间字段（强制规范）**:
   - ✅ **必须且只能**使用 `LocalDateTime` 类型
   - ❌ **绝对禁止**使用 Date、Timestamp 等类型
   - ✅ **强制要求**：创建时间必须使用 `@CreatedDate` + `updatable = false`
   - ✅ **强制要求**：更新时间必须使用 `@LastModifiedDate`

5. **关联关系（强制规范）**:
   - ✅ **强制要求**：多对多关系必须使用 `@ManyToMany` + `@JoinTable`
   - ✅ **强制要求**：必须使用 `FetchType.LAZY`（除非有明确理由）
   - ✅ **强制要求**：级联操作必须使用 `{CascadeType.PERSIST, CascadeType.MERGE}`
   - ❌ **绝对禁止**使用 `CascadeType.ALL` 或 `CascadeType.REMOVE`

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

## 安全规范

**AI Agent 必须无条件遵守以下安全规范，任何违反将被立即拒绝：**

### 🔒 认证与授权（强制执行）

#### 1. JWT 认证（强制配置）

**强制要求**:
- ✅ **必须**使用 HTTPS 传输 JWT Token（生产环境）
- ✅ **必须**设置 Token 过期时间为 2-24 小时
- ❌ **绝对禁止**在 URL 参数中传递 Token
- ❌ **绝对禁止**在 Cookie 中存储 Token（除非 HttpOnly + Secure）
- ❌ **绝对禁止**使用永久 Token
```java
// SecurityConfig.java
@Configuration
@EnableMethodSecurity  // 启用方法级安全
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)  // REST API 关闭 CSRF
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 无状态
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/user/authenticate", "/api/user/create").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll())
            .addFilterBefore(jwtAuthenticationFilter, 
                UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

**重要原则**:
1. ✅ **必须**使用 HTTPS 传输 JWT Token（生产环境）
2. ✅ Token 过期时间设置合理（建议 2-24 小时）
3. ✅ 敏感操作需要重新验证（如修改密码、删除账户）
4. ❌ **禁止**在 URL 参数中传递 Token

#### 2. 密码管理（强制规范）

**强制要求**:
- ✅ **必须且只能**使用 BCrypt 加密存储密码
- ❌ **绝对禁止**明文存储密码
- ❌ **绝对禁止**使用 MD5、SHA-1 等不安全算法
- ❌ **绝对禁止**在日志中输出密码（包括加密后的）
- ❌ **绝对禁止**在任何响应中返回密码

**密码强度强制规则**:
- ✅ **必须**最少 8 位字符
- ✅ **必须**包含字母和数字
- ✅ **建议**包含特殊字符

```java
// 密码加密：使用 BCrypt
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// Service 层使用
user.setPassword(passwordEncoder.encode(request.getPassword()));

// 密码验证
if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
    return ResultResponse.error(ResponseCode.OLD_PASSWORD_INCORRECT);
}
```

**密码强度规则**:
- ✅ 最少 8 位字符
- ✅ 包含字母和数字
- ✅ 建议包含特殊字符
- ❌ **禁止**明文存储密码
- ❌ **禁止**在日志中输出密码

#### 3. 权限控制

**方法级权限注解**:
```java
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    // 需要 ADMIN 角色
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultResponse<Page<UserVO>> getAllUsers(Pageable pageable) {
        return userService.getUsers(pageable);
    }
    
    // 需要特定权限
    @DeleteMapping("/user/{uid}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    public ResultResponse<String> deleteUser(@PathVariable String uid) {
        return userService.deleteUser(uid);
    }
}
```

**RBAC 模型**:
- User → Role → Permission
- 用户可以有多个角色
- 角色可以有多个权限

---

### 🛡️ 输入验证与 XSS 防护

#### 1. 参数验证

**DTO 验证注解**:
```java
@Data
public class CreateArticleRequest {

    @NotBlank(message = "文章标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100字符")
    private String title;

    @NotBlank(message = "文章内容不能为空")
    private String content;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug 只能包含小写字母、数字和连字符")
    private String slug;
}
```

**Controller 中启用验证**:
```java
@PostMapping("/create")
public ResultResponse<ArticleVO> createArticle(
    @Valid @RequestBody CreateArticleRequest request) {  // @Valid 启用验证
    return articleService.createArticle(request);
}
```

#### 2. XSS 防护

**使用 Jsoup 清理输入**:
```java
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class XssFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                         FilterChain chain) throws IOException, ServletException {
        // 清理 HTML 标签
        String cleanContent = Jsoup.clean(dirtyContent, Safelist.basic());
        chain.doFilter(new XssRequestWrapper(request), response);
    }
}
```

**防护要点**:
- ✅ 所有用户输入必须验证
- ✅ 输出时进行 HTML 转义
- ✅ 使用 CSP (Content Security Policy) 头
- ❌ **禁止**直接将用户输入渲染为 HTML

---

### 🔐 SQL 注入防护

**使用 JPA 已经自带防护**:
```java
// ✅ 安全：JPA 方法命名查询
List<User> findByEmail(String email);

// ✅ 安全：参数化 JPQL
@Query("SELECT u FROM User u WHERE u.email = :email")
User findUserByEmail(@Param("email") String email);

// ❌ 危险：字符串拼接 SQL（绝对禁止）
String sql = "SELECT * FROM user WHERE email = '" + email + "'";
```

---

### 🔑 敏感信息管理

#### 1. 配置文件加密

**使用 Jasypt 加密敏感配置**:
```yaml
# application.yml
spring:
  datasource:
    password: ENC(encrypted_password_here)  # 加密后的密码

jasypt:
  encryptor:
    password: ${JASYPT_ENCRYPTOR_PASSWORD}  # 从环境变量读取
```

#### 2. 日志脱敏

```java
// ❌ 错误：输出敏感信息
log.info("User login: email={}, password={}", email, password);

// ✅ 正确：隐藏敏感信息
log.info("User login attempt: email={}", email);
log.debug("Login successful for user: {}", userId);  // 只输出 ID
```

**绝对不能记录的信息**:
- ❌ 密码（原文或加密后）
- ❌ JWT Token
- ❌ 身份证号码
- ❌ 信用卡信息

---

### 🚫 CORS 配置

```java
@Configuration
public class WebConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        // 生产环境：指定允许的域名
        config.setAllowedOrigins(Arrays.asList(
            "https://yourdomain.com",
            "https://app.yourdomain.com"
        ));
        
        // 开发环境：允许所有来源（仅开发）
        config.addAllowedOriginPattern("*");
        
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```

---

## 性能优化

### 🚀 数据库优化

#### 1. 索引优化

**在实体类中添加索引**:
```java
@Table(name = "article", indexes = {
    @Index(name = "idx_article_status", columnList = "status"),
    @Index(name = "idx_article_author_id", columnList = "author_id"),
    @Index(name = "idx_article_created_at", columnList = "created_at"),
    @Index(name = "idx_article_status_created_at", columnList = "status, created_at")  // 组合索引
})
@Entity
public class Article {
    // ...
}
```

**索引原则**:
1. ✅ 常用于 WHERE 条件的字段
2. ✅ 常用于 ORDER BY 的字段
3. ✅ 外键字段
4. ❌ **避免**对低基数字段建索引（如性别、布尔值）

#### 2. 延迟加载

```java
// ✅ 推荐：使用 LAZY 加载
@ManyToMany(fetch = FetchType.LAZY)
private Set<Tag> tags;

// ❌ 避免：EAGER 加载会导致 N+1 问题
@ManyToMany(fetch = FetchType.EAGER)
private Set<Tag> tags;
```

**解决 N+1 问题**:
```java
// 使用 JOIN FETCH
@Query("SELECT a FROM Article a JOIN FETCH a.tags WHERE a.aid = :aid")
Article findArticleWithTags(@Param("aid") String aid);

// 或使用 @EntityGraph
@EntityGraph(attributePaths = {"tags", "categories"})
Article findByAid(String aid);
```

#### 3. 分页查询

```java
// Controller
@GetMapping
public ResultResponse<Page<ArticleVO>> getArticles(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size,
    @RequestParam(defaultValue = "createdAt,desc") String sort) {
    
    Pageable pageable = PageRequest.of(page, size, Sort.by(
        Sort.Direction.fromString(sort.split(",")[1]),
        sort.split(",")[0]
    ));
    return articleService.getArticles(pageable);
}
```

**分页最佳实践**:
- ✅ 默认页大小 20-50 条
- ✅ 最大页大小限制 100 条
- ✅ 使用游标分页代替 offset（大数据量）

---

### ⚡ Redis 缓存

#### 1. 缓存策略

**常用缓存模式**:
```java
@Service
public class ArticleServiceImpl implements IArticleService {
    
    @Resource
    private RedisUtil redisUtil;
    
    @Resource
    private ArticleRepository articleRepository;
    
    @Override
    public ResultResponse<ArticleVO> getArticleById(String aid) {
        // 1. 先查缓存
        String cacheKey = "article:" + aid;
        Optional<ArticleVO> cached = redisUtil.get(cacheKey, ArticleVO.class);
        if (cached.isPresent()) {
            return ResultResponse.success(cached.get());
        }
        
        // 2. 缓存未命中，查数据库
        Article article = articleRepository.findById(aid)
            .orElseThrow(() -> new BusinessException(ResponseCode.ARTICLE_NOT_FOUND));
        
        // 3. 写入缓存（设置过期时间）
        ArticleVO vo = BeanUtil.copyProperties(article, ArticleVO.class);
        redisUtil.set(cacheKey, vo, 3600);  // 1小时
        
        return ResultResponse.success(vo);
    }
    
    @Override
    @Transactional
    public ResultResponse<String> updateArticle(String aid, UpdateArticleRequest request) {
        // 更新数据库
        Article article = articleRepository.findById(aid)
            .orElseThrow(() -> new BusinessException(ResponseCode.ARTICLE_NOT_FOUND));
        // ... 更新逻辑
        articleRepository.save(article);
        
        // 删除缓存（缓存失效策略）
        redisUtil.delete("article:" + aid);
        
        return ResultResponse.success(ResponseCode.ARTICLE_UPDATED_SUCCESSFULLY);
    }
}
```

**缓存键命名规范**:
```java
public class RedisConstants {
    // 格式：模块:实体:ID
    public static final String ARTICLE_KEY_FORMAT = "article:%s";
    public static final String USER_KEY_FORMAT = "user:%s";
    public static final String TAG_LIST_KEY = "tag:list";
}
```

#### 2. 缓存雪崩防护

```java
// 设置随机过期时间，防止缓存雪崩
int expireTime = 3600 + RandomUtil.randomInt(0, 300);  // 1小时 ± 5分钟
redisUtil.set(cacheKey, data, expireTime);
```

---

### 🛠️ 其他优化

#### 1. 异步处理

```java
@Service
public class NotificationService {
    
    @Async  // 异步执行
    public void sendEmailNotification(String email, String subject, String content) {
        // 发送邮件逻辑
        log.info("Sending email to: {}", email);
    }
}

// 启用异步支持
@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }
}
```

#### 2. DTO 投影查询

```java
// 只查询需要的字段，减少数据传输
@Query("SELECT new com.sunrizon.horizon.vo.ArticleVO(a.aid, a.title, a.createdAt) " +
       "FROM Article a WHERE a.status = :status")
List<ArticleVO> findSimpleArticles(@Param("status") ArticleStatus status);
```

---

## API文档生成规范

**AI Agent 必须无条件遵守：每个模块开发完成后必须生成详细的 API 文档**

### 📝 强制要求

**每个模块开发完成后，AI Agent 必须执行以下操作：**

1. ✅ **必须生成** `docs/api/{module_name}.md` 文档
2. ✅ **必须包含**所有 API 接口的详细信息
3. ✅ **必须提供**完整的请求/响应示例
4. ✅ **必须说明**所有可能的错误情况
5. ❌ **绝对禁止**省略或简化文档内容

---

### 📄 API 文档强制模板

**AI Agent 必须按照以下模板生成 API 文档，不得有任何省略：**

```markdown
# {Module Name} API 文档

> **模块描述**: [必须填写模块功能描述]
> **基础 URL**: `http://localhost:8080/api/{module}`
> **认证方式**: Bearer Token (JWT)
> **生成日期**: {YYYY-MM-DD}
> **版本**: v1.0

---

## 📚 目录

- [创建{Entity}](#1-创建{entity})
- [查询{Entity}](#2-查询{entity})
- [更新{Entity}](#3-更新{entity})
- [删除{Entity}](#4-删除{entity})
- [分页查询](#5-分页查询)
- [错误码说明](#错误码说明)

---

## 1. 创建{Entity}

### 基本信息

- **接口名称**: 创建{Entity}
- **请求方法**: `POST`
- **请求路径**: `/api/{module}/create`
- **需要认证**: 是 ✓
- **权限要求**: `{PERMISSION_NAME}` (可选)

### 请求参数

#### Headers

| 参数名 | 类型 | 必填 | 说明 | 示例 |
|---------|------|------|------|------|
| Authorization | String | 是 | JWT Token | `Bearer eyJhbGciOiJIUzI1NiJ9...` |
| Content-Type | String | 是 | 请求类型 | `application/json` |

#### Body (请求体)

**类型**: `application/json`

| 字段名 | 类型 | 必填 | 限制 | 默认值 | 说明 |
|--------|------|------|------|--------|------|
| fieldName1 | String | 是 | 长度 1-100 | - | 字段说明 |
| fieldName2 | Integer | 否 | 范围 1-999 | 0 | 字段说明 |
| fieldName3 | Enum | 是 | ACTIVE/INACTIVE | ACTIVE | 状态说明 |

#### 请求示例

```json
{
  "fieldName1": "Example Value",
  "fieldName2": 100,
  "fieldName3": "ACTIVE"
}
```

#### curl 示例

```bash
curl -X POST "http://localhost:8080/api/{module}/create" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "fieldName1": "Example Value",
    "fieldName2": 100,
    "fieldName3": "ACTIVE"
  }'
```

### 响应结果

#### 成功响应 (200)

**响应结构**: `ResultResponse<EntityVO>`

```json
{
  "status": 200,
  "message": "{Entity}创建成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "fieldName1": "Example Value",
    "fieldName2": 100,
    "fieldName3": "ACTIVE",
    "createdAt": "2025-10-16T10:30:00",
    "updatedAt": "2025-10-16T10:30:00"
  }
}
```

#### 失败响应

**参数验证失败 (400)**

```json
{
  "status": 400,
  "message": "请求参数错误",
  "data": null
}
```

**认证失败 (401)**

```json
{
  "status": 401,
  "message": "认证失败，请重新登录",
  "data": null
}
```

**权限不足 (403)**

```json
{
  "status": 403,
  "message": "权限不足",
  "data": null
}
```

**业务错误 (409)**

```json
{
  "status": 1002,
  "message": "{Entity}已存在",
  "data": null
}
```

### 字段说明

#### 请求字段

- **fieldName1**: [必须详细说明该字段的作用、格式、校验规则]
- **fieldName2**: [必须详细说明该字段的作用、取值范围]
- **fieldName3**: [必须详细说明该字段的作用、可选值]

#### 响应字段

- **id**: 唯一标识符，UUID 格式
- **createdAt**: 创建时间，ISO 8601 格式
- **updatedAt**: 更新时间，ISO 8601 格式

### 业务逻辑说明

1. [必须详细说明该接口的业务逻辑]
2. [必须说明前置条件]
3. [必须说明副作用]

### 注意事项

- ⚠️ [必须列出所有注意事项]
- ⚠️ [必须说明常见错误]
- ⚠️ [必须提供排错建议]

---

## 2. 查询{Entity}

[按照上述格式继续编写...]

---

## 错误码说明

### 通用错误码

| 错误码 | HTTP状态 | 错误信息 | 原因 | 解决方案 |
|--------|-----------|----------|------|----------|
| 200 | 200 | 请求成功 | - | - |
| 400 | 400 | 请求参数错误 | 参数验证失败 | 检查请求参数 |
| 401 | 401 | 认证失败 | Token 无效或过期 | 重新登录 |
| 403 | 403 | 权限不足 | 缺少所需权限 | 联系管理员 |
| 404 | 404 | 资源不存在 | ID 错误或已删除 | 检查 ID |
| 500 | 500 | 服务器内部错误 | 服务器异常 | 联系技术支持 |

### 模块特定错误码

| 错误码 | 错误信息 | 原因 | 解决方案 |
|--------|----------|------|----------|
| 1001 | {Entity}不存在 | ID 错误 | 检查 ID |
| 1002 | {Entity}已存在 | 重复创建 | 检查唯一性 |

---

## 测试用例

### 完整流程示例

```bash
# 1. 登录获取 Token
curl -X POST "http://localhost:8080/api/user/authenticate" \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password"}'

# 2. 创建{Entity}
curl -X POST "http://localhost:8080/api/{module}/create" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{...}'

# 3. 查询{Entity}
curl -X GET "http://localhost:8080/api/{module}/{id}" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 更新日志

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| v1.0 | 2025-10-16 | 初始版本 |

```

---

### 📁 文档组织结构（强制规范）

**AI Agent 必须按照以下目录结构组织 API 文档：**

```
docs/
├── api/
│   ├── user.md              # 用户模块 API 文档
│   ├── article.md           # 文章模块 API 文档
│   ├── category.md          # 分类模块 API 文档
│   ├── comment.md           # 评论模块 API 文档
│   ├── tag.md               # 标签模块 API 文档
│   └── README.md            # API 文档总目录
└── README.md                # 文档总说明
```

---

### ✅ 强制执行标准

**AI Agent 在生成每个 API 文档时必须包含：**

#### 1. 基本信息（必须）
- ✅ **必须**包含模块名称和描述
- ✅ **必须**包含基础 URL
- ✅ **必须**包含认证方式说明
- ✅ **必须**包含生成日期和版本号

#### 2. 每个 API 接口（必须）
- ✅ **必须**包含接口名称、方法、路径
- ✅ **必须**包含是否需要认证
- ✅ **必须**包含所有请求参数（Headers + Body）
- ✅ **必须**包含完整的请求示例（JSON + curl）
- ✅ **必须**包含成功响应示例
- ✅ **必须**包含所有可能的失败响应示例
- ✅ **必须**包含所有字段的详细说明
- ✅ **必须**包含业务逻辑说明
- ✅ **必须**包含注意事项

#### 3. 错误码说明（必须）
- ✅ **必须**列出所有通用错误码
- ✅ **必须**列出所有模块特定错误码
- ✅ **必须**说明每个错误码的原因和解决方案

#### 4. 测试用例（必须）
- ✅ **必须**提供完整的测试流程
- ✅ **必须**包含可直接执行的 curl 命令

#### 5. 更新日志（必须）
- ✅ **必须**记录所有版本更新

---

### ❌ 绝对禁止

**AI Agent 在生成 API 文档时绝对不得：**

- ❌ **绝对禁止**省略任何一个 API 接口的文档
- ❌ **绝对禁止**简化或粗略的描述
- ❌ **绝对禁止**缺少请求/响应示例
- ❌ **绝对禁止**缺少错误情况说明
- ❌ **绝对禁止**缺少字段说明
- ❌ **绝对禁止**使用模糊的描述（如“详见代码”、“略”）
- ❌ **绝对禁止**将多个接口合并为一个文档

---

### 🔍 文档质量检查清单

**生成 API 文档后，AI Agent 必须自检：**

- [ ] ✅ 每个 Controller 方法都有对应的文档
- [ ] ✅ 每个 API 都有完整的请求示例
- [ ] ✅ 每个 API 都有完整的响应示例
- [ ] ✅ 每个 API 都有 curl 测试命令
- [ ] ✅ 所有字段都有详细说明
- [ ] ✅ 所有错误码都有说明
- [ ] ✅ 所有枚举值都有说明
- [ ] ✅ 所有限制条件都有说明
- [ ] ✅ 文档中没有“TODO”、“略”等占位符
- [ ] ✅ 所有示例都是可直接使用的

---

### 📝 文档生成流程（强制执行）

**AI Agent 必须按照以下顺序执行：**

1. **开发完成后立即生成**
   - 在 Controller + Service + Repository 开发完成后
   - 在自测完成后
   - 在提交代码之前

2. **创建文档文件**
   ```bash
   # 在 Windows CMD 中执行
   mkdir docs\api 2>nul
   type nul > docs\api\{module_name}.md
   ```

3. **填写文档内容**
   - 按照强制模板填写
   - 不得省略任何部分

4. **生成 curl 测试命令**
   - 为每个 API 生成可执行的 curl 命令
   - 包含真实的请求参数

5. **更新 README**
   - 在 `docs/api/README.md` 中添加链接

6. **自检文档**
   - 使用检查清单逐项检查

---

### 📚 API 文档总目录示例

**`docs/api/README.md` 强制格式：**

```markdown
# Horizon API 文档总目录

> **项目名称**: Horizon 博客系统
> **API 版本**: v1.0
> **基础 URL**: `http://localhost:8080/api`
> **认证方式**: Bearer Token (JWT)

---

## 📚 模块列表

### 用户管理
- [👤 用户模块 API](./user.md)
  - 用户注册、登录、信息管理
  - 包含 10 个 API 接口

### 内容管理  
- [📝 文章模块 API](./article.md)
  - 文章 CRUD、发布、搜索
  - 包含 12 个 API 接口

- [📚 分类模块 API](./category.md)
  - 分类管理
  - 包含 6 个 API 接口

### 互动功能
- [💬 评论模块 API](./comment.md)
  - 评论 CRUD、审核
  - 包含 8 个 API 接口

---

## 🔑 认证说明

所有 API 接口（除登录/注册外）都需要在 Header 中携带 JWT Token：

```
Authorization: Bearer {your_token_here}
```

获取 Token：
```bash
curl -X POST "http://localhost:8080/api/user/authenticate" \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password"}'
```

---

## 🛠️ 快速开始

1. 启动后端服务
2. 查看 Swagger 文档：http://localhost:8080/swagger-ui.html
3. 阅读具体模块的 API 文档
4. 使用 curl 或 Postman 测试 API

---

## 🔄 更新日志

| 日期 | 模块 | 更新内容 |
|------|------|----------|
| 2025-10-16 | User | 初始版本 |
| 2025-10-16 | Article | 初始版本 |
```

---

### 📌 特别说明

**为什么必须生成详细的 API 文档：**

1. ✅ **前端开发依赖**：前端开发者需要准确的接口定义
2. ✅ **减少沟通成本**：避免反复确认字段和格式
3. ✅ **提高开发效率**：可直接复制示例代码
4. ✅ **方便测试**：提供 curl 命令可直接测试
5. ✅ **错误排查**：明确的错误码说明

**违规处理**：
- 🚨 发现模块开发完成但未生成 API 文档 → **立即拒绝提交**
- 🚨 发现 API 文档内容不完整 → **要求补充**
- 🚨 发现 API 文档缺少示例 → **要求添加**

---

## API文档生成规范

**AI Agent 必须无条件遵守:每个模块开发完成后必须生成详细的 API 文档**

### 📝 强制要求

**每个模块开发完成后,AI Agent 必须执行以下操作:**

1. ✅ **必须生成** `docs/api/{module_name}.md` 文档
2. ✅ **必须包含**所有 API 接口的详细信息
3. ✅ **必须提供**完整的请求/响应示例
4. ✅ **必须说明**所有可能的错误情况
5. ❌ **绝对禁止**省略或简化文档内容
6. ✅ **必须提供** curl 测试命令
7. ✅ **必须更新** `docs/api/README.md` 总目录

---

### 📖 API 文档强制模板

**AI Agent 必须按照以下模板生成每个模块的 API 文档:**

每个 API 接口文档必须包含以下部分:

1. **基本信息**: 请求方法、路径、认证要求
2. **请求参数**: Headers、URL参数、Body参数(含类型、必填、限制、说明)
3. **请求示例**: JSON 格式的完整请求示例
4. **curl 命令**: 可直接执行的 curl 测试命令
5. **成功响应**: 包含完整数据结构的 200 响应示例
6. **失败响应**: 所有可能的错误响应(400/401/403/404/409/500)
7. **字段说明**: 所有字段的详细说明
8. **业务逻辑**: 接口的业务处理流程
9. **注意事项**: 特殊情况和限制说明

---

### 📁 文档组织结构

**强制目录结构:**

```
docs/
└── api/
    ├── README.md           # API 总目录 (必须)
    ├── user.md             # 用户模块 API
    ├── article.md          # 文章模块 API
    ├── category.md         # 分类模块 API
    ├── comment.md          # 评论模块 API
    └── interaction.md      # 互动模块 API
```

---

### ✅ 强制执行标准

**每个 API 文档必须包含:**

- [ ] ✅ 模块描述和基础信息
- [ ] ✅ 所有 API 接口的完整文档
- [ ] ✅ 每个接口的请求参数表格(含类型、必填、限制、说明)
- [ ] ✅ 每个接口的 JSON 请求示例
- [ ] ✅ 每个接口的 curl 测试命令
- [ ] ✅ 每个接口的成功响应示例
- [ ] ✅ 每个接口的所有失败响应示例
- [ ] ✅ 所有字段的详细说明
- [ ] ✅ 业务逻辑说明
- [ ] ✅ 错误码说明表格
- [ ] ✅ 完整的测试用例流程
- [ ] ✅ 更新日志

---

### ❌ 绝对禁止

**以下行为将被立即拒绝:**

- ❌ **禁止**省略任何 API 接口
- ❌ **禁止**省略请求/响应示例
- ❌ **禁止**省略 curl 测试命令
- ❌ **禁止**省略错误响应示例
- ❌ **禁止**使用"略"、"同上"、"参考其他接口"等占位符
- ❌ **禁止**字段说明不完整或缺失
- ❌ **禁止**示例数据不真实或不完整

---

### 📋 文档质量检查清单

**AI Agent 在生成文档后必须自检:**

- [ ] ✅ 每个 Controller 方法都有对应的文档
- [ ] ✅ 每个 API 都有完整的请求示例
- [ ] ✅ 每个 API 都有完整的响应示例
- [ ] ✅ 每个 API 都有 curl 测试命令
- [ ] ✅ 所有字段都有详细说明
- [ ] ✅ 所有错误码都有说明
- [ ] ✅ 所有枚举值都有说明
- [ ] ✅ 分页接口包含分页参数说明
- [ ] ✅ 需要认证的接口标明认证要求
- [ ] ✅ 文档中没有"TODO"、"略"等占位符

---

### 🔄 文档生成流程

**强制执行顺序:**

1. **开发完成模块代码** (Controller + Service + Repository)
2. **启动项目测试** (确保所有 API 正常工作)
3. **使用 curl 测试每个 API** (获取真实的请求/响应)
4. **生成 API 文档** (按照强制模板填写)
5. **更新总目录** (在 `docs/api/README.md` 中添加链接)
6. **自检文档** (使用检查清单逐项检查)
7. **提交代码** (代码和文档一起提交)

---

### 📚 API 文档总目录示例

**`docs/api/README.md` 强制格式:**

```markdown
# Horizon API 文档总目录

> **项目名称**: Horizon 博客系统
> **API 版本**: v1.0
> **基础 URL**: `http://localhost:8080/api`
> **认证方式**: Bearer Token (JWT)

---

## 📚 模块列表

### 用户管理
- [👤 用户模块 API](./user.md)
  - 用户注册、登录、信息管理
  - 包含 10 个 API 接口

### 内容管理  
- [📝 文章模块 API](./article.md)
  - 文章 CRUD、发布、搜索
  - 包含 12 个 API 接口

- [📚 分类模块 API](./category.md)
  - 分类管理
  - 包含 6 个 API 接口

### 互动功能
- [💬 评论模块 API](./comment.md)
  - 评论 CRUD、审核
  - 包含 8 个 API 接口

---

## 🔑 认证说明

所有 API 接口(除登录/注册外)都需要在 Header 中携带 JWT Token:

```
Authorization: Bearer {your_token_here}
```

获取 Token:
```bash
curl -X POST "http://localhost:8080/api/user/authenticate" \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password"}'
```

---

## 🛠️ 快速开始

1. 启动后端服务
2. 查看 Swagger 文档: http://localhost:8080/swagger-ui.html
3. 阅读具体模块的 API 文档
4. 使用 curl 或 Postman 测试 API

---

## 🔄 更新日志

| 日期 | 模块 | 更新内容 |
|------|------|----------|
| 2025-10-16 | User | 初始版本 |
| 2025-10-16 | Article | 初始版本 |
```

---

### 📌 特别说明

**为什么必须生成详细的 API 文档:**

1. ✅ **前端开发依赖**: 前端开发者需要准确的接口定义
2. ✅ **减少沟通成本**: 避免反复确认字段和格式
3. ✅ **提高开发效率**: 可直接复制示例代码
4. ✅ **方便测试**: 提供 curl 命令可直接测试
5. ✅ **错误排查**: 明确的错误码说明
6. ✅ **团队协作**: 统一的接口文档规范
7. ✅ **项目维护**: 长期维护的重要参考

**违规处理:**
- 🚨 发现模块开发完成但未生成 API 文档 → **立即拒绝提交**
- 🚨 发现 API 文档内容不完整 → **要求补充**

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

**AI Agent 强制执行指令 - 无一例外**

### ✅ 强制执行项

**以下规则为绝对强制，AI Agent 不得有任何例外或灵活性：**

#### 核心规范（绝对强制）

1. **JPA 自动建表（零容忍）**
   - ✅ **必须执行**：开发环境使用 `spring.jpa.hibernate.ddl-auto=update`
   - ✅ **必须执行**：生产环境使用 `validate` + Flyway/Liquibase
   - ❌ **绝对禁止**：手动创建表的任何行为
   - ❌ **绝对禁止**：编写 CREATE TABLE、ALTER TABLE 等 DDL 语句
   - ❌ **绝对禁止**：建议或提示手动建表

2. **依赖注入（零容忍）**
   - ✅ **必须执行**：仅使用 `@Resource` 注解
   - ❌ **绝对禁止**：使用 `@Autowired` 注解
   - ❌ **绝对禁止**：使用 `@Inject` 注解
   - **自动修复**：发现 @Autowired 必须自动替换为 @Resource

3. **工具库（零容忍）**
   - ✅ **必须执行**：仅使用 Hutool（BeanUtil, StrUtil, Validator, RandomUtil）
   - ❌ **绝对禁止**：使用 Apache Commons Lang
   - ❌ **绝对禁止**：使用 Apache Commons Collections
   - ❌ **绝对禁止**：使用 Guava 工具类
   - **自动修复**：发现其他工具库必须自动替换为 Hutool

4. **分层架构（零容忍）**
   - ✅ **必须执行**：Controller → Service → Repository 三层架构
   - ❌ **绝对禁止**：跨层调用
   - ❌ **绝对禁止**：反向依赖
   - **自动拒绝**：发现违反分层的代码必须拒绝生成

5. **事务管理（零容忍）**
   - ✅ **必须执行**：写操作方法加 `@Transactional(rollbackFor = Exception.class)`
   - ❌ **绝对禁止**：在 Controller 层使用 @Transactional
   - ❌ **绝对禁止**：省略 rollbackFor 属性
   - **自动添加**：create*/update*/delete* 方法缺少注解时必须自动添加

6. **统一响应（零容忍）**
   - ✅ **必须执行**：仅使用 `ResultResponse<T>`
   - ❌ **绝对禁止**：直接返回 Entity
   - ❌ **绝对禁止**：返回 Map 或裸对象
   - ❌ **绝对禁止**：自定义响应格式
   - **自动修复**：发现非 ResultResponse 返回类型必须自动修正

7. **Lombok（零容忍）**
   - ✅ **必须执行**：Entity 使用 `@Getter` + `@Setter`
   - ✅ **必须执行**：DTO/VO 使用 `@Data`
   - ✅ **必须执行**：Service 使用 `@Slf4j`
   - ❌ **绝对禁止**：在 Entity 上使用 `@Data`

8. **枚举类型（零容忍）**
   - ✅ **必须执行**：仅使用 `@Enumerated(EnumType.STRING)`
   - ❌ **绝对禁止**：使用 `EnumType.ORDINAL`
   - ❌ **绝对禁止**：省略 @Enumerated 注解
   - **自动修复**：发现 ORDINAL 必须自动替换为 STRING

9. **功能自测（零容忍）**
   - ✅ **必须执行**：开发完成后必须自测
   - ✅ **必须执行**：测试正常和异常场景
   - ✅ **必须执行**：验证数据库表结构
   - **不得跳过**：任何测试步骤都不得省略

---

### ❌ 绝对禁止操作（零容忍）

**AI Agent 在任何情况下都不得执行以下操作：**

#### 安全相关（零容忍）

1. ❌ **绝对不得执行**
   - 手动编写任何 DDL 语句（CREATE TABLE, ALTER TABLE, DROP TABLE）
   - 明文存储密码（包括在代码、配置文件、数据库）
   - 在日志、响应、异常中输出密码、Token、身份证等敏感信息
   - 在代码中硬编码数据库密码、JWT Secret、API Key
   - 使用字符串拼接构造 SQL 查询（必须使用参数化查询）
   - 在 URL 参数中传递 JWT Token 或其他认证信息
   - 使用 MD5、SHA-1 等不安全的加密算法
   - **检测规则**：发现以上任何行为立即拒绝执行

2. ❌ **架构违反（零容忍）**
   - Controller 层直接调用 Repository 接口
   - Service 层直接操作 HttpServletRequest/HttpServletResponse
   - Repository 中包含业务逻辑代码
   - Controller 层使用 `@Transactional` 注解
   - Entity 实体类直接返回给前端（必须转换为 VO）
   - Service 之间出现循环依赖
   - **检测规则**：发现以上任何行为立即拒绝执行

3. ❌ **数据库操作禁止（零容忍）**
   - 使用 `EnumType.ORDINAL` 存储枚举值
   - 在大事务中执行耗时操作（发送邮件、调用第三方 API）
   - 在循环中执行数据库查询（N+1 问题）
   - 使用 `FetchType.EAGER` 加载大量关联数据
   - 使用 `CascadeType.ALL` 或 `CascadeType.REMOVE`
   - 使用 AUTO、IDENTITY、SEQUENCE 等非 UUID 主键策略
   - **检测规则**：发现以上任何行为立即拒绝执行

4. ❌ **代码质量禁止（零容忍）**
   - 保留大段注释代码不删除
   - 使用 `System.out.println()` 进行调试输出
   - 使用 Magic Number（未定义为常量的数字）
   - 过多的嵌套循环（超过 3 层）
   - 空指针异常不处理
   - 使用 @Autowired 而非 @Resource
   - 使用 Apache Commons 而非 Hutool
   - **检测规则**：发现以上任何行为立即修正或拒绝

---

### 💡 最佳实践

#### 🎯 核心开发准则总结

**以下是 Horizon 项目的十大核心开发准则，必须严格遵守：**

1. **🏗️ 架构分层原则**
   - ✅ 严格遵守 Controller → Service → Repository 三层架构
   - ✅ 单向依赖，禁止跨层调用
   - ✅ 每层职责单一，符合 SOLID 原则

2. **🔒 安全优先原则**
   - ✅ 所有密码必须使用 BCrypt 加密存储
   - ✅ JWT Token 使用 HTTPS 传输，设置合理过期时间
   - ✅ 所有用户输入必须验证，防止 XSS 和 SQL 注入
   - ✅ 敏感信息不出现在日志和 URL 中
   - ✅ 使用 `@PreAuthorize` 进行方法级权限控制

3. **⚡ 性能优化原则**
   - ✅ 常用查询字段添加数据库索引
   - ✅ 使用延迟加载（`FetchType.LAZY`）避免 N+1 查询
   - ✅ 合理使用 Redis 缓存，设置随机过期时间防止雪崩
   - ✅ 分页查询默认限制 20-50 条，最大 100 条
   - ✅ 耗时操作使用 `@Async` 异步处理

4. **💾 数据持久化原则**
   - ✅ 使用 JPA 实体类自动生成数据库表（开发环境）
   - ✅ 生产环境使用 `validate` + Flyway/Liquibase 管理版本
   - ✅ 主键必须使用 UUID（`GenerationType.UUID`）
   - ✅ 枚举字段必须使用 `EnumType.STRING` 存储
   - ✅ 审计字段使用 `@CreatedDate` 和 `@LastModifiedDate` 自动管理

5. **🔄 事务管理原则**
   - ✅ 所有写操作在 Service 层加 `@Transactional`
   - ✅ 指定回滚异常：`@Transactional(rollbackFor = Exception.class)`
   - ✅ 避免在事务中执行耗时操作（发邮件、调用第三方 API）
   - ✅ 禁止在 Controller 层使用事务注解

6. **📦 依赖管理原则**
   - ✅ 优先使用 `@Resource` 注解进行依赖注入
   - ✅ 核心依赖推荐使用构造器注入提升可测试性
   - ✅ 优先使用 Hutool 工具类（`StrUtil`, `BeanUtil`, `Validator`）
   - ✅ 避免引入功能重复的依赖库

7. **📝 代码规范原则**
   - ✅ 遵循统一的命名规范（PascalCase、camelCase、UPPER_SNAKE_CASE）
   - ✅ 所有公共方法必须有 Javadoc 注释
   - ✅ 使用 Lombok 简化代码（Entity 用 `@Getter/@Setter`，DTO 用 `@Data`）
   - ✅ 日志使用 `@Slf4j`，禁止使用 `System.out.println()`
   - ✅ 合理使用常量类管理魔法值

8. **🌐 API 设计原则**
   - ✅ 统一使用 `ResultResponse<T>` 包装返回结果
   - ✅ 错误码使用 `ResponseCode` 枚举集中管理
   - ✅ 禁止直接返回 Entity，必须转换为 VO
   - ✅ 使用 `@Valid` 验证请求参数
   - ✅ 使用 OpenAPI/Swagger 注解完善 API 文档

9. **🧪 质量保障原则**
   - ✅ 每个功能开发完成后必须完成自测
   - ✅ 测试正常场景和至少 3 个异常场景
   - ✅ 验证数据库表结构和数据正确性
   - ✅ 提交前检查无编译警告和未使用的导入
   - ✅ 清理注释代码和 TODO/FIXME 标记

10. **🔐 配置管理原则**
    - ✅ 敏感配置（密码、密钥）使用环境变量或加密存储
    - ✅ 区分开发、测试、生产环境配置
    - ✅ Redis 配置区分环境，生产环境必须设置密码
    - ✅ 使用 StringRedisSerializer 序列化 Key
    - ✅ 使用 GenericJackson2JsonRedisSerializer 序列化 Value

---

#### 开发流程

1. 💡 **先定义 Entity，让 JPA 生成表**
   - 先设计实体模型
   - 启动项目验证表结构
   - 必要时调整 Entity 定义

2. 💡 **使用 ResponseCode 管理错误码**
   - 集中定义所有响应码
   - 按业务模块分组
   - 使用有意义的错误码（如 1xxx 表示用户错误）

3. 💡 **使用常量类管理 Redis 键**
   ```java
   // RedisConstants.java
   public static final String USER_KEY_FORMAT = "user:%s";
   public static final String OTP_KEY_FORMAT = "otp:%s";
   
   // 使用
   String key = String.format(RedisConstants.USER_KEY_FORMAT, userId);
   ```

4. 💡 **Service 方法加详细注释**
   ```java
   /**
    * 创建新用户
    *
    * @param request 用户创建请求
    * @return {@link ResultResponse} 包含创建的 {@link UserVO}
    * @throws BusinessException 当用户名或邮箱已存在
    */
   @Override
   @Transactional(rollbackFor = Exception.class)
   public ResultResponse<UserVO> createUser(CreateUserRequest request) {
       // ...
   }
   ```

5. 💡 **使用 OpenAPI/Swagger 注解完善 API 文档**
   ```java
   @Tag(name = "User Management", description = "User management APIs")
   @RestController
   @RequestMapping("/api/user")
   public class UserController {
       
       @Operation(summary = "Create a new user", description = "Register a new user account")
       @ApiResponses(value = {
           @ApiResponse(responseCode = "200", description = "User created successfully"),
           @ApiResponse(responseCode = "400", description = "Invalid input"),
           @ApiResponse(responseCode = "409", description = "User already exists")
       })
       @PostMapping("/create")
       public ResultResponse<UserVO> createUser(@Valid @RequestBody CreateUserRequest request) {
           return userService.createUser(request);
       }
   }
   ```

6. 💡 **合理使用索引**
   - 常用查询条件建索引
   - 外键字段建索引
   - 组合查询建组合索引
   - 定期分析慢查询

7. 💡 **日志规范**
   ```java
   // ✅ 正确使用
   log.info("User created successfully: userId={}", userId);
   log.error("Failed to send email", exception);
   log.debug("Request params: {}", request);
   
   // ❌ 错误使用
   System.out.println("Debug info");
   log.info("User password: " + password);  // 泄露敏感信息
   ```

8. 💡 **异常处理**
   ```java
   // 使用全局异常处理器
   @RestControllerAdvice
   public class GlobalExceptionHandler {
       
       @ExceptionHandler(BusinessException.class)
       public ResultResponse<?> handleBusinessException(BusinessException e) {
           log.warn("Business exception: {}", e.getMessage());
           return ResultResponse.error(e.getResponseCode());
       }
       
       @ExceptionHandler(MethodArgumentNotValidException.class)
       public ResultResponse<?> handleValidationException(MethodArgumentNotValidException e) {
           String message = e.getBindingResult().getFieldError().getDefaultMessage();
           return ResultResponse.error(ResponseCode.VALIDATION_ERROR, message);
       }
   }
   ```

9. 💡 **数据转换**
   ```java
   // DTO → Entity
   User user = BeanUtil.copyProperties(request, User.class);
   
   // Entity → VO
   UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
   
   // 列表转换
   List<UserVO> voList = users.stream()
       .map(user -> BeanUtil.copyProperties(user, UserVO.class))
       .collect(Collectors.toList());
   ```

10. 💡 **环境配置分离**
    ```yaml
    # application-dev.yml
    spring:
      jpa:
        hibernate:
          ddl-auto: update
        show-sql: true
    
    # application-prod.yml
    spring:
      jpa:
        hibernate:
          ddl-auto: validate
        show-sql: false
    ```

---

### 📝 代码检查清单

**提交代码前的检查项**:

- [ ] ✅ 代码符合项目命名规范
- [ ] ✅ 所有公共方法有 Javadoc 注释
- [ ] ✅ 写操作已添加 `@Transactional`
- [ ] ✅ Entity 不直接返回给前端（已转 VO）
- [ ] ✅ 枚举使用 `EnumType.STRING`
- [ ] ✅ 敏感配置使用环境变量或加密
- [ ] ✅ 日志中无敏感信息
- [ ] ✅ 已添加必要的数据库索引
- [ ] ✅ 已完成功能自测
- [ ] ✅ API 文档已更新（Swagger）
- [ ] ✅ 无遗留的 TODO 或 FIXME
- [ ] ✅ 无未使用的导入和变量
- [ ] ✅ 已处理所有编译警告

---

### 🔧 常见问题解决

#### 1. N+1 查询问题

**问题**:
```java
// 会执行 N+1 次查询
List<Article> articles = articleRepository.findAll();
for (Article article : articles) {
    article.getTags();  // 每次都查询数据库
}
```

**解决**:
```java
// 使用 JOIN FETCH
@Query("SELECT a FROM Article a LEFT JOIN FETCH a.tags")
List<Article> findAllWithTags();

// 或使用 @EntityGraph
@EntityGraph(attributePaths = {"tags"})
List<Article> findAll();
```

#### 2. 缓存穿透

**问题**:查询不存在的数据时，缓存无法生效

**解决**:
```java
// 缓存空值，设置较短过期时间
if (article == null) {
    redisUtil.set(cacheKey, "null", 60);  // 1分钟
    return ResultResponse.error(ResponseCode.ARTICLE_NOT_FOUND);
}
```

#### 3. 循环依赖

**问题**: Service A 依赖 Service B，Service B 又依赖 Service A

**解决**:
- 提取公共逻辑到第三个 Service
- 使用 `@Lazy` 注解延迟加载
- 重新设计业务逻辑

---

### 🚀 快速参考指南

#### 常用代码模板

**1. 创建新实体类**
```java
@Table(name = "table_name")
@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class EntityName implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 36, updatable = false)
    private String id;
    
    @Column(name = "field_name", nullable = false, length = 100)
    private String fieldName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EntityStatus status;
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

**2. 创建 Service 实现**
```java
@Service
@Slf4j
public class EntityServiceImpl implements IEntityService {
    
    @Resource
    private EntityRepository entityRepository;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultResponse<EntityVO> createEntity(CreateEntityRequest request) {
        // 1. 参数验证
        if (entityRepository.existsByName(request.getName())) {
            return ResultResponse.error(ResponseCode.ENTITY_ALREADY_EXISTS);
        }
        
        // 2. DTO 转 Entity
        Entity entity = BeanUtil.copyProperties(request, Entity.class);
        
        // 3. 业务逻辑处理
        entity.setStatus(EntityStatus.ACTIVE);
        
        // 4. 保存数据
        Entity saved = entityRepository.save(entity);
        
        // 5. Entity 转 VO
        EntityVO vo = BeanUtil.copyProperties(saved, EntityVO.class);
        
        // 6. 返回响应
        return ResultResponse.success(ResponseCode.SUCCESS, vo);
    }
}
```

**3. 创建 Controller**
```java
@RestController
@RequestMapping("/api/entity")
@Slf4j
@Tag(name = "Entity Management", description = "Entity management APIs")
public class EntityController {
    
    @Resource
    private IEntityService entityService;
    
    @PostMapping("/create")
    @Operation(summary = "Create entity")
    public ResultResponse<EntityVO> createEntity(
        @Valid @RequestBody CreateEntityRequest request) {
        return entityService.createEntity(request);
    }
    
    @GetMapping("/{id}")
    public ResultResponse<EntityVO> getEntity(@PathVariable String id) {
        return entityService.getEntity(id);
    }
    
    @GetMapping
    public ResultResponse<Page<EntityVO>> getEntities(Pageable pageable) {
        return entityService.getEntities(pageable);
    }
}
```

**4. 缓存使用模板**
```java
public ResultResponse<EntityVO> getEntityById(String id) {
    // 1. 查缓存
    String cacheKey = String.format(RedisConstants.ENTITY_KEY_FORMAT, id);
    Optional<EntityVO> cached = redisUtil.get(cacheKey, EntityVO.class);
    if (cached.isPresent()) {
        return ResultResponse.success(cached.get());
    }
    
    // 2. 查数据库
    Entity entity = entityRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseCode.ENTITY_NOT_FOUND));
    
    // 3. 写入缓存（随机过期时间）
    EntityVO vo = BeanUtil.copyProperties(entity, EntityVO.class);
    int expireTime = 3600 + RandomUtil.randomInt(0, 300);
    redisUtil.set(cacheKey, vo, expireTime);
    
    return ResultResponse.success(vo);
}
```

**5. 全局异常处理**
```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ResultResponse<?> handleBusinessException(BusinessException e) {
        log.warn("{}: {}", e.getResponseCode().getMessage(), e.getMessage());
        return ResultResponse.error(e.getResponseCode());
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultResponse<?> handleValidationException(
        MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError()
            .getDefaultMessage();
        return ResultResponse.error(ResponseCode.VALIDATION_ERROR, message);
    }
    
    @ExceptionHandler(Exception.class)
    public ResultResponse<?> handleException(Exception e) {
        log.error("Unexpected error", e);
        return ResultResponse.error(ResponseCode.INTERNAL_ERROR);
    }
}
```

---

#### 常用命令快速查询

**Maven 命令**:
```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run

# 打包项目
mvn clean package

# 跳过测试打包
mvn clean package -DskipTests

# 运行测试
mvn test
```

**数据库查询**:
```sql
-- 查看所有表
SHOW TABLES;

-- 查看表结构
DESC table_name;

-- 查看索引
SHOW INDEX FROM table_name;

-- 分析查询性能
EXPLAIN SELECT * FROM table_name WHERE field = 'value';

-- 查看最新记录
SELECT * FROM table_name ORDER BY created_at DESC LIMIT 10;
```

**测试 API 命令**:
```bash
# 用户登录获取 Token
curl -X POST "http://localhost:8080/api/user/authenticate" \
  -H "Content-Type: application/json" \
  -d '{"email":"3499508634@qq.com","password":"Wei.Li.Laba00"}' | jq .

# 使用 Token 调用 API
curl -X GET "http://localhost:8080/api/entity/123" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" | jq .

# 创建资源
curl -X POST "http://localhost:8080/api/entity/create" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{"name":"test","status":"ACTIVE"}' | jq .
```

---

#### 常见注解速查

| 注解 | 用途 | 示例 |
|------|------|------|
| `@Entity` | JPA 实体类 | `@Entity` |
| `@Table` | 指定表名 | `@Table(name = "user")` |
| `@Id` | 主键字段 | `@Id` |
| `@GeneratedValue` | 主键生成策略 | `@GeneratedValue(strategy = GenerationType.UUID)` |
| `@Column` | 字段映射 | `@Column(name = "username", length = 50)` |
| `@Enumerated` | 枚举类型 | `@Enumerated(EnumType.STRING)` |
| `@CreatedDate` | 创建时间 | `@CreatedDate` |
| `@LastModifiedDate` | 更新时间 | `@LastModifiedDate` |
| `@ManyToMany` | 多对多关系 | `@ManyToMany(fetch = FetchType.LAZY)` |
| `@OneToMany` | 一对多关系 | `@OneToMany(mappedBy = "parent")` |
| `@Service` | 服务层 | `@Service` |
| `@RestController` | 控制器 | `@RestController` |
| `@RequestMapping` | URL 映射 | `@RequestMapping("/api/user")` |
| `@GetMapping` | GET 请求 | `@GetMapping("/{id}")` |
| `@PostMapping` | POST 请求 | `@PostMapping("/create")` |
| `@Valid` | 参数验证 | `@Valid @RequestBody Request request` |
| `@Transactional` | 事务管理 | `@Transactional(rollbackFor = Exception.class)` |
| `@PreAuthorize` | 权限控制 | `@PreAuthorize("hasRole('ADMIN')")` |
| `@Slf4j` | 日志工具 | `@Slf4j` |
| `@Data` | Lombok 工具 | `@Data` |
| `@Resource` | 依赖注入 | `@Resource` |

---

#### 关键配置速查

**application.yml 核心配置**:
```yaml
# 开发环境
spring:
  profiles:
    active: dev
  
  # 数据库配置
  datasource:
    url: jdbc:mysql://localhost:3306/dbname?useSSL=false&serverTimezone=GMT%2B8
    username: root
    password: ${DB_PASSWORD}  # 使用环境变量
  
  # JPA 配置
  jpa:
    hibernate:
      ddl-auto: update  # 开发：update，生产：validate
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQL8Dialect
  
  # Redis 配置
  redis:
    host: 127.0.0.1
    port: 6379
    password: ${REDIS_PASSWORD}  # 生产必须设置
    database: 0
  
  # RabbitMQ 配置
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: guest
    password: guest

# 服务器配置
server:
  port: 8080

# Swagger 配置
springdoc:
  api-docs:
    enabled: true
    path: /v3/api-docs
  swagger-ui:
    enabled: true
    path: /swagger-ui.html
```

---

### 📚 参考资料

- [Spring Boot Official Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/index.html)
- [Hutool Documentation](https://hutool.cn/docs/)
- [Effective Java (3rd Edition)](https://www.oreilly.com/library/view/effective-java-3rd/9780134686097/)

---

### 📋 版本更新日志

#### v2.0 (2025-10-16) - 重大更新

**新增内容**:
- ➕ 新增《十大核心开发准则总结》章节
- ➕ 新增《安全规范》章节（JWT、密码、权限、XSS、SQL注入、CORS）
- ➕ 新增《性能优化》章节（数据库、缓存、异步处理）
- ➕ 新增《快速参考指南》（代码模板、常用命令、注解速查）
- ➕ 新增《代码检查清单》（13 项提交前检查）
- ➕ 新增《常见问题解决》（N+1 查询、缓存穿透、循环依赖）

**完善内容**:
- ✨ 补充了 Spring Boot 3.5.5 和 Java 24 的技术栈说明
- ✨ 完善了依赖注入的最佳实践（构造器注入 vs @Resource）
- ✨ 详细化了层级职责表格（允许操作 + 禁止操作）
- ✨ 添加了层级调用规则的可视化图示
- ✨ 增强了实体类关系开发流程的说明
- ✨ 优化了最佳实践示例（日志、异常处理、数据转换）

**文档统计**:
- 📊 总行数：约 2260 行（增加 79%）
- 📊 章节数：9 大章节 + 40+ 小节
- 📊 代码示例：80+ 个实用示例
- 📊 检查清单：13 项提交前检查

**改进点**:
- 🔧 更专业：基于 Spring Boot 3.x 最新实践
- 🔧 更全面：覆盖开发、安全、性能、测试等各个方面
- 🔧 更实用：提供大量实际代码示例和检查清单
- 🔧 更易读：使用表格、图示、emoji 提升可读性

#### v1.0 (2025-10-15) - 初始版本
- ✅ 初始版本发布
- ✅ 包含基本的编码规范和开发流程

---

### 📝 总结

**AI Agent 执行指令总结**

本文档是 Horizon 项目的**强制性开发规范**，专为 AI Agent 设计。AI Agent 在生成、修改代码时**必须无条件遵守**所有规范。

**强制执行原则**：

- ✅ **必须执行**：提高代码质量 - 统一的编码风格和命名规范
- ✅ **必须执行**：增强系统安全 - 完善的安全防护机制
- ✅ **必须执行**：优化系统性能 - 有效的缓存和数据库优化策略
- ✅ **必须执行**：加快开发速度 - 提供代码模板和快速参考指南
- ✅ **必须执行**：减少维护成本 - 标准化的开发流程和检查清单

**关键强制要求**：
1. 🏗️ **必须**严格遵守三层架构：Controller → Service → Repository
2. 🔒 **必须**安全优先：密码加密、JWT 认证、输入验证
3. ⚡ **必须**性能优化：索引、缓存、延迟加载、异步处理
4. 💾 **必须**JPA 自动建表：开发环境 update，生产环境 validate
5. 🧪 **必须**质量保障：必须自测、代码检查、文档更新

**AI Agent 执行规则**：
- 🚫 **绝对禁止**手动编写 SQL 建表语句
- 🚫 **绝对禁止**使用 @Autowired 注解
- 🚫 **绝对禁止**使用 Apache Commons 工具库
- 🚫 **绝对禁止**跨层调用或反向依赖
- 🚫 **绝对禁止**直接返回 Entity 给前端
- 🚫 **绝对禁止**使用 EnumType.ORDINAL
- 🚫 **绝对禁止**明文存储密码
- 🚫 **绝对禁止**在日志中输出敏感信息

**违规处理机制**：
- 🚨 发现违反强制规范的代码 → **立即拒绝生成**
- 🚨 发现可自动修复的问题 → **自动修复后生成**
- 🚨 不确定是否违规时 → **严格遵守规范，宁可谨慎**

---

**最后更新**: 2025-10-16  
**维护者**: Horizon Development Team  
**版本**: v2.1 (AI Agent 强制执行版)  
**文档状态**: ✅ 强制执行 - 面向 AI Agent
**执行级别**: ⚠️ 必须执行（MUST） - 零容忍政策

---

> ⚠️ **AI Agent 特别声明**: 
> 
> 本文档为 **强制性执行规范**，所有标记为“必须”、“绝对禁止”的项目都是**不可协商的硕性要求**。
> 
> AI Agent 在任何情况下都**不得**：
> - ❌ 以“可能”、“建议”、“推荐”等不确定语气解读规范
> - ❌ 自行决定是否遵守某项规范
> - ❌ 因“方便”、“简单”等理由违反规范
> - ❌ 提供多种方案供用户选择（必须直接生成符合规范的代码）
> 
> **违规处理**：任何违反本规范的代码生成将被立即拒绝或自动修正。
