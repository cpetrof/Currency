package com.task.dto.request;

import jakarta.xml.bind.annotation.*;


@XmlAccessorType(XmlAccessType.FIELD)
public class HistoryCommand {

    @XmlAttribute(name = "consumer")
    private String consumer;

    @XmlAttribute(name = "currency")
    private String currency;

    @XmlAttribute(name = "period")
    private int period;


    public String getConsumer() {
        return consumer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
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
}