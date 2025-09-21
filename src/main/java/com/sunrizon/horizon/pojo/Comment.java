package com.sunrizon.horizon.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.sunrizon.horizon.enums.CommentStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 评论实体类
 * 支持多级评论回复
 */
@Entity
@Table(name = "comment")
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
@Schema(name = "Comment", description = "评论实体")
public class Comment implements Serializable {

  private static final long serialVersionUID = -6249791470254664724L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "cmid", nullable = false, length = 36, updatable = false)
  private String cmid;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(name = "author_name", length = 100)
  private String authorName;

  @Column(name = "author_email", length = 100)
  private String authorEmail;

  @Column(name = "author_website", length = 200)
  private String authorWebsite;

  @Column(name = "author_ip", length = 45)
  private String authorIp;

  @Column(name = "user_agent", length = 500)
  private String userAgent;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private CommentStatus status = CommentStatus.PENDING;

  @Column(name = "like_count", nullable = false)
  private Long likeCount = 0L;

  @Column(name = "is_author", nullable = false)
  private Boolean isAuthor = false; // 是否为文章作者评论

  @Column(name = "article_id", nullable = false, length = 36)
  private String articleId;

  @Column(name = "uid", length = 36)
  private String userId; // 注册用户ID，可为空（游客评论）

  @Column(name = "parent_id", length = 36)
  private String parentId; // 父评论ID，支持多级回复

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  // 关联关系
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "article_id", insertable = false, updatable = false)
  private Article article;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "uid", insertable = false, updatable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id", insertable = false, updatable = false)
  private Comment parent;

  @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<Comment> replies;
}
