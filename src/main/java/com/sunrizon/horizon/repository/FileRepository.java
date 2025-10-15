package com.sunrizon.horizon.repository;

import com.sunrizon.horizon.pojo.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, String> {

  Optional<File> findByStoredName(String storedName);

  Optional<File> findByUrl(String url);

  List<File> findByCreateBy(String createBy);

  List<File> findByStatus(com.sunrizon.horizon.enums.FileStatus status);

  Page<File> findByOriginalNameContainingIgnoreCaseOrStoredNameContainingIgnoreCase(
      String originalNameKeyword,
      String storedNameKeyword,
      Pageable pageable);
}
