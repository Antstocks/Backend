package com.antstocks.project.controller;

import com.antstocks.project.projection.ArticleProjection.BreakingNewsProjection;
import com.antstocks.project.repository.ArticleRepository;
import com.antstocks.project.service.DataCrawlingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api")
//CORS 허용
@CrossOrigin(origins = "http://localhost:5173")
public class BreakingNewsController {
    private final ArticleRepository articleRepository;
    private static final Logger logger = LoggerFactory.getLogger(DataCrawlingController.class);

    // 생성자를 통한 의존성 주입
    public BreakingNewsController(DataCrawlingService dataCrawlingService,ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    // 긴급뉴스 엔드포인트
    @GetMapping("/breakingNews")
    public List<BreakingNewsProjection> getBreakingNews() {
        // 뉴스 점수가 7점 이상인 기사를 시간 내림차순으로 select
        return articleRepository.findTop5ByScoreGreaterThanEqual(8, Sort.by(Sort.Order.desc("time")));
    }
}
