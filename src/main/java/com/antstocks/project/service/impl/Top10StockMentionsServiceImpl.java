package com.antstocks.project.service.impl;

import com.antstocks.project.repository.ArticleRepository;
import com.antstocks.project.service.Top10StockMentionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
public class Top10StockMentionsServiceImpl implements Top10StockMentionsService {

    @Autowired
    private ArticleRepository articleRepository;

    @Override
    public List<Map.Entry<String, Integer>> Top10StockMentions() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        List<String> stockLists = articleRepository.findAllStocksToday(startOfDay, endOfDay);
        Map<String, Integer> stockCount = new HashMap<>();

        for (String symbols : stockLists) {
            String[] stocks = symbols.split(",");
            for (String stock : stocks) {
                stockCount.put(stock, stockCount .getOrDefault(stock, 0) + 1);
            }
        }

        return stockCount .entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(10)
                .collect(Collectors.toList());
    }
}
