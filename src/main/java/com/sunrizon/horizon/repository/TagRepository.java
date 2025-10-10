package com.sunrizon.horizon.repository;

import com.sunrizon.horizon.pojo.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, String> {

  // Find a tag by its name
  Tag findByName(String name);

  // Find a tag by its slug
  Tag findBySlug(String slug);

  // Check if a tag exists by name
  boolean existsByName(String name);

  // Check if a tag exists by slug
  boolean existsBySlug(String slug);
}
