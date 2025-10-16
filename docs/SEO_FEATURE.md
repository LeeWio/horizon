# SEO 优化功能文档

## 功能概述

Horizon 博客系统的 SEO 优化功能提供了全面的搜索引擎优化支持，包括：

- 动态生成 sitemap.xml
- 自定义 robots.txt
- 文章级别的 SEO 元数据管理
- Open Graph 社交媒体标签
- Twitter Card 支持
- JSON-LD 结构化数据
- 全局 SEO 配置管理

---

## 数据库设计

### 1. seo_config 表

全局 SEO 配置表，用于存储网站级别的 SEO 参数。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | varchar(36) | 主键 UUID |
| config_key | varchar(100) | 配置键（唯一） |
| config_value | text | 配置值 |
| description | varchar(500) | 配置描述 |
| config_group | varchar(50) | 配置分组 |
| is_enabled | bit(1) | 是否启用 |
| created_at | datetime(6) | 创建时间 |
| updated_at | datetime(6) | 更新时间 |

**索引**：
- PRIMARY KEY (id)
- UNIQUE KEY idx_seo_config_key (config_key)

### 2. article_seo 表

文章 SEO 元数据表，每篇文章一条记录。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | varchar(36) | 主键 UUID |
| article_id | varchar(36) | 文章ID（唯一，外键） |
| seo_title | varchar(150) | SEO 标题 |
| seo_description | varchar(300) | SEO 描述 |
| seo_keywords | varchar(500) | SEO 关键词 |
| canonical_url | varchar(500) | 规范 URL |
| og_title | varchar(150) | Open Graph 标题 |
| og_description | varchar(300) | Open Graph 描述 |
| og_image | varchar(500) | Open Graph 图片 |
| og_type | varchar(50) | Open Graph 类型 |
| twitter_card | varchar(50) | Twitter Card 类型 |
| twitter_site | varchar(100) | Twitter 站点账号 |
| schema_json | text | JSON-LD 结构化数据 |
| robots_meta | varchar(100) | robots meta 标签 |
| include_in_sitemap | bit(1) | 是否包含在sitemap中 |
| sitemap_priority | double | sitemap 优先级 (0.0-1.0) |
| sitemap_changefreq | varchar(20) | sitemap 更新频率 |
| created_at | datetime(6) | 创建时间 |
| updated_at | datetime(6) | 更新时间 |

**索引**：
- PRIMARY KEY (id)
- UNIQUE KEY idx_article_seo_aid (article_id)
- INDEX idx_article_seo_canonical (canonical_url)

---

## API 接口文档

### 基础路径

所有 SEO 相关接口的基础路径为：`/api/seo`

---

### 1. 获取或创建文章 SEO 元数据

**接口**: `GET /api/seo/article/{articleId}`

**描述**: 根据文章 ID 获取 SEO 元数据，如果不存在则自动创建默认值。

**请求参数**:
- `articleId` (path): 文章 ID

**响应示例**:
```json
{
  "code": 200,
  "message": "Request processed successfully",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "articleId": "article-uuid",
    "seoTitle": "文章标题",
    "seoDescription": "文章摘要",
    "seoKeywords": "Java,Spring Boot,博客",
    "canonicalUrl": null,
    "ogTitle": "文章标题",
    "ogDescription": "文章摘要",
    "ogImage": "https://example.com/cover.jpg",
    "ogType": "article",
    "twitterCard": "summary_large_image",
    "twitterSite": null,
    "schemaJson": null,
    "robotsMeta": "index,follow",
    "includeInSitemap": true,
    "sitemapPriority": 0.7,
    "sitemapChangefreq": "weekly",
    "createdAt": "2025-10-16T12:00:00",
    "updatedAt": "2025-10-16T12:00:00"
  },
  "timestamp": "2025-10-16T12:00:00",
  "success": true
}
```

---

### 2. 更新文章 SEO 元数据

**接口**: `PUT /api/seo/article/{articleId}`

**描述**: 更新文章的 SEO 元数据。

**请求参数**:
- `articleId` (path): 文章 ID

**请求体**:
```json
{
  "seoTitle": "自定义SEO标题",
  "seoDescription": "自定义SEO描述",
  "seoKeywords": "关键词1,关键词2,关键词3",
  "canonicalUrl": "https://blog.example.com/articles/my-article",
  "ogTitle": "社交分享标题",
  "ogDescription": "社交分享描述",
  "ogImage": "https://example.com/social-image.jpg",
  "twitterCard": "summary_large_image",
  "robotsMeta": "index,follow",
  "includeInSitemap": true,
  "sitemapPriority": 0.8,
  "sitemapChangefreq": "daily"
}
```

**响应**: 同"获取或创建文章 SEO 元数据"

---

### 3. 删除文章 SEO 元数据

