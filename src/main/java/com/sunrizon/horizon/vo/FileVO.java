package com.sunrizon.horizon.vo;

import com.sunrizon.horizon.enums.FileStatus;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * File View Object
 */
@Setter
@Getter
public class FileVO implements Serializable {

  private static final long serialVersionUID = 1L;

  private String fid;

  private String originalName;

  private String storedName;

  private String filePath;

  private Long fileSize;

  private String contentType;

  private com.sunrizon.horizon.enums.FileType fileType;

  private FileStatus status;

  private String createBy;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  private String description;

  private String url;

  private String bucketName;
}
