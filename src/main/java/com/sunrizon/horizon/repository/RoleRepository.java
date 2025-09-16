package com.sunrizon.horizon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunrizon.horizon.pojo.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

}
