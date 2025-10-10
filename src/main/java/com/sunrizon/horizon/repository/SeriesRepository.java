package com.sunrizon.horizon.repository;

import com.sunrizon.horizon.pojo.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeriesRepository extends JpaRepository<Series, String> {

  // Find a series by its name
  Series findByName(String name);

  // Find a series by its slug
  Series findBySlug(String slug);

  // Check if a series exists by name
  boolean existsByName(String name);

  // Check if a series exists by slug
  boolean existsBySlug(String slug);
}