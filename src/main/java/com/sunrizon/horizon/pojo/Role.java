package com.sunrizon.horizon.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.sunrizon.horizon.enums.RoleType;

/**
 * Represents a system role.
 * <p>
 * Each role has a fixed name (from RoleType) and can be assigned to multiple
 * users.
 */
@Entity
@Table(name = "role")
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Role implements Serializable {

  private static final long serialVersionUID = -6249691474254164720L;

  /** Primary key, UUID */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "rid", nullable = false, length = 36, updatable = false)
  private String rid;

  /** Role name, fixed via RoleType enum */
  @Enumerated(EnumType.STRING)
  @Column(name = "name", nullable = false, length = 50, unique = true, updatable = false)
  private RoleType name;

  /** Role description */
  @Column(name = "description", length = 255)
  private String description;

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  /** Users assigned to this role */
  @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
  private Set<User> users;

  @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  @JoinTable(name = "role_permission", joinColumns = @JoinColumn(name = "rid", referencedColumnName = "rid"), inverseJoinColumns = @JoinColumn(name = "pid", referencedColumnName = "pid"))
  private Set<Permission> permissions;
}
