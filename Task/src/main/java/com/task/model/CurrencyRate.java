package com.task.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Entity
@Table(name = "currency_rate", indexes = {
        @Index(name = "idx_timestamp", columnList = "timestamp DESC")
})
public class CurrencyRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String baseCurrency;
    private LocalDateTime timestamp;

    @ElementCollection
    @CollectionTable(name = "currency_rates", joinColumns = @JoinColumn(name = "id"))
    @MapKeyColumn(name = "currency")
    @Column(name = "rate")
    private Map<String, Double> rates;

    public CurrencyRate() {}

    public CurrencyRate(String baseCurrency, LocalDateTime timestamp, Map<String, Double> rates) {
        this.baseCurrency = baseCurrency;
        this.timestamp = timestamp;
        this.rates = rates;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Double> getRates() {
        return rates;
    }

    public void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }
}