**接口**: `DELETE /api/seo/article/{articleId}`

**权限**: 需要 ADMIN 或 MODERATOR 角色

**描述**: 删除文章的 SEO 元数据。

**请求参数**:
- `articleId` (path): 文章 ID

**响应示例**:
```json
{
  "code": 200,
  "message": "Request processed successfully",
  "data": "SEO metadata deleted successfully",
  "timestamp": "2025-10-16T12:00:00",
  "success": true
}
```

---

### 4. 生成 sitemap.xml

**接口**: `GET /api/seo/sitemap.xml`

**描述**: 生成网站的 sitemap.xml 文件（XML 格式）。

**请求参数**:
- `baseUrl` (query, optional): 网站基础 URL，默认 `https://example.com`

**响应类型**: `application/xml`

**响应示例**:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
  <url>
    <loc>https://blog.example.com/</loc>
    <changefreq>daily</changefreq>
    <priority>1.0</priority>
  </url>
  <url>
    <loc>https://blog.example.com/articles/java-tutorial</loc>
    <lastmod>2025-10-16T12:00:00+08:00</lastmod>
    <changefreq>weekly</changefreq>
    <priority>0.7</priority>
  </url>
</urlset>
```

---

### 5. 生成 sitemap（JSON 格式）

**接口**: `GET /api/seo/sitemap`

**描述**: 生成 sitemap.xml 内容并以 JSON 格式返回（用于预览）。

**请求参数**:
- `baseUrl` (query, optional): 网站基础 URL

**响应示例**:
```json
{
  "code": 200,
  "message": "Request processed successfully",
  "data": "<?xml version=\"1.0\" encoding=\"UTF-8\"?>...</urlset>",
  "timestamp": "2025-10-16T12:00:00",
  "success": true
}
```

---

### 6. 获取 robots.txt

**接口**: `GET /api/seo/robots.txt`

**描述**: 获取 robots.txt 文件内容（纯文本格式）。

**响应类型**: `text/plain`

**响应示例**:
```
User-agent: *
Allow: /
Disallow: /api/
Disallow: /admin/

