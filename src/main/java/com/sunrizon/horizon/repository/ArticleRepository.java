package com.sunrizon.horizon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunrizon.horizon.pojo.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, String> {

}
