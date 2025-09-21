# Horizon Blog 实体结构说明

## 📊 **实体关系概览**

### **核心实体**
- **User** - 用户实体
- **Role** - 角色实体  
- **Permission** - 权限实体
- **Article** - 文章实体
- **Category** - 分类实体
- **Tag** - 标签实体
- **Comment** - 评论实体
- **Like** - 点赞实体
- **File** - 文件实体
- **Setting** - 系统设置实体

## 🔗 **实体关系图**

```
User (用户)
├── 1:N → Article (文章)
├── 1:N → Comment (评论)
├── 1:N → Like (点赞)
├── 1:N → File (文件)
└── M:N → Role (角色)

Role (角色)
├── M:N → User (用户)
└── M:N → Permission (权限)

Article (文章)
├── N:1 → User (作者)
├── M:N → Category (分类)
├── M:N → Tag (标签)
└── 1:N → Comment (评论)

Category (分类)
├── M:N → Article (文章)
└── 1:N → Category (子分类)

Comment (评论)
├── N:1 → Article (文章)
├── N:1 → User (用户)
└── 1:N → Comment (回复)

Like (点赞)
└── N:1 → User (用户)

File (文件)
└── N:1 → User (上传者)
```

## 📋 **实体详细说明**

### **1. User (用户实体)**
```java
- uid: String (主键)
- username: String (用户名)
- email: String (邮箱)
- password: String (密码)
- avatar: String (头像)
- status: UserStatus (状态)
- createdAt: LocalDateTime (创建时间)
- updatedAt: LocalDateTime (更新时间)
- roles: Set<Role> (角色集合)
```

### **2. Article (文章实体)**
```java
- aid: String (主键)
- title: String (标题)
- slug: String (URL别名)
- summary: String (摘要)
- content: String (内容)
- coverImage: String (封面图)
- status: ArticleStatus (状态)
- authorId: String (作者ID)
- viewCount: Long (浏览次数)
- likeCount: Long (点赞数)
- commentCount: Long (评论数)
- isFeatured: Boolean (是否精选)
- isTop: Boolean (是否置顶)
- publishedAt: LocalDateTime (发布时间)
- categories: Set<Category> (分类集合)
- tags: Set<Tag> (标签集合)
- comments: Set<Comment> (评论集合)
```

### **3. Category (分类实体)**
```java
- cid: String (主键)
- name: String (分类名)
- slug: String (URL别名)
- description: String (描述)
- color: String (颜色)
- icon: String (图标)
- sortOrder: Integer (排序)
- isActive: Boolean (是否激活)
- articleCount: Long (文章数量)
- parent: Category (父分类)
- children: Set<Category> (子分类)
```

### **4. Tag (标签实体)**
```java
- tid: String (主键)
- name: String (标签名)
- slug: String (URL别名)
- description: String (描述)
- color: String (颜色)
- articleCount: Long (文章数量)
- isActive: Boolean (是否激活)
```

### **5. Comment (评论实体)**
```java
- cmid: String (主键)
- content: String (内容)
- authorName: String (作者名)
- authorEmail: String (作者邮箱)
- authorWebsite: String (作者网站)
- authorIp: String (IP地址)
- userAgent: String (用户代理)
- status: CommentStatus (状态)
- likeCount: Long (点赞数)
- isAuthor: Boolean (是否作者)
- articleId: String (文章ID)
- userId: String (用户ID)
- parentId: String (父评论ID)
- parent: Comment (父评论)
- replies: Set<Comment> (回复集合)
```

### **6. Like (点赞实体)**
```java
- lid: String (主键)
- userId: String (用户ID)
- targetId: String (目标ID)
- targetType: LikeType (目标类型)
- createdAt: LocalDateTime (创建时间)
```

### **7. File (文件实体)**
```java
- fid: String (主键)
- originalName: String (原始文件名)
- fileName: String (存储文件名)
- filePath: String (文件路径)
- fileUrl: String (访问URL)
- fileSize: Long (文件大小)
- mimeType: String (MIME类型)
- fileType: FileType (文件类型)
- width: Integer (宽度)
- height: Integer (高度)
- uploaderId: String (上传者ID)
- isPublic: Boolean (是否公开)
- downloadCount: Long (下载次数)
```

### **8. Setting (系统设置实体)**
```java
- sid: String (主键)
- keyName: String (键名)
- value: String (值)
- description: String (描述)
- settingType: SettingType (设置类型)
- isPublic: Boolean (是否公开)
- sortOrder: Integer (排序)
```

## 🎯 **功能特性**

### **文章管理**
- ✅ 文章CRUD操作
- ✅ 文章状态管理（草稿、发布、归档）
- ✅ 文章分类和标签
- ✅ 文章统计（浏览、点赞、评论）
- ✅ 文章置顶和精选
- ✅ 文章搜索和筛选

### **用户系统**
- ✅ 用户注册和登录
- ✅ 用户状态管理
- ✅ 基于角色的权限控制
- ✅ 用户资料管理

### **评论系统**
- ✅ 多级评论回复
- ✅ 评论审核机制
- ✅ 评论点赞功能
- ✅ 游客评论支持

### **文件管理**
- ✅ 文件上传和存储
- ✅ 图片处理（尺寸、压缩）
- ✅ 文件类型分类
- ✅ 文件访问控制

### **系统设置**
- ✅ 网站基础配置
- ✅ 功能开关控制
- ✅ 主题和样式设置
- ✅ 邮件和通知配置

## 🚀 **扩展建议**

### **短期优化**
1. 添加文章版本控制
2. 实现文章草稿自动保存
3. 添加文章收藏功能
4. 实现文章分享统计

### **中期功能**
1. 添加用户关注系统
2. 实现文章推荐算法
3. 添加全文搜索功能
4. 实现邮件通知系统

### **长期规划**
1. 添加多语言支持
2. 实现主题系统
3. 添加API接口
4. 实现移动端适配

## 📝 **数据库设计原则**

1. **规范化设计** - 避免数据冗余
2. **索引优化** - 提高查询性能
3. **外键约束** - 保证数据完整性
4. **软删除** - 保留历史数据
5. **审计字段** - 记录操作历史
6. **UUID主键** - 分布式友好

这个实体结构设计符合现代博客系统的标准，具有良好的扩展性和维护性。
