package com.antstocks.project.controller;

import com.antstocks.project.entity.Article;
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
public class DataCrawlingController {

    private final DataCrawlingService dataCrawlingService;

    private final ArticleRepository articleRepository;
    private static final Logger logger = LoggerFactory.getLogger(DataCrawlingController.class);

    // 생성자를 통한 의존성 주입
    public DataCrawlingController(DataCrawlingService dataCrawlingService,ArticleRepository articleRepository) {
        this.dataCrawlingService = dataCrawlingService;
        this.articleRepository = articleRepository;

    }

    // Crawling 실행 엔드포인트
    @GetMapping("/crawling")
    public String startCrawling() {
        try {
            dataCrawlingService.parseHtml();
            return "Crawling completed successfully!";
        } catch (Exception e) {
            logger.error("Crawling failed: ", e);  // 로그로 예외 기록
            return "Crawling failed: " + e.getMessage();
        }
    }

    @GetMapping("/allArticles")
    public List<Article> getAllArticles() {
        // 기사를 시간 내림차순으로 select
        return articleRepository.findAll(Sort.by(Sort.Order.desc("time")));
    }
}
