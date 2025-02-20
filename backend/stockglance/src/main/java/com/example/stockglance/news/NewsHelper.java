package com.example.stockglance.news;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;


public class NewsHelper {
    private final RestTemplate restTemplate = new RestTemplate();


    public List<NewsApiResponse.Article> fetchNews() {
        // Get today's date in YYYY-MM-DD format
        String today = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String apiKey = "c2337c75255848c28da85c6bd6b60d05";
        final String apiUrl = "https://newsapi.org/v2/everything?"
                + "q=stock+market+India"
                + "&language=en"
                + "&from=" + today
                + "&sortBy=publishedAt"
                + "&apiKey=" + apiKey;
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
        System.out.println("Raw API Response: " + response.getBody());

        if (response.getBody() != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                NewsApiResponse apiResponse = objectMapper.readValue(response.getBody(), NewsApiResponse.class);
                return apiResponse.getArticles();
            }
            catch (Exception e){
                e.printStackTrace();
                return List.of();
            }
        } else {
            return List.of();
        }
    }
}
