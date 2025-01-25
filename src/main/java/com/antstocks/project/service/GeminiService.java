package com.antstocks.project.service;

import org.springframework.stereotype.Service;

@Service
public interface GeminiService {

    String getContents(String prompt);
}
