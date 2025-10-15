package com.sunrizon.horizon.controller;

import com.sunrizon.horizon.enums.FileStatus;
import com.sunrizon.horizon.service.IFileService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.FileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * File Management Controller
 */
@RestController
@RequestMapping("/api/file")
@Tag(name = "File Management", description = "File upload, download, and management APIs")
public class FileController {

  @Resource
  private IFileService fileService;

  /**
   * 上传文件
   */
  @PostMapping("/upload")
  @Operation(summary = "Upload file", description = "Upload a file (image, document, etc.)")
  public ResultResponse<FileVO> uploadFile(
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "description", required = false) String description,
      Authentication authentication) {
    String userId = authentication != null ? authentication.getName() : null;
    return fileService.uploadFile(file, userId, description);
  }

  /**
   * 获取文件详情
   */
  @GetMapping("/{fid}")
  @Operation(summary = "Get file by ID", description = "Get file details by file ID")
  public ResultResponse<FileVO> getFileById(@PathVariable String fid) {
    return fileService.getFileById(fid);
  }

  /**
   * 获取当前用户的所有文件
   */
  @GetMapping("/my-files")
  @Operation(summary = "Get my files", description = "Get all files uploaded by current user")
  public ResultResponse<List<FileVO>> getMyFiles(Authentication authentication) {
    String userId = authentication.getName();
    return fileService.getFilesByUserId(userId);
  }

  /**
   * 根据用户ID获取文件列表
   */
  @GetMapping("/user/{userId}")
  @Operation(summary = "Get files by user ID", description = "Get all files uploaded by specified user")
  public ResultResponse<List<FileVO>> getFilesByUserId(@PathVariable String userId) {
    return fileService.getFilesByUserId(userId);
  }

  /**
   * 根据状态获取文件列表
   */
  @GetMapping("/status/{status}")
  @Operation(summary = "Get files by status", description = "Get files by status (ACTIVE, DELETED)")
  public ResultResponse<List<FileVO>> getFilesByStatus(@PathVariable FileStatus status) {
    return fileService.getFilesByStatus(status);
  }

  /**
   * 分页获取所有文件
   */
  @GetMapping("/page")
  @Operation(summary = "Get files with pagination", description = "Get all files with pagination support")
  public ResultResponse<Page<FileVO>> getAllFiles(
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return fileService.getAllFiles(pageable);
  }

  /**
   * 搜索文件
   */
  @GetMapping("/search")
  @Operation(summary = "Search files", description = "Search files by keyword in name")
  public ResultResponse<Page<FileVO>> searchFiles(
      @RequestParam String keyword,
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return fileService.searchFiles(keyword, pageable);
  }

  /**
   * 更新文件状态
   */
  @PutMapping("/{fid}/status")
  @Operation(summary = "Update file status", description = "Update file status (ACTIVE, DELETED)")
  public ResultResponse<FileVO> updateFileStatus(
      @PathVariable String fid,
      @RequestParam FileStatus status) {
    return fileService.updateFileStatus(fid, status);
  }

  /**
   * 逻辑删除文件
   */
  @DeleteMapping("/{fid}")
  @Operation(summary = "Delete file", description = "Soft delete file (mark as DELETED)")
  public ResultResponse<String> deleteFile(@PathVariable String fid) {
    return fileService.deleteFile(fid);
  }

  /**
   * 物理删除文件
   */
  @DeleteMapping("/{fid}/completely")
  @Operation(summary = "Delete file completely", description = "Permanently delete file and its database record")
  public ResultResponse<String> deleteFileCompletely(@PathVariable String fid) {
    return fileService.deleteFileCompletely(fid);
  }
}
