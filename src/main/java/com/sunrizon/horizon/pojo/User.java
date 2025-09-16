package com.sunrizon.horizon.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.sunrizon.horizon.enums.UserStatus;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "user")
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable {

  private static final long serialVersionUID = -6249791470254664710L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "uid", nullable = false, length = 36, updatable = false)
  private String uid;

  @Column(name = "username", nullable = false, length = 50, unique = true)
  private String username;

  @Column(name = "email", nullable = false, length = 100, unique = true)
  private String email;

  @Column(name = "password", nullable = false, length = 255)
  private String password;

  @Column(name = "avatar")
  private String avatar;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private UserStatus status = UserStatus.PENDING;

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  /** Roles assigned to this user */
  @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "uid"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "rid"))
  private Set<Role> roles;
}
