package com.sunrizon.horizon.repository;

import com.sunrizon.horizon.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

  // Find a category by its name
  Category findByName(String name);

  // Find a category by its slug
  Category findBySlug(String slug);

  // Check if a category exists by name
  boolean existsByName(String name);

  // Check if a category exists by slug
  boolean existsBySlug(String slug);
}