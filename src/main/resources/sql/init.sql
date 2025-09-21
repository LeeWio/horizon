-- 博客系统数据库初始化脚本

-- 创建数据库（如果不存在）
-- CREATE DATABASE IF NOT EXISTS horizon_blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE horizon_blog;

-- 插入默认角色
INSERT INTO role (rid, name, description, created_at, updated_at) VALUES
('admin-role-uuid-1', 'ADMIN', '系统管理员，拥有所有权限', NOW(), NOW()),
('editor-role-uuid-2', 'EDITOR', '内容编辑，可以创建和管理文章', NOW(), NOW()),
('user-role-uuid-3', 'USER', '普通用户，可以评论和点赞', NOW(), NOW())
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- 插入默认权限
INSERT INTO permission (pid, name, description) VALUES
-- 文章权限
('perm-article-create', 'ARTICLE_CREATE', '创建文章'),
('perm-article-read', 'ARTICLE_READ', '查看文章'),
('perm-article-update', 'ARTICLE_UPDATE', '编辑文章'),
('perm-article-delete', 'ARTICLE_DELETE', '删除文章'),
('perm-article-publish', 'ARTICLE_PUBLISH', '发布文章'),
('perm-article-archive', 'ARTICLE_ARCHIVE', '归档文章'),

-- 用户权限
('perm-user-create', 'USER_CREATE', '创建用户'),
('perm-user-read', 'USER_READ', '查看用户'),
('perm-user-update', 'USER_UPDATE', '编辑用户'),
('perm-user-delete', 'USER_DELETE', '删除用户'),
('perm-user-assign-role', 'USER_ASSIGN_ROLE', '分配角色'),

-- 角色权限
('perm-role-create', 'ROLE_CREATE', '创建角色'),
('perm-role-read', 'ROLE_READ', '查看角色'),
('perm-role-update', 'ROLE_UPDATE', '编辑角色'),
('perm-role-delete', 'ROLE_DELETE', '删除角色'),

-- 分类权限
('perm-category-create', 'CATEGORY_CREATE', '创建分类'),
('perm-category-read', 'CATEGORY_READ', '查看分类'),
('perm-category-update', 'CATEGORY_UPDATE', '编辑分类'),
('perm-category-delete', 'CATEGORY_DELETE', '删除分类'),

-- 标签权限
('perm-tag-create', 'TAG_CREATE', '创建标签'),
('perm-tag-read', 'TAG_READ', '查看标签'),
('perm-tag-update', 'TAG_UPDATE', '编辑标签'),
('perm-tag-delete', 'TAG_DELETE', '删除标签'),

-- 评论权限
('perm-comment-create', 'COMMENT_CREATE', '创建评论'),
('perm-comment-read', 'COMMENT_READ', '查看评论'),
('perm-comment-update', 'COMMENT_UPDATE', '编辑评论'),
('perm-comment-delete', 'COMMENT_DELETE', '删除评论'),
('perm-comment-moderate', 'COMMENT_MODERATE', '审核评论'),

-- 文件权限
('perm-file-upload', 'FILE_UPLOAD', '上传文件'),
('perm-file-read', 'FILE_READ', '查看文件'),
('perm-file-delete', 'FILE_DELETE', '删除文件'),

-- 系统权限
('perm-setting-read', 'SETTING_READ', '查看设置'),
('perm-setting-update', 'SETTING_UPDATE', '更新设置')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- 分配权限给角色
-- 管理员拥有所有权限
INSERT INTO role_permission (rid, pid) 
SELECT 'admin-role-uuid-1', pid FROM permission
ON DUPLICATE KEY UPDATE rid = VALUES(rid);

