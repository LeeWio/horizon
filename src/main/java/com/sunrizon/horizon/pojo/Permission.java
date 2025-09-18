package com.sunrizon.horizon.pojo;

import java.io.Serializable;
import java.util.Set;

import com.sunrizon.horizon.enums.PermissionType;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a system permission.
 * <p>
 * Each permission can be assigned to multiple roles.
 */
@Entity
@Table(name = "permission")
@Setter
@Getter
public class Permission implements Serializable {

  private static final long serialVersionUID = -6249791470254664721L;

  /** Primary key, UUID */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "pid", nullable = false, length = 36, updatable = false)
  private String pid;

  /** Permission name, should be unique, e.g., 'ARTICLE_CREATE' */
  @Enumerated(EnumType.STRING)
  @Column(name = "name", nullable = false, length = 50, unique = true, updatable = false)
  private PermissionType name;

  /** Optional description */
  @Column(name = "description", length = 255)
  private String description;

  /** Roles that have this permission */
  @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
  private Set<Role> roles;
}
