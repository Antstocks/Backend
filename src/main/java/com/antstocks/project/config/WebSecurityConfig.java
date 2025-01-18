package com.antstocks.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")  // "/api/**" 경로에만 보안 설정
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()  // 모든 "/api/**" 요청을 인증 없이 허용
                );

        return http.build();
    }
}