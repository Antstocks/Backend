package com.antstocks.project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String title;

    @Column (length = 50000)
    private String summary;

    @Column(name = "time", columnDefinition = "TIMESTAMP") // 명시적인 컬럼 정의
    private LocalDateTime time;

    @Column(length = 50)
    private String stocks;

    @Column(length = 1000)
    private String originTitle;

    @Column(length = 1000)
    private String originLink;

    private  Integer score;
}
