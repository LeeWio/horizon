package com.sunrizon.horizon.service.impl;

import com.sunrizon.horizon.enums.FileStatus;
import com.sunrizon.horizon.enums.ResponseCode;
import com.sunrizon.horizon.pojo.File;
import com.sunrizon.horizon.repository.FileRepository;
import com.sunrizon.horizon.service.IFileService;
import com.sunrizon.horizon.utils.ResultResponse;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileServiceImpl implements IFileService {

  @Value("${file.upload-dir:uploads}")
  private String uploadDir;

  @Resource
  private FileRepository fileRepository;

  // public ResultResponse<File> uploadAndSaveFile(MultipartFile multipartFile) {
  // try {
  //
  // if (multipartFile.isEmpty()) {
  // return ResultResponse.error(ResponseCode.FAILURE);
  // }
  //
  // String originalName = multipartFile.getOriginalFilename();
  // String extName = FileUtil.extName(originalName);
  // String uuid = IdUtil.fastSimpleUUID();
  // String storedName = uuid + (StrUtil.isNotBlank(extName) ? "." + extName : //
  // 设置文件类型
  // if (originalFilename != null) {
  // String extension = "";
  // if (originalFilename.contains(".")) {
  // extension = originalFilename.substring(originalFilename.lastIndexOf(".") +
  // 1);
  // }
  // file.setFileType(com.sunrizon.horizon.enums.FileType.fromExtension(extension));
  // });
  //
  // String rootPath = System.getProperty("user.dir");
  // java.io.File path = FileUtil.mkdir(rootPath + java.io.File.separator +
  // uploadDir);
  // java.io.File destPath = new java.io.File(path, storedName);
  //
  // multipartFile.transferTo(destPath);
  //
  // File file = new File();
  //
  // // save file
  // fileRepository.saveAndFlush(file);
  //
  // } catch (Exception e) {
  //
  // }
  //
  // return null;
  // }
}
