# 异常处理和响应格式最佳实践指南

## 📋 目录

1. [核心设计原则](#核心设计原则)
2. [异常处理机制](#异常处理机制)
3. [响应格式规范](#响应格式规范)
4. [使用示例](#使用示例)
5. [迁移指南](#迁移指南)
6. [常见问题](#常见问题)

---

## 🎯 核心设计原则

### 1. 统一的异常处理机制

**✅ 推荐做法**：Service层抛出异常，由GlobalExceptionHandler统一处理

```java
// ✅ GOOD - 抛出异常
public UserVO getUser(String userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResponseCode.USER_NOT_FOUND, 
            "User with ID " + userId + " not found"
        ));
    return convertToVO(user);
}
```

**❌ 不推荐做法**：Service层返回错误响应

```java
// ❌ BAD - 返回错误响应（已废弃）
public ResultResponse<UserVO> getUser(String userId) {
    Optional<User> userOpt = userRepository.findById(userId);
    if (userOpt.isEmpty()) {
        return ResultResponse.error(ResponseCode.USER_NOT_FOUND);
    }
    return ResultResponse.success(convertToVO(userOpt.get()));
}
```

### 2. 清晰的职责分离

| 层级 | 职责 | 返回类型 |
|-----|------|---------|
| **Controller** | 参数校验、调用Service、返回响应 | `ResultResponse<T>` |
| **Service** | 业务逻辑、抛出异常 | 业务对象（如 `UserVO`）或 `void` |
| **Repository** | 数据访问 | `Optional<T>`、`List<T>` 等 |
| **GlobalExceptionHandler** | 捕获异常、转换为统一响应 | `ResultResponse<?>` |

### 3. 响应格式统一

所有API响应都应该符合以下格式：

```json
{
  "code": 200,
  "message": "Request processed successfully",
  "data": { ... },
  "timestamp": "2024-01-15T10:30:00"
}
```

---

## 🚨 异常处理机制

### 异常类层次结构

```
RuntimeException
  └─ BusinessException (业务异常基类)
       ├─ ResourceNotFoundException (资源未找到)
       │    └─ UsernameNotFoundException (用户未找到)
       └─ ValidationException (业务验证失败)
```

### 1. BusinessException（业务异常基类）

**用途**：所有业务异常的基类

**构造方法**：

```java
// 使用默认消息
throw new BusinessException(ResponseCode.USER_NOT_FOUND);

// 使用自定义消息
throw new BusinessException(ResponseCode.USER_NOT_FOUND, "User with email user@example.com not found");

// 包含原因异常
throw new BusinessException(ResponseCode.DATABASE_ERROR, e);

// 自定义消息 + 原因异常
throw new BusinessException(ResponseCode.DATABASE_ERROR, "Failed to save user", e);
```

**特性**：
- 包含 `ResponseCode`（错误码和默认消息）
- 支持自定义错误消息（覆盖默认消息）
- 支持原因异常链
- 自动被 `GlobalExceptionHandler` 捕获并转换为统一响应

### 2. ResourceNotFoundException（资源未找到异常）

**用途**：当查询的资源不存在时抛出

**使用场景**：
- 根据ID查询实体不存在
- 根据业务键查询资源不存在

**示例**：

```java
// 简单用法
throw new ResourceNotFoundException(ResponseCode.ARTICLE_NOT_FOUND);

// 带详细信息
throw new ResourceNotFoundException(
    ResponseCode.ARTICLE_NOT_FOUND, 
    "Article with slug '" + slug + "' not found"
);
```

### 3. ValidationException（业务验证异常）

**用途**：业务逻辑验证失败（区别于Bean Validation）

**使用场景**：
- 业务规则验证（如：用户名已存在）
- 状态验证（如：文章已发布无法删除）
- 权限验证（如：不能删除自己的账户）

**示例**：

```java
// 用户名重复
if (userRepository.existsByUsername(username)) {
    throw new ValidationException(
        ResponseCode.USER_USERNAME_EXISTS, 
        "Username '" + username + "' is already taken"
    );
}

// 状态不符合要求
if (article.getStatus() == ArticleStatus.PUBLISHED) {
    throw new ValidationException(
        ResponseCode.ARTICLE_INVALID_STATUS,
        "Cannot delete published article"
    );
}
```

### 4. UsernameNotFoundException（用户未找到异常）

**用途**：用户认证时未找到用户

**使用场景**：
- 登录时根据用户名/邮箱查询用户
- Spring Security的UserDetailsService

**示例**：

```java
// 简洁写法（自动生成错误消息）
throw new UsernameNotFoundException("user123");

// 输出：User not found: user123
```

---

## 📦 响应格式规范

### ResultResponse 结构

```java
@Getter
public class ResultResponse<T> {
    private final int code;           // 业务状态码
    private final String message;      // 响应消息
    private final T data;              // 响应数据（可为null）
    private final LocalDateTime timestamp; // 响应时间戳
}
```

### 创建成功响应

```java
// 1. 无数据的成功响应
return ResultResponse.success();
// {"code": 200, "message": "Request processed successfully", "data": null, "timestamp": "..."}

// 2. 带数据的成功响应
return ResultResponse.success(userVO);
// {"code": 200, "message": "Request processed successfully", "data": {...}, "timestamp": "..."}

// 3. 带自定义成功码的响应
return ResultResponse.success(ResponseCode.USER_CREATED, userVO);
// {"code": 10010, "message": "User created successfully", "data": {...}, "timestamp": "..."}

// 4. 仅自定义成功码（无数据）
return ResultResponse.success(ResponseCode.ARTICLE_DELETED_SUCCESSFULLY);
// {"code": 2008, "message": "Article deleted successfully", "data": null, "timestamp": "..."}
```

### 错误响应（由GlobalExceptionHandler自动生成）

当抛出异常时，`GlobalExceptionHandler` 会自动捕获并转换为 `ResultResponse`：

```java
// Service层抛出异常
throw new ResourceNotFoundException(ResponseCode.USER_NOT_FOUND, "User with ID 123 not found");

// GlobalExceptionHandler自动处理，返回：
// {"code": 1001, "message": "User with ID 123 not found", "data": null, "timestamp": "..."}
```

---

## 💡 使用示例

### 示例1：CRUD操作标准模式

```java
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements IArticleService {

  private final ArticleRepository articleRepository;

  /**
   * 创建文章
   * Controller返回ResultResponse，Service返回业务对象
   */
  @Override
  @Transactional
  public ArticleVO createArticle(CreateArticleRequest request) {
    // 1. 业务验证 - 抛出异常
    if (articleRepository.existsBySlug(request.getSlug())) {
      throw new ValidationException(
          ResponseCode.ARTICLE_SLUG_EXISTS,
          "Article with slug '" + request.getSlug() + "' already exists"
      );
    }

    // 2. 执行业务逻辑
    Article article = BeanUtil.copyProperties(request, Article.class);
    Article saved = articleRepository.save(article);

    // 3. 返回业务对象（不是ResultResponse）
    return convertToVO(saved);
  }

  /**
   * 获取文章
   */
  @Override
  public ArticleVO getArticle(String articleId) {
    // 找不到时抛出异常
    Article article = articleRepository.findById(articleId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResponseCode.ARTICLE_NOT_FOUND,
            "Article with ID " + articleId + " not found"
        ));
    
    return convertToVO(article);
  }

  /**
   * 更新文章
   */
  @Override
  @Transactional
  public ArticleVO updateArticle(String articleId, UpdateArticleRequest request) {
    // 1. 查询文章（不存在抛出异常）
    Article article = articleRepository.findById(articleId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResponseCode.ARTICLE_NOT_FOUND
        ));

    // 2. 验证slug唯一性
    if (request.getSlug() != null && !request.getSlug().equals(article.getSlug())) {
      if (articleRepository.existsBySlug(request.getSlug())) {
        throw new ValidationException(ResponseCode.ARTICLE_SLUG_EXISTS);
      }
      article.setSlug(request.getSlug());
    }

    // 3. 更新字段
    if (request.getTitle() != null) {
      article.setTitle(request.getTitle());
    }

    // 4. 保存并返回
    Article updated = articleRepository.save(article);
    return convertToVO(updated);
  }

  /**
   * 删除文章
   */
  @Override
  @Transactional
  public void deleteArticle(String articleId) {
    // 验证存在性
    Article article = articleRepository.findById(articleId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResponseCode.ARTICLE_NOT_FOUND
        ));

    // 验证业务规则
    if (article.getStatus() == ArticleStatus.PUBLISHED) {
      throw new ValidationException(
          ResponseCode.ARTICLE_INVALID_STATUS,
          "Cannot delete published article. Please unpublish it first."
      );
    }

    // 执行删除
    articleRepository.delete(article);
    // void方法，Controller会返回ResultResponse.success(ResponseCode.ARTICLE_DELETED_SUCCESSFULLY)
  }
}
```

### 示例2：Controller层

```java
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

  private final IArticleService articleService;

  /**
   * 创建文章
   */
  @PostMapping
  public ResultResponse<ArticleVO> createArticle(@Valid @RequestBody CreateArticleRequest request) {
    // Service抛出异常会被GlobalExceptionHandler捕获
    ArticleVO article = articleService.createArticle(request);
    return ResultResponse.success(ResponseCode.ARTICLE_CREATED, article);
  }

  /**
   * 获取文章
   */
  @GetMapping("/{id}")
  public ResultResponse<ArticleVO> getArticle(@PathVariable String id) {
    ArticleVO article = articleService.getArticle(id);
    return ResultResponse.success(article);
  }

  /**
   * 更新文章
   */
  @PutMapping("/{id}")
  public ResultResponse<ArticleVO> updateArticle(
      @PathVariable String id,
      @Valid @RequestBody UpdateArticleRequest request) {
    ArticleVO article = articleService.updateArticle(id, request);
    return ResultResponse.success(ResponseCode.ARTICLE_UPDATED_SUCCESSFULLY, article);
  }

  /**
   * 删除文章
   */
  @DeleteMapping("/{id}")
  public ResultResponse<Void> deleteArticle(@PathVariable String id) {
    articleService.deleteArticle(id);
    return ResultResponse.success(ResponseCode.ARTICLE_DELETED_SUCCESSFULLY);
  }

  /**
   * 分页查询
   */
  @GetMapping
  public ResultResponse<Page<ArticleVO>> getArticles(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<ArticleVO> articles = articleService.getArticles(pageable);
    return ResultResponse.success(articles);
  }
}
```

### 示例3：参数验证

```java
/**
 * 请求DTO - 使用Bean Validation
 */
@Data
public class CreateUserRequest {
  
  @NotBlank(message = "Username cannot be empty")
  @Size(min = 3, max = 20, message = "Username must be 3-20 characters")
  private String username;

  @NotBlank(message = "Email cannot be empty")
  @Email(message = "Invalid email format")
  private String email;

  @NotBlank(message = "Password cannot be empty")
  @Size(min = 8, message = "Password must be at least 8 characters")
  private String password;
}

/**
 * Controller - 使用@Valid触发验证
 */
@PostMapping("/users")
public ResultResponse<UserVO> createUser(@Valid @RequestBody CreateUserRequest request) {
  // @Valid验证失败会抛出MethodArgumentNotValidException
  // GlobalExceptionHandler会捕获并返回VALIDATION_ERROR响应
  UserVO user = userService.createUser(request);
  return ResultResponse.success(ResponseCode.USER_CREATED, user);
}

/**
 * Service - 业务验证
 */
@Override
public UserVO createUser(CreateUserRequest request) {
  // Bean Validation已通过，这里做业务验证
  
  // 验证用户名唯一性
  if (userRepository.existsByUsername(request.getUsername())) {
    throw new ValidationException(
        ResponseCode.USER_USERNAME_EXISTS,
        "Username '" + request.getUsername() + "' is already taken"
    );
  }

  // 验证邮箱唯一性
  if (userRepository.existsByEmail(request.getEmail())) {
    throw new ValidationException(
        ResponseCode.USER_EMAIL_EXISTS,
        "Email '" + request.getEmail() + "' is already registered"
    );
  }

  // 执行创建逻辑
  User user = new User();
  user.setUsername(request.getUsername());
  user.setEmail(request.getEmail());
  user.setPassword(passwordEncoder.encode(request.getPassword()));
  
  User saved = userRepository.save(user);
  return convertToVO(saved);
}
```

---

## 🔄 迁移指南

### 从旧代码迁移到新机制

**旧代码**（返回ResultResponse错误）：

```java
@Override
public ResultResponse<CategoryVO> getCategory(String cid) {
    if (StrUtil.isBlank(cid)) {
        return ResultResponse.error(ResponseCode.CATEGORY_ID_CANNOT_BE_EMPTY);
    }
    
    Category category = categoryRepository.findById(cid)
        .orElseThrow(() -> new RuntimeException("Category not found with cid: " + cid));
    
    CategoryVO categoryVO = BeanUtil.copyProperties(category, CategoryVO.class);
    return ResultResponse.success(categoryVO);
}
```

**新代码**（抛出异常）：

```java
@Override
public CategoryVO getCategory(String cid) {
    // 参数验证 - 抛出异常
    if (StrUtil.isBlank(cid)) {
        throw new ValidationException(
            ResponseCode.CATEGORY_ID_CANNOT_BE_EMPTY,
            "Category ID is required"
        );
    }
    
    // 查询不存在 - 抛出异常
    Category category = categoryRepository.findById(cid)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResponseCode.CATEGORY_NOT_FOUND,
            "Category with ID " + cid + " not found"
        ));
    
    // 返回业务对象（不是ResultResponse）
    return BeanUtil.copyProperties(category, CategoryVO.class);
}
```

**Controller调整**：

```java
// 旧代码
@GetMapping("/{id}")
public ResultResponse<CategoryVO> getCategory(@PathVariable String id) {
    return categoryService.getCategory(id); // Service返回ResultResponse
}

// 新代码
@GetMapping("/{id}")
public ResultResponse<CategoryVO> getCategory(@PathVariable String id) {
    CategoryVO category = categoryService.getCategory(id); // Service返回CategoryVO
    return ResultResponse.success(category); // Controller包装成ResultResponse
}
```

### 迁移步骤

1. **修改Service接口和实现类**
   - 将返回类型从 `ResultResponse<T>` 改为 `T`
   - 将 `return ResultResponse.error(...)` 改为 `throw new XxxException(...)`
   - 将 `return ResultResponse.success(data)` 改为 `return data`

2. **修改Controller**
   - 接收Service返回的业务对象
   - 用 `ResultResponse.success()` 包装返回值

3. **替换RuntimeException**
   - 将 `new RuntimeException(...)` 改为具体的业务异常
   - 如：`ResourceNotFoundException`、`ValidationException`

4. **测试验证**
   - 确保异常正确抛出
   - 确保GlobalExceptionHandler正确捕获并转换

---

## ❓ 常见问题

### Q1: 什么时候使用BusinessException vs ResourceNotFoundException？

**A:** 根据语义选择：

- **ResourceNotFoundException**: 查询的资源不存在（404语义）
  ```java
  // 用户、文章、分类等实体不存在
  throw new ResourceNotFoundException(ResponseCode.USER_NOT_FOUND);
  ```

- **ValidationException**: 业务验证失败（400语义）
  ```java
  // 用户名已存在、状态不符合要求、权限不足等
  throw new ValidationException(ResponseCode.USER_USERNAME_EXISTS);
  ```

- **BusinessException**: 通用业务异常（其他情况）
  ```java
  // 第三方服务调用失败、数据库错误等
  throw new BusinessException(ResponseCode.INTERNAL_ERROR, e);
  ```

### Q2: Service方法应该返回什么类型？

**A:** 根据操作类型选择：

| 操作类型 | 返回类型 | 示例 |
|---------|---------|------|
| 查询单个 | `EntityVO` | `UserVO getUser(String id)` |
| 查询列表 | `List<EntityVO>` | `List<UserVO> getUsers()` |
| 分页查询 | `Page<EntityVO>` | `Page<UserVO> getUsers(Pageable)` |
| 创建/更新 | `EntityVO` | `UserVO createUser(...)` |
| 删除 | `void` | `void deleteUser(String id)` |
| 业务操作 | `void` 或结果对象 | `void publishArticle(String id)` |

### Q3: 什么时候使用@Valid，什么时候抛出ValidationException？

**A:**
- **@Valid (Bean Validation)**: 参数格式验证
  - 非空、长度、格式、范围等
  - 在Controller层自动验证
  - 失败抛出 `MethodArgumentNotValidException`

- **ValidationException**: 业务规则验证
  - 唯一性、状态、权限等
  - 在Service层手动验证
  - 需要访问数据库或其他业务逻辑

```java
// Bean Validation - 格式验证
@NotBlank(message = "Username cannot be empty")
@Size(min = 3, max = 20, message = "Username must be 3-20 characters")
private String username;

// Service层 - 业务验证
if (userRepository.existsByUsername(username)) {
    throw new ValidationException(ResponseCode.USER_USERNAME_EXISTS);
}
```

### Q4: GlobalExceptionHandler返回的HTTP状态码是什么？

**A:** 当前配置：

| 异常类型 | HTTP状态码 | 说明 |
|---------|-----------|------|
| BusinessException | 200 OK | 业务错误在响应体的code中体现 |
| ValidationException | 200 OK | 继承自BusinessException |
| ResourceNotFoundException | 200 OK | 继承自BusinessException |
| MethodArgumentNotValidException | 400 Bad Request | Bean Validation失败 |
| Exception (兜底) | 500 Internal Server Error | 未预期的系统错误 |

**注意**：业务异常返回200是因为HTTP请求本身是成功的，错误信息在响应体中。如果需要返回4xx/5xx，可以在`@ExceptionHandler`上修改`@ResponseStatus`。

### Q5: 如何处理事务回滚？

**A:** 
- 默认情况下，Spring `@Transactional` 遇到 `RuntimeException`（包括 `BusinessException`）会自动回滚
- 不需要额外配置
- 如果希望某些异常不回滚，可以配置：
  ```java
  @Transactional(noRollbackFor = ValidationException.class)
  ```

### Q6: 生产环境是否应该暴露详细错误信息？

**A:** 分情况处理：

- **业务异常**：可以返回详细信息（如"用户名已存在"）
  ```java
  throw new ValidationException(ResponseCode.USER_USERNAME_EXISTS, "Username 'admin' is already taken");
  ```

- **系统异常**：应隐藏详细信息，只返回通用错误
  ```java
  @ExceptionHandler(Exception.class)
  public ResultResponse<?> handleException(Exception e) {
      log.error("Unexpected error", e); // 记录完整日志
      return ResultResponse.error(ResponseCode.INTERNAL_ERROR, "An unexpected error occurred"); // 返回通用信息
  }
  ```

- 可以通过配置文件控制是否返回堆栈信息：
  ```yaml
  app:
    error:
      include-stack-trace: ${INCLUDE_STACK_TRACE:false}  # 生产环境false，开发环境true
  ```

---

## 📚 相关文件

- [`ResultResponse.java`](../src/main/java/com/sunrizon/horizon/utils/ResultResponse.java) - 统一响应包装类
- [`ResponseCode.java`](../src/main/java/com/sunrizon/horizon/enums/ResponseCode.java) - 响应码枚举
- [`GlobalExceptionHandler.java`](../src/main/java/com/sunrizon/horizon/exception/GlobalExceptionHandler.java) - 全局异常处理器
- [`BusinessException.java`](../src/main/java/com/sunrizon/horizon/exception/BusinessException.java) - 业务异常基类
- [`ResourceNotFoundException.java`](../src/main/java/com/sunrizon/horizon/exception/ResourceNotFoundException.java) - 资源未找到异常
- [`ValidationException.java`](../src/main/java/com/sunrizon/horizon/exception/ValidationException.java) - 业务验证异常
- [`UsernameNotFoundException.java`](../src/main/java/com/sunrizon/horizon/exception/UsernameNotFoundException.java) - 用户未找到异常

---

## 🎓 总结

### 核心要点

1. **Service层抛出异常，不返回ResultResponse**
2. **Controller层捕获Service返回值，包装成ResultResponse**
3. **GlobalExceptionHandler统一处理所有异常**
4. **使用语义化的异常类（ResourceNotFoundException、ValidationException）**
5. **ResponseCode.code 区分HTTP标准码（200/400/500）和业务码（1xxx-10xxx）**
6. **Bean Validation处理格式验证，Service层处理业务验证**

### 优势

✅ **代码更简洁**：Service层专注业务逻辑，不需要包装响应  
✅ **职责清晰**：异常处理统一在GlobalExceptionHandler  
✅ **易于测试**：Service方法返回业务对象，更容易mock和断言  
✅ **符合规范**：遵循Spring Boot和REST API最佳实践  
✅ **易于维护**：异常处理逻辑集中管理，修改方便  

### 下一步

1. 逐步迁移现有Service层代码（优先级高的模块先迁移）
2. 添加集成测试验证异常处理是否正确
3. 考虑添加全局日志拦截器记录所有请求和响应
4. 生产环境配置错误信息脱敏策略

---

**文档版本**: 1.0  
**最后更新**: 2024-01-15  
**维护者**: Horizon Team
