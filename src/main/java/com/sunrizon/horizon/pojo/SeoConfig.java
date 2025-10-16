package com.sunrizon.horizon.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * SEO全局配置实体类
 * 用于配置网站级别的SEO参数
 */
@Table(name = "seo_config", indexes = {
    @Index(name = "idx_seo_config_key", columnList = "config_key", unique = true)
})
@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class SeoConfig implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "id", nullable = false, length = 36, updatable = false)
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  /**
   * 配置键（唯一）
   * 例如：site_name, site_description, default_keywords, robots_txt, etc.
   */
  @Column(name = "config_key", nullable = false, length = 100, unique = true)
  private String configKey;

  /**
   * 配置值
   */
  @Column(name = "config_value", columnDefinition = "TEXT")
  private String configValue;

  /**
   * 配置描述
   */
  @Column(name = "description", length = 500)
  private String description;

  /**
   * 配置分组（用于管理后台分类展示）
   * 例如：basic, advanced, social_media
   */
  @Column(name = "config_group", length = 50)
  private String configGroup;

  /**
   * 是否启用
   */
  @Column(name = "is_enabled", nullable = false)
  private Boolean isEnabled = true;

  @Column(name = "created_at", updatable = false, nullable = false)
  @CreatedDate
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  @LastModifiedDate
  private LocalDateTime updatedAt;
}
