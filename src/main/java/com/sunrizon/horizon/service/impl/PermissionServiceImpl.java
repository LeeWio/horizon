package com.sunrizon.horizon.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sunrizon.horizon.enums.PermissionType;
import com.sunrizon.horizon.enums.ResponseCode;
import com.sunrizon.horizon.pojo.Permission;
import com.sunrizon.horizon.repository.PermissionRepository;
import com.sunrizon.horizon.service.IPermissionService;
import com.sunrizon.horizon.utils.ResultResponse;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PermissionServiceImpl implements IPermissionService {

  @Resource
  private PermissionRepository permissionRepository;

  @Override
  @Transactional
  public ResultResponse<Permission> createPermission(PermissionType name, String description) {
    log.error("name: {}", name.name());
    if (StrUtil.isBlank(name.name())) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "权限名称不能为空");
    }
    Optional<Permission> existing = permissionRepository.findByName(name);
    if (existing.isPresent()) {
      return ResultResponse.error(ResponseCode.CONFLICT, "权限已存在");
    }
    Permission permission = new Permission();
    permission.setName(name);
    permission.setDescription(description);
    Permission saved = permissionRepository.save(permission);
    return ResultResponse.success(saved);
  }

  @Override
  public ResultResponse<Permission> getPermission(String pid) {
    if (pid == null || pid.isBlank()) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "权限ID不能为空");
    }
    return permissionRepository.findById(pid)
        .map(ResultResponse::success)
        .orElseGet(() -> ResultResponse.error(ResponseCode.PERMISSION_NOT_FOUND));
  }

  @Override
  public ResultResponse<Permission> getPermissionByName(PermissionType name) {
    if (name == null) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "权限名称不能为空");
    }
    return permissionRepository.findByName(name)
        .map(ResultResponse::success)
        .orElseGet(() -> ResultResponse.error(ResponseCode.PERMISSION_NOT_FOUND));
  }

  @Override
  public ResultResponse<List<Permission>> getPermissions() {
    List<Permission> all = permissionRepository.findAll();
    return ResultResponse.success(all);
  }

  @Override
  @Transactional
  public ResultResponse<String> deletePermission(String pid) {
    if (pid == null || pid.isBlank()) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "权限ID不能为空");
    }
    boolean exists = permissionRepository.existsById(pid);
    if (!exists) {
      return ResultResponse.error(ResponseCode.PERMISSION_NOT_FOUND);
    }
    permissionRepository.deleteById(pid);
    return ResultResponse.success("删除成功");
  }
}
