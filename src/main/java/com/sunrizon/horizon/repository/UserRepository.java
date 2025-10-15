package com.sunrizon.horizon.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunrizon.horizon.enums.UserStatus;
import com.sunrizon.horizon.pojo.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

  // Find a user by email
  Optional<User> findUserByEmail(String email);

  // Find a user by username
  Optional<User> findUserByUsername(String username);

  // Find all users with a specific status
  List<User> findAllByStatus(UserStatus status);

  // Find users by status with pagination
  Page<User> findByStatus(UserStatus status, Pageable pageable);

  // Find all users by role name (assuming User has a roles collection)
  List<User> findAllByRoles_Name(String roleName);

  // Find a user by email or username (commonly used for login)
  Optional<User> findByEmailOrUsername(String email, String username);

  // Check if an email exists
  boolean existsByEmail(String email);

  // Check if a username exists
  boolean existsByUsername(String username);

}
