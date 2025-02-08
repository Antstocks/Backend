package com.antstocks.project.service.impl;

import com.antstocks.project.controller.SseController;
import com.antstocks.project.entity.Article;
import com.antstocks.project.repository.ArticleRepository;
import com.antstocks.project.service.DataCrawlingService;
import com.antstocks.project.service.GeminiService;
import com.antstocks.project.service.Top10StockMentionsService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// 랜덤
import java.util.concurrent.ThreadLocalRandom;

@Service
public class DataCrawlingServiceImpl implements DataCrawlingService {


    @Autowired
    private final ArticleRepository articleRepository;
    @Autowired
    private final GeminiService geminiService;

    @Autowired
    private SseController sseController;

    @Autowired
    private Top10StockMentionsService top10StockMentionsService;
    private int lastArticleCount = 0; // 이전 기사 갯수 저장

    public DataCrawlingServiceImpl(ArticleRepository articleRepository, GeminiService geminiService,SseController sseController) {
        this.articleRepository = articleRepository;
        this.geminiService = geminiService;
        this.sseController = sseController;
    }

    @Override
    @Scheduled(fixedRate = 600000)
    public void parseHtml() {

        String sp500 [] = {"AAPL","NVDA","MSFT","GOOG","GOOGL","AMZN","META","TSLA","AVGO","BRK-B"};
        //String sp500 [] = {"AAPL"};
            for (String rank : sp500) {
                try {
                    System.out.println(rank+" 크롤링 시작");

                    // yahoo크롤링
                    String mainUrl = "https://finance.yahoo.com/quote/"+rank+"/news/";
                    // 크롤링 방지 회피를 위한 userAgent 추가
                    Document mainDoc = Jsoup.connect(mainUrl).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
                            .get();

                    // 기사 링크 추출
                    Elements articles = mainDoc.select("a.subtle-link.fin-size-small.titles.noUnderline.yf-1xqzjha");

                    for (Element article : articles) {
                        // 기사 제목 추출
                        String articleTitle = article.attr("aria-label");

                        if (articleRepository.existsByoriginTitle(articleTitle)) {
                            continue; // 중복된 제목은 건너뜁니다.
                        }
                        String originTitle = articleTitle;
                        if (articleTitle.startsWith("Update: ")) {
                            continue; // 기사의 업데이트 소식은 건너뜁니다.
                        }
                        // 상대 URL이 있을 경우, 절대 URL로 변환
                        String articleUrl = article.attr("href");
                        if (!articleUrl.startsWith("http")) {
                            articleUrl = "https://www.investing.com" + articleUrl;
                        }

                        // 2단계: 개별 기사 페이지 크롤링
                        Document articleDoc = Jsoup.connect(articleUrl).get();
                        // 게시 날짜 크롤링
                        Elements dateElements = articleDoc.select("time");
                        String postDate = dateElements.attr("datetime");
                        LocalDateTime dateTime = null;

                        postDate = postDate.replace("Z", "");
                        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                        LocalDateTime localDateTime = LocalDateTime.parse(postDate, formatter);
                        dateTime = localDateTime.plusHours(9);

                        // div.article_WYSIWYG__O0uhw 내부의 p 태그들 추출
                        Elements paragraphs = articleDoc.select("p.yf-1pe5jgt");
                        if (paragraphs.isEmpty()) {
                            continue;
                        }

                        // title 속성을 가진 모든 a 태그 선택
                        Elements links = articleDoc.select("a[data-testid=\"ticker-container\"]");

                        // title 값을 쉼표로 연결된 문자열로 저장 ex) AAPL,NVDA
                        StringBuilder titles = new StringBuilder();
                        for (var link : links) {
                            if (titles.length() > 0) {
                                titles.append(","); // 쉼표 추가
                            }
                            titles.append(link.attr("title")); // title 값 추가
                        }

                        String stocks = titles.toString();

                        StringBuilder contentBuilder = new StringBuilder();
                        // 각 p 태그의 텍스트를 하나의 String에 합치기
                        for (Element paragraph : paragraphs) {
                            // 앞뒤 공백 제거
                            String content = paragraph.text().trim();
                            if (!content.isEmpty()) {
                                // 여러 공백을 하나로
                                contentBuilder.append(content.replaceAll("\\s+", " ")).append("\n");
                            }
                        }

                        // StringBuilder로 합쳐진 텍스트를 하나의 String으로 변환
                        String articleContent = contentBuilder.toString();



                        try {
                            String response = geminiService.getContents("title :" + articleTitle + "summary :" + articleContent + "번역해주고 summary는 200자 요약만 출력 해줘 출력형식은 title: 제목, summary : 내용");
                            Thread.sleep(5000);
                            String[] parts = response.split("\n");

                            String title = parts[0].replace("title: ", "").trim();
                            String summary = parts[2].replace("summary: ", "").trim();
                            System.out.println("title: " +title);
                            System.out.println("summary: " +summary);
                            articleTitle = title;
                            articleContent = summary;
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        // 데이터 베이스 값 입력
                        Article articleEntity = new Article();
                        articleEntity.setOriginTitle(originTitle);
                        articleEntity.setTitle(articleTitle);
                        articleEntity.setSummary(articleContent);
                        articleEntity.setTime(dateTime);
                        articleEntity.setStocks(stocks);
                        articleEntity.setOriginLink(articleUrl);

                        // 1 ~ 10 사이의 랜덤 점수 생성
                        int randomScore = ThreadLocalRandom.current().nextInt(1, 11); // 범위: [1, 11)
                        articleEntity.setScore(randomScore);

                        // 데이터베이스에 저장
                        articleRepository.save(articleEntity);


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(rank+" 크롤링 완료");
            }
        System.out.println("크롤링 전체 완료");


        int currentCount = (int) articleRepository.count();
        int updateCount = currentCount - lastArticleCount;

        // 만약 추가된 기사가 있으면 웹소켓 통신
        if (updateCount > 0) {
            sseController.sendTopStocks(updateCount,top10StockMentionsService.Top10StockMentions());
        }

        // 마지막 기사 갯수 저장
        lastArticleCount = currentCount;



    }
}
