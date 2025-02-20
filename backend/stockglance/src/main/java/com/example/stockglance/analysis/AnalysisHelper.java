package com.example.stockglance.analysis;

import com.example.stockglance.analysis.sentimentAnalysis.SentimentAnalysisService;
import com.example.stockglance.news.NewsApiResponse;
import com.example.stockglance.news.NewsHelper;

import java.util.List;

public class AnalysisHelper {
    public List<NewsApiResponse.Article> fetchNewsWithSentiment() {
        NewsHelper newsHelper = new NewsHelper();
        List<NewsApiResponse.Article> articles = newsHelper.fetchNews(); // Fetch news from API

        for (NewsApiResponse.Article article : articles) {
            SentimentAnalysisService sentimentAnalysisService = new SentimentAnalysisService();
            String sentiment = sentimentAnalysisService.analyzeSentiment(article.getTitle());
            article.setSentiment(sentiment);
        }

        return articles;
    }
}
