package com.sunrizon.horizon.repository;

import com.sunrizon.horizon.enums.FileStatus;
import com.sunrizon.horizon.pojo.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, String> {

  // Optional<File> findByStoredName(String storedName);
  //
  // Optional<File> findByUrl(String url);
  //
  // List<File> findByUploadUserId(String uploadUserId);
  //
  // List<File> findByStatus(FileStatus status);
  //
  // @Query("SELECT f FROM File f WHERE f.originalName LIKE %:keyword% OR
  // f.storedName LIKE %:keyword%")
  // List<File> findByKeyword(@Param("keyword") String keyword);
  //
  // List<File>
  // findByOriginalNameContainingIgnoreCaseOrStoredNameContainingIgnoreCase(String
  // originalNameKeyword,
  // String storedNameKeyword);
}
