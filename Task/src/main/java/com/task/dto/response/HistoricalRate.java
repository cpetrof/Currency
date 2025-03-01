package com.task.dto.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "historicalRate")
@XmlAccessorType(XmlAccessType.FIELD)
public class HistoricalRate {

    @XmlElement
    private String date;

    @XmlElement(name = "value")
    private Double rate;

    public HistoricalRate() {}

    public HistoricalRate(String date, Double rate) {
        this.date = date;
        this.rate = rate;
    }

    // Getters and setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
}
