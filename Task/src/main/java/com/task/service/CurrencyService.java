package com.task.service;

import com.task.exceptions.CurrencyNotFoundException;
import com.task.model.CurrencyRate;
import com.task.model.RequestLog;
import com.task.repository.CurrencyRateRepository;
import com.task.repository.RequestLogRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CurrencyService {

    @Value("${fixer.api.url}")
    private String apiUrl;

    @Value("${fixer.api.key}")
    private String apiKey;

    @Value("${rabbitmq.exchange}")
    private String exchangeName;

    private final CurrencyRateRepository currencyRateRepository;
    private final RequestLogRepository requestLogRepository;
    private final RestTemplate restTemplate;

    public CurrencyService(CurrencyRateRepository currencyRateRepository, RequestLogRepository requestLogRepository, RabbitTemplate rabbitTemplate) {
        this.currencyRateRepository = currencyRateRepository;
        this.requestLogRepository = requestLogRepository;
        this.restTemplate = new RestTemplate();
    }

    public boolean isDuplicateRequest(String requestId) {
        return requestLogRepository.existsByRequestId(requestId);
    }

    @Cacheable(value = "latestCurrencyRates", key = "#currency", unless = "#result == null")
    public Double getLatestCurrencyRateForSpecificCurrency(String currency) {
        Optional<CurrencyRate> latestRate = currencyRateRepository.findTopByOrderByTimestampDesc();
        if (latestRate.isEmpty()) {
            throw new CurrencyNotFoundException("No currency data available");
        }

        Map<String, Double> rates = latestRate.get().getRates();
        Double rate = rates.get(currency);
        if (rate == null) {
            throw new CurrencyNotFoundException("Rate for " + currency + " is not available");
        }

        return rate;
    }
    @Cacheable(value = "historicalCurrencyRates", key = "#currency.concat('-').concat(#period)", unless = "#result == null || #result.isEmpty()")
    public List<Map<String, Object>> getHistoricalRatesForAnyCurrency(String currency, int period) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(period);


        List<CurrencyRate> storedRates = currencyRateRepository.findByTimestampBetween(startTime, endTime);
        if (storedRates.isEmpty()) {
            System.out.println("No historical rates found for this currency and period");
            return new ArrayList<>();
        }
        List<Map<String, Object>> historicalRates = new ArrayList<>();
        for (CurrencyRate rateEntry : storedRates) {
            Double rateForCurrency = rateEntry.getRates().get(currency); 
            if (rateForCurrency != null) { 
                Map<String, Object> responseEntry = new HashMap<>();
                responseEntry.put("timestamp", rateEntry.getTimestamp());
                responseEntry.put("currency", currency);
                responseEntry.put("rate", rateForCurrency);

                historicalRates.add(responseEntry);
            }
        }
        if (historicalRates.isEmpty()) {
            return Collections.emptyList();
        }
        return historicalRates;
    }

    public void logRequest(String requestId, String clientId, String serviceName) {
        requestLogRepository.save(new RequestLog(requestId, clientId, serviceName));
    }

    @Scheduled(fixedRateString = "${currency.update.interval}")
    public void fetchCurrencyData() {
        try {
            String url = apiUrl + "?access_key=" + apiKey;
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            if (response != null && response.get("success").asBoolean()) {
                String baseCurrency = response.get("base").asText();
                JsonNode ratesNode = response.get("rates");
                Map<String, Double> rates = new HashMap<>();
                ratesNode.fields().forEachRemaining(entry -> rates.put(entry.getKey(), entry.getValue().asDouble()));
                CurrencyRate currencyRate = new CurrencyRate(baseCurrency, LocalDateTime.now(), rates);
                currencyRateRepository.save(currencyRate);
                System.out.println("Successfully fetched and saved currency data: " + rates);
                clearCache();
            } else {
                System.err.println("Failed to fetch data from fixer.io: " + response);
            }
        } catch (Exception e) {
            System.err.println("Error fetching currency data: " + e.getMessage());
        }
    }

    /**
     * Clear the cache for latest and historical currency rates .
     */
    @CacheEvict(value = {"latestCurrencyRates", "historicalCurrencyRates"}, allEntries = true)
    @Scheduled(fixedRate = 3600000)
    public void clearCache() {
        System.out.println("Clearing all currency rate caches");
    }

}
