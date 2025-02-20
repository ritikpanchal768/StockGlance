package com.example.stockglance.nifty50;

import com.example.stockglance.common.dbUtils.DbUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class Nifty50helper {
    private final JdbcTemplate jdbcTemplate;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper(); // ✅ JSON Parser

    public Nifty50helper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final List<String> symbols = List.of(
            "ADANIENT.NS", "ADANIPORTS.NS", "APOLLOHOSP.NS", "ASIANPAINT.NS",
            "AXISBANK.NS", "BAJAJ-AUTO.NS", "BAJFINANCE.NS", "BAJAJFINSV.NS",
            "BEL.NS", "BPCL.NS", "BHARTIARTL.NS", "BRITANNIA.NS", "CIPLA.NS",
            "COALINDIA.NS", "DRREDDY.NS", "EICHERMOT.NS", "GRASIM.NS", "HCLTECH.NS",
            "HDFCBANK.NS", "HDFCLIFE.NS", "HEROMOTOCO.NS", "HINDALCO.NS", "HINDUNILVR.NS",
            "ICICIBANK.NS", "ITC.NS", "INDUSINDBK.NS", "INFY.NS", "JSWSTEEL.NS",
            "KOTAKBANK.NS", "LT.NS", "M&M.NS", "MARUTI.NS", "NTPC.NS", "NESTLEIND.NS",
            "ONGC.NS", "POWERGRID.NS", "RELIANCE.NS", "SBILIFE.NS", "SHRIRAMFIN.NS",
            "SBIN.NS", "SUNPHARMA.NS", "TCS.NS", "TATACONSUM.NS", "TATAMOTORS.NS",
            "TATASTEEL.NS", "TECHM.NS", "TITAN.NS", "TRENT.NS", "ULTRACEMCO.NS",
            "WIPRO.NS"
    );



    @Scheduled(cron = "0 0 8 * * ?") // Runs every day at 8 AM
    public void fetchAndStoreStockData() {
        cleanPreviousData();
        for (String symbol : symbols) {
            fetchAndInsertStockData(symbol);
        }
    }

    private void cleanPreviousData() {
        jdbcTemplate.update("DELETE FROM nifty50prices");
    }

    private void fetchAndInsertStockData(String symbol) {
        try {
            String url = "https://query1.finance.yahoo.com/v8/finance/chart/" + symbol + "?interval=1d&range=1d";
            System.out.println("Hitting URL: " + url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0"); // Prevents bot detection
            headers.set("Accept", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                parseAndInsertStockData(response.getBody(), symbol);
            } else {
                System.out.println("Failed to fetch data for " + symbol);
            }
        } catch (Exception e) {
            System.err.println("Error fetching data for " + symbol + ": " + e.getMessage());
        }
    }

    private void parseAndInsertStockData(String responseBody, String symbol) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode resultNode = rootNode.path("chart").path("result").get(0);
            if (resultNode == null) return;

            JsonNode meta = resultNode.path("meta");
            JsonNode indicators = resultNode.path("indicators").path("quote").get(0);

            if (indicators == null) return;

            double marketPrice = indicators.path("close").get(0).asDouble();
            double high = indicators.path("high").get(0).asDouble();
            double low = indicators.path("low").get(0).asDouble();
            double prevClose = indicators.path("open").get(0).asDouble();
            long volume = indicators.path("volume").get(0).asLong();
            String companyName = getCompanyName(symbol);
            Nifty50Prices stock = new Nifty50Prices();
            stock.setSymbol(symbol.replace(".NS", ""));
            stock.setCompanyname(companyName);
            stock.setMarketprice(marketPrice);
            // ✅ Fetch 52-week high and low from meta
            double fiftyTwoWeekHigh = meta.path("fiftyTwoWeekHigh").asDouble(0.0);
            double fiftyTwoWeekLow = meta.path("fiftyTwoWeekLow").asDouble(0.0);

            stock.setDayhigh(high);
            stock.setDaylow(low);
            stock.setVolume(volume);
            stock.setFiftytwoweekhigh(fiftyTwoWeekHigh);
            stock.setFiftytwoweeklow(fiftyTwoWeekLow);
            stock.setPrevclose(prevClose);
            stock.setCreatedOn(Timestamp.valueOf(LocalDateTime.now()));
            stock.setModifiedOn(Timestamp.valueOf(LocalDateTime.now()));
            stock.setCreatedBy("admin");
            stock.setModifiedBy("admin");

//            jdbcTemplate.update(
//                    "INSERT INTO nifty50prices (id, createdon, modifiedon, createdby, modifiedby, symbol, companyname, timestamp, marketprice, fiftytwoweekhigh, fiftytwoweeklow, dayhigh, daylow, volume, prevclose) " +
//                            "VALUES (gen_random_uuid(), NOW(), NOW(), 'admin', 'admin', ?, ?, NOW(), ?, ?, ?, ?, ?, ?, ?)",
//                    symbol.replace(".NS", ""), companyName, marketPrice, 0.0, 0.0, high, low, volume, prevClose
//            );
            new DbUtils().saveObject(stock,"nifty50prices");
            System.out.println("Inserted data for: " + symbol);
        } catch (Exception e) {
            System.err.println("Error parsing data for " + symbol + ": " + e.getMessage());
        }
    }

    private String getCompanyName(String symbol) {
        Map<String, String> companyMap = Map.ofEntries(
                Map.entry("ADANIENT.NS", "Adani Enterprises Ltd."),
                Map.entry("ADANIPORTS.NS", "Adani Ports and Special Economic Zone Ltd."),
                Map.entry("APOLLOHOSP.NS", "Apollo Hospitals Enterprise Ltd."),
                Map.entry("ASIANPAINT.NS", "Asian Paints Ltd."),
                Map.entry("AXISBANK.NS", "Axis Bank Ltd."),
                Map.entry("BAJAJ-AUTO.NS", "Bajaj Auto Ltd."),
                Map.entry("BAJFINANCE.NS", "Bajaj Finance Ltd."),
                Map.entry("BAJAJFINSV.NS", "Bajaj Finserv Ltd."),
                Map.entry("BEL.NS", "Bharat Electronics Ltd."),
                Map.entry("BPCL.NS", "Bharat Petroleum Corporation Ltd."),
                Map.entry("BHARTIARTL.NS", "Bharti Airtel Ltd."),
                Map.entry("BRITANNIA.NS", "Britannia Industries Ltd."),
                Map.entry("CIPLA.NS", "Cipla Ltd."),
                Map.entry("COALINDIA.NS", "Coal India Ltd."),
                Map.entry("DRREDDY.NS", "Dr. Reddy's Laboratories Ltd."),
                Map.entry("EICHERMOT.NS", "Eicher Motors Ltd."),
                Map.entry("GRASIM.NS", "Grasim Industries Ltd."),
                Map.entry("HCLTECH.NS", "HCL Technologies Ltd."),
                Map.entry("HDFCBANK.NS", "HDFC Bank Ltd."),
                Map.entry("HDFCLIFE.NS", "HDFC Life Insurance Company Ltd."),
                Map.entry("HEROMOTOCO.NS", "Hero MotoCorp Ltd."),
                Map.entry("HINDALCO.NS", "Hindalco Industries Ltd."),
                Map.entry("HINDUNILVR.NS", "Hindustan Unilever Ltd."),
                Map.entry("ICICIBANK.NS", "ICICI Bank Ltd."),
                Map.entry("ITC.NS", "ITC Ltd."),
                Map.entry("INDUSINDBK.NS", "IndusInd Bank Ltd."),
                Map.entry("INFY.NS", "Infosys Ltd."),
                Map.entry("JSWSTEEL.NS", "JSW Steel Ltd."),
                Map.entry("KOTAKBANK.NS", "Kotak Mahindra Bank Ltd."),
                Map.entry("LT.NS", "Larsen & Toubro Ltd."),
                Map.entry("M&M.NS", "Mahindra & Mahindra Ltd."),
                Map.entry("MARUTI.NS", "Maruti Suzuki India Ltd."),
                Map.entry("NTPC.NS", "NTPC Ltd."),
                Map.entry("NESTLEIND.NS", "Nestle India Ltd."),
                Map.entry("ONGC.NS", "Oil & Natural Gas Corporation Ltd."),
                Map.entry("POWERGRID.NS", "Power Grid Corporation of India Ltd."),
                Map.entry("RELIANCE.NS", "Reliance Industries Ltd."),
                Map.entry("SBIN.NS", "State Bank of India")
        );

        return companyMap.get(symbol);
    }
}
