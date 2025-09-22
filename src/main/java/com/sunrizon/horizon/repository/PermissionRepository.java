package com.sunrizon.horizon.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunrizon.horizon.pojo.Permission;
import com.sunrizon.horizon.enums.PermissionType;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {
  Optional<Permission> findByName(PermissionType name);
}
