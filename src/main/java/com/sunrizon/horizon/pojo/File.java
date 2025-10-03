package com.sunrizon.horizon.pojo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.sunrizon.horizon.enums.FileStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "file")
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class File implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "fid", nullable = false, length = 36, updatable = false)
  @GeneratedValue(strategy = GenerationType.UUID)
  private String fid;

  @Column(name = "original_name", nullable = false, length = 255)
  private String originalName; // 原始文件名

  @Column(name = "stored_name", nullable = false, length = 255, unique = true)
  private String storedName; // 存储时的文件名（通常是生成的唯一名称）

  @Column(name = "file_path", nullable = false, length = 500)
  private String filePath; // 文件存储路径

  @Column(name = "file_size")
  private Long fileSize; // 文件大小（字节）

  @Column(name = "content_type", length = 100)
  private String contentType; // MIME类型

  @Column(name = "file_type", length = 50)
  @Enumerated(EnumType.STRING)
  private com.sunrizon.horizon.enums.FileType fileType; // 文件类型（如 image, document, video 等）

  @Column(name = "status", length = 20)
  @Enumerated(EnumType.STRING)
  private FileStatus status = FileStatus.ACTIVE; // 文件状态

  @Column(name = "create_by", length = 36)
  @CreatedBy
  private String createBy; // 上传用户ID

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreatedDate
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  @LastModifiedDate
  private LocalDateTime updatedAt;

  @Column(name = "description", length = 500)
  private String description; // 文件描述

  @Column(name = "url", length = 500)
  private String url; // 文件访问URL

  @Column(name = "bucket_name", length = 100)
  private String bucketName; // 存储桶名称（如果使用云存储）
}
