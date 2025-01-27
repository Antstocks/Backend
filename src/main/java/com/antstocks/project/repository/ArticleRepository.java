package com.antstocks.project.repository;

import com.antstocks.project.entity.Article;
import com.antstocks.project.projection.ArticleProjection.BreakingNewsProjection;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {


    //제목 중복 검사
    boolean existsByoriginTitle(String title);

    // 긴급 뉴스 조회
    List<BreakingNewsProjection> findTop5ByScoreGreaterThanEqual(int score, Sort sort);
}
