package com.sunrizon.horizon.service;

import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.enums.FileStatus;
import com.sunrizon.horizon.pojo.File;
import com.sunrizon.horizon.vo.FileVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFileService {

  /**
   * 上传文件并保存信息
   */
  ResultResponse<FileVO> uploadFile(MultipartFile multipartFile);

  /**
   * 上传文件并保存信息，带描述
   */
  ResultResponse<FileVO> uploadFile(MultipartFile multipartFile, String description);

  /**
   * 根据ID查找文件
   */
  ResultResponse<FileVO> getFileById(String fileId);

  /**
   * 根据存储名称查找文件
   */
  ResultResponse<FileVO> getFileByStoredName(String storedName);

  /**
   * 获取用户上传的所有文件
   */
  ResultResponse<List<FileVO>> getFilesByUserId(String userId);

  /**
   * 根据状态获取文件列表
   */
  ResultResponse<List<FileVO>> getFilesByStatus(FileStatus status);

  /**
   * 分页获取所有文件
   */
  ResultResponse<Page<FileVO>> getAllFiles(Pageable pageable);

  /**
   * 根据关键词分页搜索文件
   */
  ResultResponse<Page<FileVO>> searchFiles(String keyword, Pageable pageable);

  /**
   * 更新文件状态
   */
  ResultResponse<FileVO> updateFileStatus(String fileId, FileStatus status);

  /**
   * 删除文件（逻辑删除）
   */
  ResultResponse<String> deleteFile(String fileId);

  /**
   * 物理删除文件记录和文件
   */
  ResultResponse<String> deleteFileCompletely(String fileId);
}
