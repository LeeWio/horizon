package com.sunrizon.horizon.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sunrizon.horizon.dto.CreatePermissionRequest;
import com.sunrizon.horizon.enums.ResponseCode;
import com.sunrizon.horizon.pojo.Permission;
import com.sunrizon.horizon.repository.PermissionRepository;
import com.sunrizon.horizon.service.IPermissionService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.PermissionVO;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityNotFoundException;

@Service
public class PermissionServiceImpl implements IPermissionService {

  @Resource
  private PermissionRepository permissionRepository;

  @Override
  @Transactional
  public ResultResponse<PermissionVO> createPermission(CreatePermissionRequest request) {

    if (StrUtil.isBlank(request.getName().name())) {
      return ResultResponse.error(ResponseCode.PERMISSION_NAME_CANNOT_BE_NULL);
    }

    if (permissionRepository.existsByName(request.getName())) {
      return ResultResponse.error(ResponseCode.PERMISSION_NAME_ALREADY_EXISTS);
    }

    Permission permission = BeanUtil.copyProperties(request, Permission.class);

    Permission saved = permissionRepository.saveAndFlush(permission);

    PermissionVO permissionVO = BeanUtil.copyProperties(saved, PermissionVO.class);

    return ResultResponse.success(permissionVO);

  }

  @Override
  public ResultResponse<PermissionVO> getPermission(String pid) {

    if (StrUtil.isBlank(pid)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    Permission permission = permissionRepository.findById(pid)
        .orElseThrow(() -> new EntityNotFoundException("Permission not found for ID: " + pid));

    PermissionVO permissionVO = BeanUtil.copyProperties(permission, PermissionVO.class);

    return ResultResponse.success(permissionVO);
  }

  @Override
  public ResultResponse<String> deletePermission(String pid) {

    if (StrUtil.isBlank(pid)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    Permission permission = permissionRepository.findById(pid)
        .orElseThrow(() -> new EntityNotFoundException("Permission not found for ID: " + pid));

    permissionRepository.delete(permission);

    return ResultResponse.success(ResponseCode.PERMISSION_DELETED_SUCCESSFULLY);

  }

  @Override
  public ResultResponse<Page<PermissionVO>> getPermissions(Pageable pageable) {

    Page<Permission> permissionPage = permissionRepository.findAll(pageable);

    Page<PermissionVO> voPage = permissionPage.map(permission -> {
      return BeanUtil.copyProperties(permission, PermissionVO.class);
    });

    return ResultResponse.success(voPage);
  }
}
