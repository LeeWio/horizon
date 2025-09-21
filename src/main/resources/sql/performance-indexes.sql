-- 性能优化索引脚本
-- 为常用查询字段添加索引，提升查询性能

-- 用户表索引
CREATE INDEX IF NOT EXISTS idx_user_email ON user(email);
CREATE INDEX IF NOT EXISTS idx_user_username ON user(username);
CREATE INDEX IF NOT EXISTS idx_user_status ON user(status);
CREATE INDEX IF NOT EXISTS idx_user_created_at ON user(created_at);

-- 文章表索引
CREATE INDEX IF NOT EXISTS idx_article_author_id ON article(author_id);
CREATE INDEX IF NOT EXISTS idx_article_status ON article(status);
CREATE INDEX IF NOT EXISTS idx_article_published_at ON article(published_at);
CREATE INDEX IF NOT EXISTS idx_article_featured ON article(is_featured);
CREATE INDEX IF NOT EXISTS idx_article_top ON article(is_top);
CREATE INDEX IF NOT EXISTS idx_article_created_at ON article(created_at);
CREATE INDEX IF NOT EXISTS idx_article_updated_at ON article(updated_at);

-- 文章状态和发布时间复合索引
CREATE INDEX IF NOT EXISTS idx_article_status_published ON article(status, published_at);
CREATE INDEX IF NOT EXISTS idx_article_author_status ON article(author_id, status);

-- 文章统计表索引
CREATE INDEX IF NOT EXISTS idx_article_stats_article_id ON article_stats(article_id);
CREATE INDEX IF NOT EXISTS idx_article_stats_view_count ON article_stats(view_count);
CREATE INDEX IF NOT EXISTS idx_article_stats_like_count ON article_stats(like_count);

-- 用户统计表索引
CREATE INDEX IF NOT EXISTS idx_user_stats_user_id ON user_stats(user_id);
CREATE INDEX IF NOT EXISTS idx_user_stats_article_count ON user_stats(article_count);

-- 分类表索引
CREATE INDEX IF NOT EXISTS idx_category_name ON category(name);
CREATE INDEX IF NOT EXISTS idx_category_slug ON category(slug);
CREATE INDEX IF NOT EXISTS idx_category_parent_id ON category(parent_id);
CREATE INDEX IF NOT EXISTS idx_category_created_at ON category(created_at);

-- 标签表索引
CREATE INDEX IF NOT EXISTS idx_tag_name ON tag(name);
CREATE INDEX IF NOT EXISTS idx_tag_slug ON tag(slug);
CREATE INDEX IF NOT EXISTS idx_tag_created_at ON tag(created_at);

-- 文章分类关联表索引
CREATE INDEX IF NOT EXISTS idx_article_category_article_id ON article_category(article_id);
CREATE INDEX IF NOT EXISTS idx_article_category_category_id ON article_category(category_id);
CREATE INDEX IF NOT EXISTS idx_article_category_composite ON article_category(article_id, category_id);

-- 文章标签关联表索引
CREATE INDEX IF NOT EXISTS idx_article_tag_article_id ON article_tag(article_id);
CREATE INDEX IF NOT EXISTS idx_article_tag_tag_id ON article_tag(tag_id);
CREATE INDEX IF NOT EXISTS idx_article_tag_composite ON article_tag(article_id, tag_id);

-- 评论表索引
CREATE INDEX IF NOT EXISTS idx_comment_article_id ON comment(article_id);
CREATE INDEX IF NOT EXISTS idx_comment_user_id ON comment(user_id);
CREATE INDEX IF NOT EXISTS idx_comment_status ON comment(status);
CREATE INDEX IF NOT EXISTS idx_comment_created_at ON comment(created_at);
CREATE INDEX IF NOT EXISTS idx_comment_parent_id ON comment(parent_comment_id);

-- 点赞表索引
CREATE INDEX IF NOT EXISTS idx_likes_user_id ON likes(user_id);
CREATE INDEX IF NOT EXISTS idx_likes_target_id ON likes(target_id);
CREATE INDEX IF NOT EXISTS idx_likes_target_type ON likes(like_type);
CREATE INDEX IF NOT EXISTS idx_likes_composite ON likes(user_id, target_id, like_type);

-- 文件表索引
CREATE INDEX IF NOT EXISTS idx_file_uploader_id ON file(uploader_id);
CREATE INDEX IF NOT EXISTS idx_file_type ON file(file_type);
CREATE INDEX IF NOT EXISTS idx_file_uploaded_at ON file(uploaded_at);

-- 系统设置表索引
CREATE INDEX IF NOT EXISTS idx_setting_type ON setting(setting_type);
CREATE INDEX IF NOT EXISTS idx_setting_key ON setting(setting_key);

-- 角色表索引
CREATE INDEX IF NOT EXISTS idx_role_name ON role(name);

-- 权限表索引
CREATE INDEX IF NOT EXISTS idx_permission_name ON permission(name);

-- 用户角色关联表索引
CREATE INDEX IF NOT EXISTS idx_user_role_user_id ON user_role(uid);
CREATE INDEX IF NOT EXISTS idx_user_role_role_id ON user_role(rid);

-- 角色权限关联表索引
CREATE INDEX IF NOT EXISTS idx_role_permission_role_id ON role_permission(rid);
CREATE INDEX IF NOT EXISTS idx_role_permission_permission_id ON role_permission(pid);

-- 全文搜索索引（MySQL 5.7+）
-- 为文章标题和内容创建全文搜索索引
ALTER TABLE article ADD FULLTEXT(title, content) WITH PARSER ngram;

-- 为文章摘要创建全文搜索索引
ALTER TABLE article ADD FULLTEXT(summary) WITH PARSER ngram;
