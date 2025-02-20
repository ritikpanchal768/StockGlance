package com.example.stockglance.nifty50;

import com.example.stockglance.common.commonExtend.CommonExtend;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;


public class Nifty50Prices extends CommonExtend {
    private String symbol;
    private String companyname;
    private Timestamp timestamp;
    private double marketprice;
    private double fiftytwoweekhigh;
    private double fiftytwoweeklow;
    private double dayhigh;
    private double daylow;
    private long volume;
    private double prevclose;


    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getCompanyname() { return companyname; }
    public void setCompanyname(String companyname) { this.companyname = companyname; }

    public double getMarketprice() { return marketprice; }
    public void setMarketprice(double marketprice) { this.marketprice = marketprice; }

    public double getFiftytwoweekhigh() { return fiftytwoweekhigh; }
    public void setFiftytwoweekhigh(double fiftytwoweekhigh) { this.fiftytwoweekhigh = fiftytwoweekhigh; }

    public double getFiftytwoweeklow() { return fiftytwoweeklow; }
    public void setFiftytwoweeklow(double fiftytwoweeklow) { this.fiftytwoweeklow = fiftytwoweeklow; }

    public double getDayhigh() { return dayhigh; }
    public void setDayhigh(double dayhigh) { this.dayhigh = dayhigh; }

    public double getDaylow() { return daylow; }
    public void setDaylow(double daylow) { this.daylow = daylow; }

    public long getVolume() { return volume; }
    public void setVolume(long volume) { this.volume = volume; }

    public double getPrevclose() { return prevclose; }
    public void setPrevclose(double prevclose) { this.prevclose = prevclose; }


}