Sitemap: https://example.com/sitemap.xml
```

---

### 7. 获取 robots.txt（JSON 格式）

**接口**: `GET /api/seo/robots`

**描述**: 获取 robots.txt 内容并以 JSON 格式返回。

**响应示例**:
```json
{
  "code": 200,
  "message": "Request processed successfully",
  "data": "User-agent: *\nAllow: /\nDisallow: /api/\nDisallow: /admin/\n\nSitemap: {baseUrl}/sitemap.xml\n",
  "timestamp": "2025-10-16T12:00:00",
  "success": true
}
```

---

### 8. 更新 robots.txt

**接口**: `PUT /api/seo/robots`

**权限**: 需要 ADMIN 角色

**描述**: 更新自定义 robots.txt 内容。

**请求体**:
```json
{
  "content": "User-agent: *\nAllow: /\nDisallow: /api/\nDisallow: /admin/\n\nSitemap: https://blog.example.com/sitemap.xml\n"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "Request processed successfully",
  "data": "Robots.txt updated successfully",
  "timestamp": "2025-10-16T12:00:00",
  "success": true
}
```

---

### 9. 获取 SEO 配置

**接口**: `GET /api/seo/config/{configKey}`

**描述**: 根据配置键获取 SEO 配置。

**请求参数**:
- `configKey` (path): 配置键（例如：site_name）

**响应示例**:
```json
{
  "code": 200,
  "message": "Request processed successfully",
  "data": {
    "id": "config-uuid",
    "configKey": "site_name",
    "configValue": "Horizon Blog",
    "description": "Website name",
    "configGroup": "basic",
    "isEnabled": true,
    "createdAt": "2025-10-16T12:00:00",
    "updatedAt": "2025-10-16T12:00:00"
  },
  "timestamp": "2025-10-16T12:00:00",
  "success": true
}
```

---

### 10. 保存 SEO 配置

**接口**: `POST /api/seo/config`

**权限**: 需要 ADMIN 角色

**描述**: 创建或更新 SEO 配置。

**请求体**:
```json
{
  "configKey": "site_name",
  "configValue": "Horizon Blog",
  "description": "Website name",
  "configGroup": "basic",
  "isEnabled": true
}
```

**响应**: 同"获取 SEO 配置"

---

### 11. 获取所有 SEO 配置

**接口**: `GET /api/seo/configs`

**描述**: 获取所有启用的 SEO 配置（按分组）。

**响应示例**:
```json
{
  "code": 200,
  "message": "Request processed successfully",
  "data": {
    "configs": {
      "basic": [
        {
          "id": "uuid1",
          "configKey": "site_name",
          "configValue": "Horizon Blog",
          "description": "Website name",
          "configGroup": "basic",
          "isEnabled": true,
          "createdAt": "2025-10-16T12:00:00",
          "updatedAt": "2025-10-16T12:00:00"
        }
      ],
      "social_media": [
        {
          "id": "uuid2",
          "configKey": "twitter_username",
          "configValue": "@horizonblog",
          "description": "Official Twitter account",
          "configGroup": "social_media",
          "isEnabled": true,
          "createdAt": "2025-10-16T12:00:00",
          "updatedAt": "2025-10-16T12:00:00"
        }
      ]
    },
    "total": 2
  },
  "timestamp": "2025-10-16T12:00:00",
  "success": true
}
```

---

### 12. 生成文章 JSON-LD

**接口**: `GET /api/seo/article/{articleId}/json-ld`

**描述**: 生成文章的 Schema.org JSON-LD 结构化数据。

**请求参数**:
- `articleId` (path): 文章 ID

**响应示例**:
```json
{
  "code": 200,
  "message": "Request processed successfully",
  "data": "{\n  \"@context\" : \"https://schema.org\",\n  \"@type\" : \"Article\",\n  \"headline\" : \"文章标题\",\n  \"description\" : \"文章摘要\",\n  \"image\" : \"https://example.com/cover.jpg\",\n  \"datePublished\" : \"2025-10-16T12:00:00\",\n  \"dateModified\" : \"2025-10-16T12:00:00\",\n  \"author\" : {\n    \"@type\" : \"Person\",\n    \"name\" : \"Author\"\n  },\n  \"publisher\" : {\n    \"@type\" : \"Organization\",\n    \"name\" : \"Horizon Blog\"\n  },\n  \"keywords\" : [ \"Java\", \"Spring Boot\", \"Blog\" ]\n}",
  "timestamp": "2025-10-16T12:00:00",
  "success": true
}
```

---

### 13. 批量生成文章 SEO 元数据

**接口**: `POST /api/seo/batch-generate`

**权限**: 需要 ADMIN 角色

**描述**: 为所有已发布但没有 SEO 配置的文章自动生成默认 SEO 元数据。

**响应示例**:
```json
{
  "code": 200,
  "message": "Request processed successfully",
  "data": 15,
  "timestamp": "2025-10-16T12:00:00",
  "success": true
}
```

---

### 14. 获取文章详情（含 SEO 元数据）

**接口**: `GET /api/article/{id}/detail`

**描述**: 获取文章详情及完整的 SEO 元数据（包括 meta 标签和 Open Graph 标签）。

**请求参数**:
- `id` (path): 文章 ID

**响应示例**:
```json
{
  "code": 200,
  "message": "Request processed successfully",
  "data": {
    "aid": "article-uuid",
    "title": "文章标题",
    "content": "文章内容...",
    "summary": "文章摘要",
    "coverImage": "https://example.com/cover.jpg",
    "status": "PUBLISHED",
    "viewCount": 1000,
    "seo": {
      "seoTitle": "自定义SEO标题",
      "seoDescription": "自定义SEO描述",
      "seoKeywords": "关键词1,关键词2",
      "canonicalUrl": "https://blog.example.com/articles/my-article",
      "ogTitle": "社交分享标题",
      "ogDescription": "社交分享描述",
      "ogImage": "https://example.com/social-image.jpg",
      "ogType": "article",
      "twitterCard": "summary_large_image",
      "twitterSite": "@horizonblog",
      "jsonLd": "{...JSON-LD结构化数据...}",
      "robotsMeta": "index,follow"
    },
    "metaTags": {
      "title": "自定义SEO标题",
      "description": "自定义SEO描述",
      "keywords": "关键词1,关键词2",
      "canonical": "https://blog.example.com/articles/my-article",
      "robots": "index,follow"
    },
    "ogTags": {
      "og:title": "社交分享标题",
      "og:description": "社交分享描述",
      "og:type": "article",
      "og:image": "https://example.com/social-image.jpg",
      "twitter:card": "summary_large_image",
      "twitter:site": "@horizonblog"
    },
    "stats": {
      "likeCount": 100,
      "favoriteCount": 50,
      "shareCount": 20,
      "commentCount": 0
    }
  },
  "timestamp": "2025-10-16T12:00:00",
  "success": true
}
```

---

## 前端集成指南

### 1. 在 HTML 页面中添加 meta 标签

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <!-- 基础 SEO meta 标签 -->
  <title>{{ article.seo.seoTitle || article.title }}</title>
  <meta name="description" content="{{ article.seo.seoDescription || article.summary }}">
  <meta name="keywords" content="{{ article.seo.seoKeywords }}">
  <meta name="robots" content="{{ article.seo.robotsMeta }}">
  
  <!-- Canonical URL -->
  <link rel="canonical" href="{{ article.seo.canonicalUrl || currentUrl }}">
  
  <!-- Open Graph 标签 -->
  <meta property="og:title" content="{{ article.seo.ogTitle || article.title }}">
  <meta property="og:description" content="{{ article.seo.ogDescription || article.summary }}">
  <meta property="og:type" content="{{ article.seo.ogType }}">
  <meta property="og:url" content="{{ article.seo.canonicalUrl || currentUrl }}">
  <meta property="og:image" content="{{ article.seo.ogImage || article.coverImage }}">
  
  <!-- Twitter Card 标签 -->
  <meta name="twitter:card" content="{{ article.seo.twitterCard }}">
  <meta name="twitter:site" content="{{ article.seo.twitterSite }}">
  <meta name="twitter:title" content="{{ article.seo.ogTitle || article.title }}">
  <meta name="twitter:description" content="{{ article.seo.ogDescription || article.summary }}">
  <meta name="twitter:image" content="{{ article.seo.ogImage || article.coverImage }}">
  
  <!-- JSON-LD 结构化数据 -->
  <script type="application/ld+json">
    {{ article.seo.jsonLd }}
  </script>
</head>
<body>
  <!-- 页面内容 -->
</body>
</html>
```

