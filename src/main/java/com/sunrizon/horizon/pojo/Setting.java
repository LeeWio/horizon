package com.sunrizon.horizon.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.sunrizon.horizon.enums.SettingType;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 系统设置实体类
 * 用于存储博客系统的各种配置
 */
@Entity
@Table(name = "setting", uniqueConstraints = {
  @UniqueConstraint(columnNames = {"key_name"})
})
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Setting implements Serializable {

  private static final long serialVersionUID = -6249791470254664727L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "sid", nullable = false, length = 36, updatable = false)
  private String sid;

  @Column(name = "key_name", nullable = false, length = 100, unique = true)
  private String keyName;

  @Column(name = "value", columnDefinition = "TEXT")
  private String value;

  @Column(name = "description", length = 500)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "setting_type", nullable = false, length = 20)
  private SettingType settingType;

  @Column(name = "is_public", nullable = false)
  private Boolean isPublic = false; // 是否公开设置

  @Column(name = "sort_order", nullable = false)
  private Integer sortOrder = 0;

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
