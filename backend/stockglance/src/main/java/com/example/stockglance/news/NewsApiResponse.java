package com.example.stockglance.news;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)  // Ignore unexpected JSON fields
public class NewsApiResponse {

    @JsonProperty("status")
    private String status;

    @JsonProperty("totalResults")
    private int totalResults;

    @JsonProperty("articles")
    private List<Article> articles;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Article {

        @JsonProperty("title")
        private String title;

        @JsonProperty("description")
        private String description;

        @JsonProperty("url")
        private String url;

        @JsonProperty("urlToImage")
        private String urlToImage;

        @JsonProperty("content")
        private String content;

        @JsonProperty("source")
        private Source source;

        @JsonProperty("author")
        private String author;

        @JsonProperty("publishedAt")
        private String publishedAt;

        @JsonProperty("sentiment")
        private String sentiment;  // This might not exist in the response, check if it's needed

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }

        public String getUrlToImage() { return urlToImage; }
        public void setUrlToImage(String urlToImage) { this.urlToImage = urlToImage; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public Source getSource() { return source; }
        public void setSource(Source source) { this.source = source; }

        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }

        public String getPublishedAt() { return publishedAt; }
        public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }

        public String getSentiment() { return sentiment; }
        public void setSentiment(String sentiment) { this.sentiment = sentiment; }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Source {
            @JsonProperty("id")
            private String id;

            @JsonProperty("name")
            private String name;

            public String getId() { return id; }
            public void setId(String id) { this.id = id; }

            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
        }
    }
}
