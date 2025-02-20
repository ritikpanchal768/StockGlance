package com.example.stockglance.analysis;

import com.example.stockglance.news.NewsApiResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.stockglance.common.commonResponse.CommonResponse;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/analysis")
public class AnalysisController {

    @GetMapping("/latest")
    public CommonResponse<List<NewsApiResponse.Article>> getLatestNewsWithSentiment()throws Exception {
        AnalysisHelper analysisHelper = new AnalysisHelper();
        CommonResponse<List<NewsApiResponse.Article>> commonResponse = new CommonResponse<>();
        try{
           commonResponse.setResponseObject(analysisHelper.fetchNewsWithSentiment());
           if(commonResponse.getResponseObject()!=null){
               commonResponse.setCode("200");
               commonResponse.setResponseMessage("Analysis Complete");
           }
        }catch (Exception e){
            throw new Exception(e);
        }
        return commonResponse;
    }
}
