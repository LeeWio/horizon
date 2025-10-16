package com.sunrizon.horizon.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunrizon.horizon.pojo.SeoConfig;

/**
 * SEO配置数据访问层
 */
@Repository
public interface SeoConfigRepository extends JpaRepository<SeoConfig, String> {

  /**
   * 根据配置键查询配置
   */
  Optional<SeoConfig> findByConfigKey(String configKey);

  /**
   * 根据配置分组查询所有配置
   */
  List<SeoConfig> findByConfigGroup(String configGroup);

  /**
   * 查询所有启用的配置
   */
  List<SeoConfig> findByIsEnabledTrue();

  /**
   * 根据配置分组查询启用的配置
   */
  @Query("SELECT s FROM SeoConfig s WHERE s.configGroup = :group AND s.isEnabled = true ORDER BY s.configKey")
  List<SeoConfig> findEnabledByGroup(@Param("group") String group);

  /**
   * 检查配置键是否存在
   */
  boolean existsByConfigKey(String configKey);

  /**
   * 根据配置键删除配置
   */
  void deleteByConfigKey(String configKey);
}
