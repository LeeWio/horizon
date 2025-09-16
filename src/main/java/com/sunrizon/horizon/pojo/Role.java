package com.sunrizon.horizon.pojo;

import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import com.sunrizon.horizon.enums.RoleType;

/**
 * Represents a system role.
 * <p>
 * Each role has a fixed name (from RoleType) and can be assigned to multiple
 * users.
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
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

  /** Users assigned to this role */
  @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
  private Set<User> users;

  @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  @JoinTable(name = "role_permission", joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "rid"), inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "pid"))
  private Set<Permission> permissions;
}
