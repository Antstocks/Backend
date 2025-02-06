package com.antstocks.project.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
//CORS 허용
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/sse")
public class SseController {
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping("/subscribe")
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter();

        emitter.onCompletion(() -> {
            // 연결 종료 시 처리
            emitters.remove(emitter);
            System.out.println("Connection closed");
        });

        emitter.onError((ex) -> {
            // 에러 발생 시 처리
            emitters.remove(emitter);
            System.out.println("Error occurred: " + ex.getMessage());
        });

        emitter.onTimeout(() -> {
            // 타임아웃 시 처리
            System.out.println("Connection timeout");
            emitters.remove(emitter);
        });

        emitters.add(emitter);
        return emitter;
    }

    public void sendNotification(String message) {
        List<SseEmitter> deadEmitters = new ArrayList<>();

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().data(message));
            } catch (Exception e) {
                deadEmitters.add(emitter); // 실패한 연결 저장
            }
        }

        // 오류 발생한 emitter 제거
        emitters.removeAll(deadEmitters);
    }
}