-- 编辑者权限
INSERT INTO role_permission (rid, pid) VALUES
('editor-role-uuid-2', 'perm-article-create'),
('editor-role-uuid-2', 'perm-article-read'),
('editor-role-uuid-2', 'perm-article-update'),
('editor-role-uuid-2', 'perm-article-publish'),
('editor-role-uuid-2', 'perm-article-archive'),
('editor-role-uuid-2', 'perm-category-read'),
('editor-role-uuid-2', 'perm-tag-read'),
('editor-role-uuid-2', 'perm-comment-read'),
('editor-role-uuid-2', 'perm-comment-moderate'),
('editor-role-uuid-2', 'perm-file-upload'),
('editor-role-uuid-2', 'perm-file-read')
ON DUPLICATE KEY UPDATE rid = VALUES(rid);

-- 普通用户权限
INSERT INTO role_permission (rid, pid) VALUES
('user-role-uuid-3', 'perm-article-read'),
('user-role-uuid-3', 'perm-comment-create'),
('user-role-uuid-3', 'perm-comment-read'),
('user-role-uuid-3', 'perm-file-read')
ON DUPLICATE KEY UPDATE rid = VALUES(rid);

-- 插入默认分类
INSERT INTO category (cid, name, slug, description, color, icon, sort_order, is_active, article_count, created_at, updated_at) VALUES
('cat-tech-uuid-1', '技术', 'tech', '技术相关文章', '#2196F3', 'fas fa-code', 1, true, 0, NOW(), NOW()),
('cat-life-uuid-2', '生活', 'life', '生活感悟分享', '#4CAF50', 'fas fa-heart', 2, true, 0, NOW(), NOW()),
('cat-travel-uuid-3', '旅行', 'travel', '旅行见闻记录', '#FF9800', 'fas fa-plane', 3, true, 0, NOW(), NOW()),
('cat-photo-uuid-4', '摄影', 'photo', '摄影作品展示', '#9C27B0', 'fas fa-camera', 4, true, 0, NOW(), NOW())
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- 插入默认标签
INSERT INTO tag (tid, name, slug, description, color, article_count, is_active, created_at, updated_at) VALUES
('tag-spring-uuid-1', 'Spring Boot', 'spring-boot', 'Spring Boot 相关技术', '#6DB33F', 0, true, NOW(), NOW()),
('tag-java-uuid-2', 'Java', 'java', 'Java 编程语言', '#F89820', 0, true, NOW(), NOW()),
('tag-mysql-uuid-3', 'MySQL', 'mysql', 'MySQL 数据库', '#4479A1', 0, true, NOW(), NOW()),
('tag-redis-uuid-4', 'Redis', 'redis', 'Redis 缓存', '#DC382D', 0, true, NOW(), NOW()),
('tag-docker-uuid-5', 'Docker', 'docker', 'Docker 容器化', '#2496ED', 0, true, NOW(), NOW())
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- 插入系统设置
INSERT INTO setting (sid, key_name, value, description, setting_type, is_public, sort_order, created_at, updated_at) VALUES
('setting-site-name', 'site.name', 'Horizon Blog', '网站名称', 'STRING', true, 1, NOW(), NOW()),
('setting-site-description', 'site.description', '一个现代化的博客平台', '网站描述', 'STRING', true, 2, NOW(), NOW()),
('setting-site-keywords', 'site.keywords', '博客,技术,生活,分享', '网站关键词', 'STRING', true, 3, NOW(), NOW()),
('setting-site-logo', 'site.logo', '/images/logo.png', '网站Logo', 'STRING', true, 4, NOW(), NOW()),
('setting-comment-enabled', 'comment.enabled', 'true', '是否启用评论功能', 'BOOLEAN', true, 5, NOW(), NOW()),
('setting-comment-approval', 'comment.approval', 'true', '评论是否需要审核', 'BOOLEAN', false, 6, NOW(), NOW()),
('setting-article-per-page', 'article.per_page', '10', '每页文章数量', 'NUMBER', true, 7, NOW(), NOW()),
('setting-registration-enabled', 'registration.enabled', 'true', '是否允许用户注册', 'BOOLEAN', true, 8, NOW(), NOW())
ON DUPLICATE KEY UPDATE value = VALUES(value);
