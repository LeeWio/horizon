-- Horizon Blog Platform 初始化数据
-- 创建时间: 2025-09-21
-- 描述: 初始化博客平台的基础数据，包括用户、角色、权限等

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- 1. 插入权限数据
-- =============================================
INSERT INTO permission (pid, name, description) VALUES
('perm_001', 'USER_CREATE', '创建用户权限'),
('perm_002', 'USER_READ', '查看用户权限'),
('perm_003', 'USER_UPDATE', '更新用户权限'),
('perm_004', 'USER_DELETE', '删除用户权限'),
('perm_005', 'USER_ASSIGN_ROLE', '分配用户角色权限'),
('perm_006', 'ROLE_CREATE', '创建角色权限'),
('perm_007', 'ROLE_READ', '查看角色权限'),
('perm_008', 'ROLE_UPDATE', '更新角色权限'),
('perm_009', 'ROLE_DELETE', '删除角色权限'),
('perm_010', 'ARTICLE_CREATE', '创建文章权限'),
('perm_011', 'ARTICLE_READ', '查看文章权限'),
('perm_012', 'ARTICLE_UPDATE', '更新文章权限'),
('perm_013', 'ARTICLE_DELETE', '删除文章权限'),
('perm_014', 'ARTICLE_PUBLISH', '发布文章权限'),
('perm_015', 'ARTICLE_ARCHIVE', '归档文章权限');

-- =============================================
-- 2. 插入角色数据
-- =============================================
INSERT INTO role (rid, name, description, created_at, updated_at) VALUES
('role_001', 'ADMIN', '管理员', NOW(), NOW()),
('role_002', 'EDITOR', '编辑', NOW(), NOW()),
('role_003', 'USER', '普通用户', NOW(), NOW());

-- =============================================
-- 3. 分配角色权限
-- =============================================
-- 管理员拥有所有权限
INSERT INTO role_permission (rid, pid) VALUES
('role_001', 'perm_001'), ('role_001', 'perm_002'), ('role_001', 'perm_003'), ('role_001', 'perm_004'),
('role_001', 'perm_005'), ('role_001', 'perm_006'), ('role_001', 'perm_007'), ('role_001', 'perm_008'),
('role_001', 'perm_009'), ('role_001', 'perm_010'), ('role_001', 'perm_011'), ('role_001', 'perm_012'),
('role_001', 'perm_013'), ('role_001', 'perm_014'), ('role_001', 'perm_015');

-- 编辑权限
INSERT INTO role_permission (rid, pid) VALUES
('role_002', 'perm_002'), ('role_002', 'perm_010'), ('role_002', 'perm_011'), ('role_002', 'perm_012'),
('role_002', 'perm_013'), ('role_002', 'perm_014'), ('role_002', 'perm_015');

-- 普通用户权限
INSERT INTO role_permission (rid, pid) VALUES
('role_003', 'perm_002'), ('role_003', 'perm_010');

