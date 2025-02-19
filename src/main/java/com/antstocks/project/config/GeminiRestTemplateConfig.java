package com.antstocks.project.config;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class GeminiRestTemplateConfig {

    @Bean
    @Qualifier("geminiRestTemplate")

    public RestTemplate geminiRestTemplate() {

        // RestTemplate 객체 생성 (HTTP 요청을 보낼 때 사용하는 Spring의 기본 REST 클라이언트)
        RestTemplate restTemplate = new RestTemplate();

        // 요청을 가로채서 실행 (현재 아무런 변경 없이 실행)
        restTemplate.getInterceptors().add((request, body, execution) -> execution.execute(request, body));

        return restTemplate;
    }
}
