package com.antstocks.project.controller;

import com.antstocks.project.service.DataCrawlingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class DataCrawlingController {

    private final DataCrawlingService dataCrawlingService;
    private static final Logger logger = LoggerFactory.getLogger(DataCrawlingController.class);

    // 생성자를 통한 의존성 주입
    public DataCrawlingController(DataCrawlingService dataCrawlingService) {
        this.dataCrawlingService = dataCrawlingService;
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
}