-- =============================================
-- 4. 插入用户数据
-- =============================================
-- 注意：密码使用BCrypt加密，这里使用一个示例加密后的密码
-- 实际使用时，建议通过应用程序的密码加密功能生成
-- 密码: Wei.Li.Laba00
INSERT INTO user (uid, username, email, password, avatar, status, created_at, updated_at) VALUES
('user_001', 'wei.li', '3499508634@qq.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, 'ACTIVE', NOW(), NOW());

-- =============================================
-- 5. 分配用户角色
-- =============================================
-- 将wei.li用户分配为管理员
INSERT INTO user_role (uid, rid) VALUES
('user_001', 'role_001');

-- =============================================
-- 6. 创建用户统计记录
-- =============================================
INSERT INTO user_stats (usid, uid, article_count, comment_count, like_received_count, like_given_count, followers_count, following_count, created_at, updated_at) VALUES
('us_001', 'user_001', 0, 0, 0, 0, 0, 0, NOW(), NOW());

-- =============================================
-- 7. 插入示例分类数据
-- =============================================
INSERT INTO category (cid, name, slug, description, color, icon, sort_order, is_active, article_count, parent_id, created_at, updated_at) VALUES
('cat_001', '技术分享', 'tech-share', '分享技术相关的文章和心得', '#FF5722', 'fas fa-code', 1, true, 0, NULL, NOW(), NOW()),
('cat_002', '生活随笔', 'life-essay', '记录生活中的点点滴滴', '#4CAF50', 'fas fa-heart', 2, true, 0, NULL, NOW(), NOW()),
('cat_003', '学习笔记', 'study-notes', '学习过程中的笔记和总结', '#2196F3', 'fas fa-book', 3, true, 0, NULL, NOW(), NOW()),
('cat_004', '项目经验', 'project-experience', '项目开发中的经验分享', '#FF9800', 'fas fa-project-diagram', 4, true, 0, NULL, NOW(), NOW()),
('cat_005', '前端开发', 'frontend', '前端技术相关文章', '#9C27B0', 'fab fa-html5', 1, true, 0, 'cat_001', NOW(), NOW()),
('cat_006', '后端开发', 'backend', '后端技术相关文章', '#607D8B', 'fas fa-server', 2, true, 0, 'cat_001', NOW(), NOW());

-- =============================================
-- 8. 插入示例标签数据
-- =============================================
INSERT INTO tag (tid, name, slug, description, color, is_active, article_count, created_at, updated_at) VALUES
('tag_001', 'Spring Boot', 'spring-boot', 'Spring Boot 相关技术', '#4CAF50', true, 0, NOW(), NOW()),
('tag_002', 'Java', 'java', 'Java 编程语言', '#F44336', true, 0, NOW(), NOW()),
('tag_003', 'MySQL', 'mysql', 'MySQL 数据库', '#FF9800', true, 0, NOW(), NOW()),
('tag_004', 'Redis', 'redis', 'Redis 缓存技术', '#E91E63', true, 0, NOW(), NOW()),
('tag_005', 'Vue.js', 'vuejs', 'Vue.js 前端框架', '#42A5F5', true, 0, NOW(), NOW()),
('tag_006', 'React', 'react', 'React 前端框架', '#66BB6A', true, 0, NOW(), NOW()),
('tag_007', 'Docker', 'docker', 'Docker 容器技术', '#26A69A', true, 0, NOW(), NOW()),
('tag_008', 'Kubernetes', 'kubernetes', 'Kubernetes 容器编排', '#AB47BC', true, 0, NOW(), NOW()),
('tag_009', '微服务', 'microservices', '微服务架构', '#FF7043', true, 0, NOW(), NOW()),
('tag_010', 'API设计', 'api-design', 'API 设计相关', '#8D6E63', true, 0, NOW(), NOW());

-- =============================================
-- 9. 插入示例文章数据
-- =============================================
INSERT INTO article (aid, title, slug, summary, content, cover_image, status, author_id, is_featured, is_top, published_at, created_at, updated_at, created_by, updated_by) VALUES
('art_001', 'Spring Boot 最佳实践指南', 'spring-boot-best-practices', '本文介绍了Spring Boot开发中的最佳实践，包括项目结构、配置管理、异常处理等方面。', 'Spring Boot 是一个优秀的Java框架，它简化了Spring应用的开发过程。在本文中，我们将探讨Spring Boot开发中的最佳实践...', NULL, 'PUBLISHED', 'user_001', true, true, NOW(), NOW(), NOW(), 'user_001', 'user_001'),
('art_002', 'MySQL 性能优化技巧', 'mysql-performance-optimization', '分享一些MySQL数据库性能优化的实用技巧，帮助提升应用性能。', 'MySQL 作为最流行的关系型数据库之一，性能优化是每个开发者都需要掌握的技能。本文将介绍一些实用的优化技巧...', NULL, 'PUBLISHED', 'user_001', false, false, NOW(), NOW(), NOW(), 'user_001', 'user_001'),
('art_003', 'Vue.js 3.0 新特性解析', 'vuejs-3-features', '深入解析Vue.js 3.0的新特性和改进，帮助开发者快速上手。', 'Vue.js 3.0 带来了许多令人兴奋的新特性，包括Composition API、更好的TypeScript支持等。让我们一起来看看这些新特性...', NULL, 'DRAFT', 'user_001', false, false, NULL, NOW(), NOW(), 'user_001', 'user_001');

-- =============================================
-- 10. 关联文章和分类
-- =============================================
INSERT INTO article_category (acid, article_id, category_id, sort_order, created_at) VALUES
('ac_001', 'art_001', 'cat_006', 1, NOW()),  -- Spring Boot 文章属于后端开发分类
('ac_002', 'art_002', 'cat_006', 1, NOW()),  -- MySQL 文章属于后端开发分类
('ac_003', 'art_003', 'cat_005', 1, NOW());  -- Vue.js 文章属于前端开发分类

-- =============================================
-- 11. 关联文章和标签
-- =============================================
INSERT INTO article_tag (atid, article_id, tag_id, sort_order, created_at) VALUES
('at_001', 'art_001', 'tag_001', 1, NOW()), ('at_002', 'art_001', 'tag_002', 2, NOW()), ('at_003', 'art_001', 'tag_009', 3, NOW()),  -- Spring Boot 文章标签
('at_004', 'art_002', 'tag_002', 1, NOW()), ('at_005', 'art_002', 'tag_003', 2, NOW()), ('at_006', 'art_002', 'tag_004', 3, NOW()),  -- MySQL 文章标签
('at_007', 'art_003', 'tag_005', 1, NOW()), ('at_008', 'art_003', 'tag_006', 2, NOW());  -- Vue.js 文章标签

-- =============================================
-- 12. 创建文章统计记录
-- =============================================
INSERT INTO article_stats (asid, article_id, view_count, like_count, comment_count, share_count, favorite_count, created_at, updated_at) VALUES
('as_001', 'art_001', 156, 23, 8, 5, 12, NOW(), NOW()),
('as_002', 'art_002', 89, 15, 3, 2, 7, NOW(), NOW()),
('as_003', 'art_003', 0, 0, 0, 0, 0, NOW(), NOW());

-- =============================================
-- 13. 插入系统设置数据
-- =============================================
INSERT INTO setting (sid, key_name, value, description, setting_type, is_public, sort_order, created_at, updated_at) VALUES
('set_001', 'site.title', 'Horizon Blog Platform', '网站标题', 'STRING', true, 1, NOW(), NOW()),
('set_002', 'site.description', '一个现代化的博客平台，基于Spring Boot构建', '网站描述', 'STRING', true, 2, NOW(), NOW()),
('set_003', 'site.keywords', '博客,Spring Boot,Java,技术分享', '网站关键词', 'STRING', true, 3, NOW(), NOW()),
('set_004', 'site.author', 'wei.li', '网站作者', 'STRING', true, 4, NOW(), NOW()),
('set_005', 'site.email', '3499508634@qq.com', '联系邮箱', 'STRING', true, 5, NOW(), NOW()),
('set_006', 'site.logo', '/images/logo.png', '网站Logo', 'STRING', true, 6, NOW(), NOW()),
('set_007', 'site.favicon', '/images/favicon.ico', '网站图标', 'STRING', true, 7, NOW(), NOW()),
('set_008', 'site.copyright', '© 2025 Horizon Blog Platform. All rights reserved.', '版权信息', 'STRING', true, 8, NOW(), NOW()),
('set_009', 'site.analytics', '', '网站统计代码', 'TEXT', false, 9, NOW(), NOW()),
('set_010', 'site.comment.enabled', 'true', '是否启用评论', 'BOOLEAN', true, 10, NOW(), NOW()),
('set_011', 'site.registration.enabled', 'true', '是否允许注册', 'BOOLEAN', true, 11, NOW(), NOW()),
('set_012', 'site.upload.max_size', '10485760', '文件上传最大大小（字节）', 'NUMBER', false, 12, NOW(), NOW()),
('set_013', 'site.upload.allowed_types', 'jpg,jpeg,png,gif,pdf,doc,docx', '允许上传的文件类型', 'STRING', false, 13, NOW(), NOW()),
('set_014', 'site.pagination.size', '10', '分页大小', 'NUMBER', false, 14, NOW(), NOW()),
('set_015', 'site.cache.enabled', 'true', '是否启用缓存', 'BOOLEAN', false, 15, NOW(), NOW());

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 显示插入结果
SELECT '数据初始化完成！' as message;
SELECT COUNT(*) as user_count FROM user;
SELECT COUNT(*) as role_count FROM role;
SELECT COUNT(*) as permission_count FROM permission;
SELECT COUNT(*) as category_count FROM category;
SELECT COUNT(*) as tag_count FROM tag;
SELECT COUNT(*) as article_count FROM article;
SELECT COUNT(*) as setting_count FROM setting;
