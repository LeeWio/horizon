package com.sunrizon.horizon.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "reading_history")
@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class ReadingHistory implements Serializable {

  private static final long serialVersionUID = -6249794470754667714L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, length = 36, updatable = false)
  private String id;

  @Column(name = "article_id", nullable = false, length = 36)
  private String articleId;

  @Column(name = "user_id", length = 36)
  private String userId;

  @Column(name = "ip_address", length = 50)
  private String ipAddress;

  @Column(name = "user_agent", length = 500)
  private String userAgent;

  @Column(name = "read_time")
  private Integer readTime; // 阅读时长（秒）

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
