package com.task.dto.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "historicalRatesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class HistoricalRatesResponse {

    @XmlElement
    private String currency;

    @XmlElement
    private int period;

    @XmlElement(name = "historicalRate")
    private List<HistoricalRate> rates;

    public HistoricalRatesResponse() {}

    public HistoricalRatesResponse(String currency, int period, List<HistoricalRate> rates) {
        this.currency = currency;
        this.period = period;
        this.rates = rates;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public List<HistoricalRate> getRates() {
        return rates;
    }

    public void setRates(List<HistoricalRate> rates) {
        this.rates = rates;
    }
}
