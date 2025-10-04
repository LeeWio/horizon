package com.sunrizon.horizon.repository;

import com.sunrizon.horizon.pojo.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
