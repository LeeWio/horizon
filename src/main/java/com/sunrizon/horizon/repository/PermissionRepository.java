package com.sunrizon.horizon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunrizon.horizon.enums.PermissionType;
import com.sunrizon.horizon.pojo.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {
  boolean existsByName(PermissionType name);
}
