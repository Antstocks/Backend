package com.antstocks.project.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
public interface Top10StockMentionsService {
    List<Map.Entry<String, Integer>> Top10StockMentions();
}