### 2. React/Vue 示例

```javascript
// 调用 API 获取文章详情
const fetchArticleDetail = async (articleId) => {
  const response = await fetch(`/api/article/${articleId}/detail`);
  const result = await response.json();
  
  if (result.success) {
    const article = result.data;
    
    // 动态设置 meta 标签
    document.title = article.metaTags.title;
    
    // 使用 react-helmet 或 vue-meta 库更新 meta 标签
    // ...
  }
};
```

---

## 最佳实践

### 1. SEO 标题优化

- 长度：50-60 个字符
- 包含主要关键词
- 独特且描述性强
- 避免关键词堆砌

### 2. SEO 描述优化

- 长度：150-160 个字符
- 包含主要关键词
- 提供文章价值概述
- 包含行动号召（CTA）

### 3. 关键词选择

- 使用 3-5 个相关关键词
- 包含长尾关键词
- 避免重复和无关关键词
- 逗号分隔

### 4. Open Graph 图片

- 推荐尺寸：1200x630 像素
- 最小尺寸：600x315 像素
- 格式：JPG 或 PNG
- 文件大小：< 8 MB

### 5. Sitemap 优先级

- 首页：1.0
- 重要页面：0.8-0.9
- 常规文章：0.6-0.7
- 次要页面：0.4-0.5

### 6. 更新频率

- 首页：daily
- 博客文章：weekly
- 静态页面：monthly
- 归档内容：yearly

---

## 常见问题

### Q1: 如何让搜索引擎识别 sitemap？

**A**: 在网站根目录提供 sitemap.xml，并在 robots.txt 中声明：
```
Sitemap: https://yourdomain.com/api/seo/sitemap.xml?baseUrl=https://yourdomain.com
```

或者在 Google Search Console 中手动提交 sitemap。

### Q2: 如何禁止某篇文章被搜索引擎索引？

**A**: 更新文章的 SEO 元数据：
```json
{
  "robotsMeta": "noindex,nofollow",
  "includeInSitemap": false
}
```

### Q3: 如何验证 Open Graph 标签是否正确？

**A**: 使用以下工具验证：
- Facebook Sharing Debugger: https://developers.facebook.com/tools/debug/
- Twitter Card Validator: https://cards-dev.twitter.com/validator
- LinkedIn Post Inspector: https://www.linkedin.com/post-inspector/

### Q4: JSON-LD 结构化数据有什么作用？

**A**: JSON-LD 帮助搜索引擎更好地理解页面内容，可能提升以下功能：
- 丰富搜索结果（Rich Snippets）
- 知识图谱（Knowledge Graph）
- 面包屑导航显示
- 文章元数据显示（作者、发布时间等）

---

## 更新日志

### v1.0.0 (2025-10-16)

**新增功能**：
- ✅ 文章级别 SEO 元数据管理
- ✅ 动态 sitemap.xml 生成
- ✅ 自定义 robots.txt 配置
- ✅ Open Graph 标签支持
- ✅ Twitter Card 支持
- ✅ JSON-LD 结构化数据生成
- ✅ 全局 SEO 配置管理
- ✅ 批量生成 SEO 元数据
- ✅ 文章详情 API 包含完整 SEO 数据

**数据库**：
- ✅ 自动创建 `seo_config` 表
- ✅ 自动创建 `article_seo` 表
- ✅ 包含所有必要索引

**测试**：
- ✅ robots.txt 生成测试通过
- ✅ sitemap.xml 生成测试通过
- ✅ API 接口测试通过
- ✅ 错误处理测试通过

---

**维护者**: Horizon Team  
**最后更新**: 2025-10-16
