package com.sunrizon.horizon.service;

import com.sunrizon.horizon.enums.FileStatus;
import com.sunrizon.horizon.pojo.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface IFileService {

  // /**
  // * 保存文件信息到数据库
  // */
  // File saveFile(File file);
  //
  // /**
  // * 根据ID查找文件
  // */
  // Optional<File> getFileById(String fileId);
  //
  // /**
  // * 根据存储名称查找文件
  // */
  // Optional<File> getFileByStoredName(String storedName);
  //
  // /**
  // * 根据URL查找文件
  // */
  // Optional<File> getFileByUrl(String url);
  //
  // /**
  // * 获取用户上传的所有文件
  // */
  // List<File> getFilesByUserId(String userId);
  //
  // /**
  // * 根据状态获取文件列表
  // */
  // List<File> getFilesByStatus(FileStatus status);
  //
  // /**
  // * 分页获取所有文件
  // */
  // Page<File> getAllFiles(Pageable pageable);
  //
  // /**
  // * 搜索文件
  // */
  // Page<File> searchFiles(String keyword, Pageable pageable);
  //
  // /**
  // * 根据关键词分页搜索文件
  // */
  // Page<File> searchFilesByKeyword(String keyword, Pageable pageable);
  //
  // /**
  // * 更新文件状态
  // */
  // File updateFileStatus(String fileId, FileStatus status);
  //
  // /**
  // * 删除文件（逻辑删除）
  // */
  // void deleteFile(String fileId);
  //
  // /**
  // * 物理删除文件记录
  // */
  // void deleteFileRecord(String fileId);
  //
  // /**
  // * 上传文件并保存信息
  // */
  // File uploadAndSaveFile(MultipartFile multipartFile, String uploadUserId);
  //
  // /**
  // * 上传文件并保存信息，带描述
  // */
  // File uploadAndSaveFile(MultipartFile multipartFile, String uploadUserId,
  // String description);
}
