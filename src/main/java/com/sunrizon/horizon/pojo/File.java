package com.sunrizon.horizon.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.sunrizon.horizon.enums.FileType;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 文件实体类
 * 用于管理上传的文件（图片、附件等）
 */
@Entity
@Table(name = "file")
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
public class File implements Serializable {

  private static final long serialVersionUID = -6249791470254664726L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "fid", nullable = false, length = 36, updatable = false)
  private String fid;

  @Column(name = "original_name", nullable = false, length = 255)
  private String originalName;

  @Column(name = "file_name", nullable = false, length = 255)
  private String fileName;

  @Column(name = "file_path", nullable = false, length = 500)
  private String filePath;

  @Column(name = "file_url", length = 500)
  private String fileUrl;

  @Column(name = "file_size", nullable = false)
  private Long fileSize;

  @Column(name = "mime_type", length = 100)
  private String mimeType;

  @Enumerated(EnumType.STRING)
  @Column(name = "file_type", nullable = false, length = 20)
  private FileType fileType;

  @Column(name = "width")
  private Integer width; // 图片宽度

  @Column(name = "height")
  private Integer height; // 图片高度

  @Column(name = "uploader_id", length = 36)
  private String uploaderId;

  @Column(name = "is_public", nullable = false)
  private Boolean isPublic = true;

  @Column(name = "download_count", nullable = false)
  private Long downloadCount = 0L;

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  // 关联关系
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "uploader_id", insertable = false, updatable = false)
  private User uploader;
}
