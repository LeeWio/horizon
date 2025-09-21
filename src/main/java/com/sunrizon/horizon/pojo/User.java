package com.sunrizon.horizon.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.sunrizon.horizon.enums.UserStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table(name = "user")
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Schema(name = "User", description = "用户实体")
public class User implements Serializable {

  private static final long serialVersionUID = -6249791470254664710L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "uid", nullable = false, length = 36, updatable = false)
  @Schema(description = "用户ID")
  private String uid;

  @Column(name = "username", nullable = false, length = 50, unique = true)
  @Schema(description = "用户名", example = "john_doe")
  private String username;

  @Column(name = "email", nullable = false, length = 100, unique = true)
  @Schema(description = "邮箱地址", example = "john@example.com")
  private String email;

  @Column(name = "password", nullable = false, length = 255)
  @Schema(description = "密码（加密后）")
  private String password;

  @Column(name = "avatar")
  @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
  private String avatar;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  @Schema(description = "用户状态", example = "ACTIVE")
  private UserStatus status = UserStatus.PENDING;

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  /** Roles assigned to this user */
  @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "uid", referencedColumnName = "uid"), inverseJoinColumns = @JoinColumn(name = "rid", referencedColumnName = "rid"))
  private Set<Role> roles;

  // 关联关系
  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private UserStats stats;

  @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<Article> articles;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<Comment> comments;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<Like> likes;

  @OneToMany(mappedBy = "uploader", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<File> files;
}
