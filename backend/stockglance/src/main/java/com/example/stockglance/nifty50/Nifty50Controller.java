package com.example.stockglance.nifty50;


import com.example.stockglance.common.commonResponse.CommonResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/nifty50")
public class Nifty50Controller {

    private final Nifty50helper nifty50helper;

    // ✅ Injecting Nifty50helper via Constructor
    public Nifty50Controller(Nifty50helper nifty50helper) {
        this.nifty50helper = nifty50helper;
    }

    @GetMapping("/addAfterCloseDetails")
    public CommonResponse fetchStockData() throws Exception {
        CommonResponse commonResponse = new CommonResponse();
        try {
            nifty50helper.fetchAndStoreStockData(); // ✅ Use injected instance
            commonResponse.setCode("200");
            commonResponse.setResponseMessage("Success");
        } catch (Exception e) {
            throw new Exception(e);
        }
        return commonResponse;
    }
}
