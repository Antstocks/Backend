package com.antstocks.project.service.impl;

import com.antstocks.project.entity.Article;
import com.antstocks.project.repository.ArticleRepository;
import com.antstocks.project.service.DataCrawlingService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Service
public class DataCrawlingServiceImpl implements DataCrawlingService {


    @Autowired
    public ArticleRepository articleRepository;

    @Override
    public void parseHtml() {
        try {
            // 1단계: 메인 페이지 크롤링
            String mainUrl = "https://www.investing.com/news/stock-market-news";
            Document mainDoc = Jsoup.connect(mainUrl).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
                    .get();


            // 기사 링크 추출
            Elements articles = mainDoc.select("[data-test=article-title-link]");

            for (Element article : articles) {
                // 상대 URL이 있을 경우, 절대 URL로 변환
                String articleUrl = article.attr("href");
                if (!articleUrl.startsWith("http")) {
                    articleUrl = "https://www.investing.com" + articleUrl;
                }

                String articleTitle = article.text();
                System.out.println("Href: " + articleUrl);
                System.out.println("Text: " + articleTitle);

                // 2단계: 개별 기사 페이지 크롤링
                Document articleDoc = Jsoup.connect(articleUrl).get();
                String postStr ="시간";

                // 게시 날짜 크롤링 (div.flex flex-row items-center 내의 span)
                Elements dateElements = articleDoc.select("div.flex.flex-row.items-center span");
                for (Element dateElement : dateElements) {
                    String postDate = dateElement.text();

                    postDate = postDate.replace("Published", "").trim();
                    if (!postDate.trim().isEmpty()) {
                        System.out.println("Post Date: " + postDate);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy, hh:mm a");
                        //LocalDateTime dateTime = LocalDateTime.parse(postDate,formatter);
                        //System.out.println("Post Date: " + dateTime);
                        break;
                    }
                }


                // div.article_WYSIWYG__O0uhw 내부의 p 태그들 추출
                Elements paragraphs = articleDoc.select("div.article_WYSIWYG__O0uhw p");

                StringBuilder contentBuilder = new StringBuilder();

                // 각 p 태그의 텍스트를 하나의 String에 합치기
                for (Element paragraph : paragraphs) {
                    String content = paragraph.text().trim();  // 앞뒤 공백 제거
                    if (!content.isEmpty()) {
                        contentBuilder.append(content.replaceAll("\\s+", " ")).append("\n");  // 여러 공백을 하나로
                    }
                }

                // StringBuilder로 합쳐진 텍스트를 하나의 String으로 변환
                String articleContent = contentBuilder.toString();
                System.out.println("Article Content: " + articleContent);

                Article articleEntity = new Article();
                articleEntity.setTitle(articleTitle);
                articleEntity.setSummary(articleContent);

                // 데이터베이스에 저장
                articleRepository.save(articleEntity);
                System.out.println("--------------------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
