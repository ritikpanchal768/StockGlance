package com.example.stockglance.analysis.sentimentAnalysis;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

public class SentimentAnalysisService {
    private final StanfordCoreNLP pipeline;

    public SentimentAnalysisService() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,parse,sentiment");
        pipeline = new StanfordCoreNLP(props);
    }

    public String analyzeSentiment(String text) {
        CoreDocument document = new CoreDocument(text);
        pipeline.annotate(document);

        int sentimentScore = 0;
        int count = 0;

        for (CoreSentence sentence : document.sentences()) {
            String sentiment = sentence.sentiment();
            sentimentScore += getSentimentScore(sentiment);
            count++;
        }

        return getOverallSentiment(sentimentScore / count);
    }

    private int getSentimentScore(String sentiment) {
        switch (sentiment) {
            case "Very Negative": return -2;
            case "Negative": return -1;
            case "Neutral": return 0;
            case "Positive": return 1;
            case "Very Positive": return 2;
            default: return 0;
        }
    }

    private String getOverallSentiment(int avgScore) {
        if (avgScore < -1) return "Very Negative";
        else if (avgScore == -1) return "Negative";
        else if (avgScore == 0) return "Neutral";
        else if (avgScore == 1) return "Positive";
        else return "Very Positive";
    }
}
