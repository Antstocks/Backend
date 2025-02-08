package com.antstocks.project.service.impl;

import com.antstocks.project.repository.ArticleRepository;
import com.antstocks.project.service.Top10StockMentionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        List<String> stockLists = articleRepository.findAllStocks();
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
