package com.sunrizon.horizon.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.sunrizon.horizon.enums.FileStatus;
import com.sunrizon.horizon.enums.ResponseCode;
import com.sunrizon.horizon.pojo.File;
import com.sunrizon.horizon.repository.FileRepository;
import com.sunrizon.horizon.service.IFileService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.FileVO;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl implements IFileService {

  @Value("${file.upload-dir:uploads}")
  private String uploadDir;

  @Resource
  private FileRepository fileRepository;

  @Override
  @Transactional
  public ResultResponse<FileVO> uploadFile(MultipartFile multipartFile, String uploadUserId) {
    return uploadFile(multipartFile, uploadUserId, null);
  }

  @Override
  @Transactional
  public ResultResponse<FileVO> uploadFile(MultipartFile multipartFile, String uploadUserId, String description) {
    try {
      // 1. 参数验证
      if (multipartFile == null || multipartFile.isEmpty()) {
        return ResultResponse.error(ResponseCode.FILE_IS_EMPTY);
      }

      // 2. 生成唯一文件名
      String originalName = multipartFile.getOriginalFilename();
      String extName = FileUtil.extName(originalName);
      String uuid = IdUtil.fastSimpleUUID();
      String storedName = uuid + (StrUtil.isNotBlank(extName) ? "." + extName : "");

      // 3. 创建上传目录
      String rootPath = System.getProperty("user.dir");
      java.io.File uploadPath = FileUtil.mkdir(rootPath + java.io.File.separator + uploadDir);
      java.io.File destFile = new java.io.File(uploadPath, storedName);

      // 4. 保存文件到磁盘
      multipartFile.transferTo(destFile);

      // 5. 创建文件实体
      File file = new File();
      file.setOriginalName(originalName);
      file.setStoredName(storedName);
      file.setFilePath(destFile.getAbsolutePath());
      file.setFileSize(multipartFile.getSize());
      file.setContentType(multipartFile.getContentType());
      file.setStatus(FileStatus.ACTIVE);
      file.setDescription(description);

      // 设置文件类型
      if (StrUtil.isNotBlank(extName)) {
        String ext = extName.toLowerCase();
        if (ext.matches("jpg|jpeg|png|gif|bmp|webp|svg")) {
          file.setFileType(com.sunrizon.horizon.enums.FileType.IMAGE);
        } else if (ext.matches("pdf|doc|docx|xls|xlsx|ppt|pptx|txt")) {
          file.setFileType(com.sunrizon.horizon.enums.FileType.DOCUMENT);
        } else {
          file.setFileType(com.sunrizon.horizon.enums.FileType.OTHER);
        }
      }

      // 设置访问URL（相对路径）
      String url = "/" + uploadDir + "/" + storedName;
      file.setUrl(url);

      // 6. 保存到数据库
      File savedFile = fileRepository.saveAndFlush(file);

      // 7. 转换为VO
      FileVO fileVO = BeanUtil.copyProperties(savedFile, FileVO.class);

      return ResultResponse.success(ResponseCode.FILE_UPLOAD_SUCCESS, fileVO);

    } catch (IOException e) {
      return ResultResponse.error(ResponseCode.FILE_UPLOAD_FAILED);
    } catch (Exception e) {
      return ResultResponse.error(ResponseCode.INTERNAL_ERROR);
    }
  }

  @Override
  public ResultResponse<FileVO> getFileById(String fileId) {
    // 1. 参数验证
    if (StrUtil.isBlank(fileId)) {
      return ResultResponse.error(ResponseCode.FILE_ID_CANNOT_BE_EMPTY);
    }

    // 2. 查询文件
    File file = fileRepository.findById(fileId)
        .orElse(null);

    if (file == null) {
      return ResultResponse.error(ResponseCode.FILE_NOT_FOUND);
    }

    // 3. 转换为VO
    FileVO fileVO = BeanUtil.copyProperties(file, FileVO.class);

    return ResultResponse.success(fileVO);
  }

  @Override
  public ResultResponse<FileVO> getFileByStoredName(String storedName) {
    // 1. 参数验证
    if (StrUtil.isBlank(storedName)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    // 2. 查询文件
    File file = fileRepository.findByStoredName(storedName)
        .orElse(null);

    if (file == null) {
      return ResultResponse.error(ResponseCode.FILE_NOT_FOUND);
    }

    // 3. 转换为VO
    FileVO fileVO = BeanUtil.copyProperties(file, FileVO.class);

    return ResultResponse.success(fileVO);
  }

  @Override
  public ResultResponse<List<FileVO>> getFilesByUserId(String userId) {
    // 1. 参数验证
    if (StrUtil.isBlank(userId)) {
      return ResultResponse.error(ResponseCode.USER_ID_CANNOT_BE_EMPTY);
    }

    // 2. 查询文件列表
    List<File> files = fileRepository.findByCreateBy(userId);

    // 3. 转换为VO列表
    List<FileVO> fileVOs = files.stream()
        .map(file -> BeanUtil.copyProperties(file, FileVO.class))
        .collect(Collectors.toList());

    return ResultResponse.success(fileVOs);
  }

  @Override
  public ResultResponse<List<FileVO>> getFilesByStatus(FileStatus status) {
    // 1. 参数验证
    if (status == null) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    // 2. 查询文件列表
    List<File> files = fileRepository.findByStatus(status);

    // 3. 转换为VO列表
    List<FileVO> fileVOs = files.stream()
        .map(file -> BeanUtil.copyProperties(file, FileVO.class))
        .collect(Collectors.toList());

    return ResultResponse.success(fileVOs);
  }

  @Override
  public ResultResponse<Page<FileVO>> getAllFiles(Pageable pageable) {
    // 1. 分页查询
    Page<File> filePage = fileRepository.findAll(pageable);

    // 2. 转换为VO Page
    Page<FileVO> fileVOPage = filePage.map(file -> BeanUtil.copyProperties(file, FileVO.class));

    return ResultResponse.success(fileVOPage);
  }

  @Override
  public ResultResponse<Page<FileVO>> searchFiles(String keyword, Pageable pageable) {
    // 1. 参数验证
    if (StrUtil.isBlank(keyword)) {
      return getAllFiles(pageable);
    }

    // 2. 搜索文件
    Page<File> filePage = fileRepository.findByOriginalNameContainingIgnoreCaseOrStoredNameContainingIgnoreCase(
        keyword, keyword, pageable);

    // 3. 转换为VO Page
    Page<FileVO> fileVOPage = filePage.map(file -> BeanUtil.copyProperties(file, FileVO.class));

    return ResultResponse.success(fileVOPage);
  }

  @Override
  @Transactional
  public ResultResponse<FileVO> updateFileStatus(String fileId, FileStatus status) {
    // 1. 参数验证
    if (StrUtil.isBlank(fileId)) {
      return ResultResponse.error(ResponseCode.FILE_ID_CANNOT_BE_EMPTY);
    }

    if (status == null) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    // 2. 查询文件
    File file = fileRepository.findById(fileId)
        .orElse(null);

    if (file == null) {
      return ResultResponse.error(ResponseCode.FILE_NOT_FOUND);
    }

    // 3. 更新状态
    file.setStatus(status);
    File updatedFile = fileRepository.saveAndFlush(file);

    // 4. 转换为VO
    FileVO fileVO = BeanUtil.copyProperties(updatedFile, FileVO.class);

    return ResultResponse.success(fileVO);
  }

  @Override
  @Transactional
  public ResultResponse<String> deleteFile(String fileId) {
    // 1. 参数验证
    if (StrUtil.isBlank(fileId)) {
      return ResultResponse.error(ResponseCode.FILE_ID_CANNOT_BE_EMPTY);
    }

    // 2. 查询文件
    File file = fileRepository.findById(fileId)
        .orElse(null);

    if (file == null) {
      return ResultResponse.error(ResponseCode.FILE_NOT_FOUND);
    }

    // 3. 逻辑删除（更新状态为DELETED）
    file.setStatus(FileStatus.DELETED);
    fileRepository.saveAndFlush(file);

    return ResultResponse.success(ResponseCode.FILE_DELETED_SUCCESSFULLY);
  }

  @Override
  @Transactional
  public ResultResponse<String> deleteFileCompletely(String fileId) {
    // 1. 参数验证
    if (StrUtil.isBlank(fileId)) {
      return ResultResponse.error(ResponseCode.FILE_ID_CANNOT_BE_EMPTY);
    }

    // 2. 查询文件
    File file = fileRepository.findById(fileId)
        .orElse(null);

    if (file == null) {
      return ResultResponse.error(ResponseCode.FILE_NOT_FOUND);
    }

    // 3. 删除物理文件
    try {
      java.io.File physicalFile = new java.io.File(file.getFilePath());
      if (physicalFile.exists()) {
        FileUtil.del(physicalFile);
      }
    } catch (Exception e) {
      // 文件删除失败，记录日志但继续删除数据库记录
    }

    // 4. 删除数据库记录
    fileRepository.delete(file);

    return ResultResponse.success(ResponseCode.FILE_DELETED_SUCCESSFULLY);
  }
}
