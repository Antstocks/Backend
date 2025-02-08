package com.antstocks.project.repository;

import com.antstocks.project.entity.Article;
import com.antstocks.project.projection.ArticleProjection.BreakingNewsProjection;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {


    //제목 중복 검사
    boolean existsByoriginTitle(String title);

    // 긴급 뉴스 조회
    List<BreakingNewsProjection> findTop5ByScoreGreaterThanEqual(int score, Sort sort);

    // 언급된 상위 10개 종목 조회
    @Query("SELECT a.stocks FROM Article a WHERE a.time >= :startOfDay AND a.time < :endOfDay")
    List<String> findAllStocksToday(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
}
