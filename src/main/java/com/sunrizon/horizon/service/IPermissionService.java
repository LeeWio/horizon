package com.sunrizon.horizon.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sunrizon.horizon.dto.CreatePermissionRequest;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.PermissionVO;

public interface IPermissionService {
  ResultResponse<PermissionVO> createPermission(CreatePermissionRequest request);

  // ResultResponse<PermissionVO> updatePermission(String pid,
  // CreatePermissionRequest request);

  ResultResponse<String> deletePermission(String pid);

  ResultResponse<PermissionVO> getPermission(String pid);

  ResultResponse<Page<PermissionVO>> getPermissions(Pageable pageable);
}
