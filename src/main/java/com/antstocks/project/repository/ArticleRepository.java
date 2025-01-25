package com.antstocks.project.repository;

import com.antstocks.project.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {


    //제목 중복 검사
    boolean existsByoriginTitle(String title);
}
