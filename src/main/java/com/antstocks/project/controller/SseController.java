package com.antstocks.project.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/sse")
public class SseController {
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping("/subscribe")
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(60 * 1000L); // 60초 타임아웃 설정 (기본적으로 1분)

        emitter.onCompletion(() -> {
            removeEmitter(emitter);
            System.out.println("🔴 SSE 연결 종료됨");
        });

        emitter.onError((ex) -> {
            removeEmitter(emitter);
            System.out.println("⚠️ SSE 오류 발생: " + ex.getMessage());
        });

        emitter.onTimeout(() -> {
            removeEmitter(emitter);
            System.out.println("⏳ SSE 타임아웃");
        });

        emitters.add(emitter);
        return emitter;
    }

    private void removeEmitter(SseEmitter emitter) {
        emitters.remove(emitter);
    }

    public void sendTopStocks(int updateCount, List<Map.Entry<String, Integer>> topStocks) {
        Map<String, Object> data = new HashMap<>();
        data.put("notification", "새로운 속보 " + updateCount + "개");
        data.put("topStocks", topStocks);
        System.out.println("📡 SSE 데이터 전송: " + data);

        List<SseEmitter> deadEmitters = new ArrayList<>();

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("combinedData").data(data));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        }

        // 예외가 발생한 emitter는 한 번에 제거 (반복문 안에서 제거하면 문제 발생 가능)
        emitters.removeAll(deadEmitters);
    }
}
