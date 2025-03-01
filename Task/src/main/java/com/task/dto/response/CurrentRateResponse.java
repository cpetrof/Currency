package com.task.dto.response;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlElement;

@XmlRootElement(name = "currentRateResponse")
public class CurrentRateResponse {
    private String currency;
    private Double rate;

    public CurrentRateResponse() {}

    public CurrentRateResponse(String currency, Double rate) {
        this.currency = currency;
        this.rate = rate;
    }

    @XmlElement
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @XmlElement
    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
}
