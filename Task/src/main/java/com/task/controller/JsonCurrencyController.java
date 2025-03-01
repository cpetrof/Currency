package com.task.controller;

import com.task.exceptions.CurrencyNotFoundException;
import com.task.dto.request.CurrencyHistoryRequest;
import com.task.dto.request.CurrencyRequest;
import com.task.service.CurrencyService;
import com.task.service.RabbitMQProducer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/json_api")
public class JsonCurrencyController {
    private final CurrencyService currencyService;
    private final RabbitMQProducer rabbitMQProducer;

    public JsonCurrencyController(CurrencyService currencyService, RabbitMQProducer rabbitMQProducer) {
        this.currencyService = currencyService;
        this.rabbitMQProducer = rabbitMQProducer;
    }
    @GetMapping("/fetch")
    public ResponseEntity<?> fetchCurrencyManually() {
        currencyService.fetchCurrencyData();
        return ResponseEntity.ok("Currency data fetched manually.");
    }


    @PostMapping(value = "/current", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> getCurrent(@RequestBody CurrencyRequest request) {
        if (currencyService.isDuplicateRequest(request.getRequestId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Duplicate request");
        }

        currencyService.logRequest(request.getRequestId(), request.getClient(), "EXT_SERVICE_1");
        rabbitMQProducer.sendRequestLog("EXT_SERVICE_1", request.getRequestId(), request.getClient());

        try {
            Double rate = currencyService.getLatestCurrencyRateForSpecificCurrency(request.getCurrency());
            return ResponseEntity.ok(Map.of("currency", request.getCurrency(), "rate", rate));
        } catch (CurrencyNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Rate for currency not available");
        }
    }


    @PostMapping(value = "/history", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> getHistory(@RequestBody CurrencyHistoryRequest request) {

        if (currencyService.isDuplicateRequest(request.getRequestId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Duplicate request");
        }

        currencyService.logRequest(request.getRequestId(), request.getClient(), "EXT_SERVICE_1");
        rabbitMQProducer.sendRequestLog("EXT_SERVICE_1", request.getRequestId(), request.getClient());

            List<Map<String, Object>> historicalRates =
                    currencyService.getHistoricalRatesForAnyCurrency(request.getCurrency(), request.getPeriod());

        if (historicalRates.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid currency or timestamp");
        }
        return ResponseEntity.ok(historicalRates);
    }

}
