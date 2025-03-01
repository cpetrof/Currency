package com.task.controller;

import com.task.dto.response.CurrentRateResponse;
import com.task.dto.response.HistoricalRatesResponse;
import com.task.dto.response.HistoricalRate;
import com.task.dto.request.XmlCommand;
import com.task.exceptions.CurrencyNotFoundException;
import com.task.service.CurrencyService;
import com.task.service.RabbitMQProducer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/xml_api")
public class XmlCurrencyController {
    private final CurrencyService currencyService;
    private final RabbitMQProducer rabbitMQProducer;

    public XmlCurrencyController(CurrencyService currencyService, RabbitMQProducer rabbitMQProducer) {
        this.currencyService = currencyService;
        this.rabbitMQProducer = rabbitMQProducer;
    }

    @PostMapping(value = "/command", consumes = "application/xml", produces = "application/xml")
    public ResponseEntity<String> handleXmlCommand(@RequestBody XmlCommand command) {
        String requestId = command.getId();
        String clientId = command.getHistory() != null ? command.getHistory().getConsumer() :
                command.getGet() != null ? command.getGet().getConsumer() : "UNKNOWN";

        if (currencyService.isDuplicateRequest(requestId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("<error>Duplicate request</error>");
        }

        currencyService.logRequest(requestId, clientId, "EXT_SERVICE_2");
        rabbitMQProducer.sendRequestLog("EXT_SERVICE_2", requestId, clientId);

        try {
            if (command.getGet() != null) {

                String currency = command.getGet().getCurrency();
                Double rate = currencyService.getLatestCurrencyRateForSpecificCurrency(currency);

                if (rate == null) {
                    throw new CurrencyNotFoundException("Rate not found for currency: " + currency);
                }

                return ResponseEntity.ok(convertToXml(new CurrentRateResponse(currency, rate)));

            } else if (command.getHistory() != null) {

                String currency = command.getHistory().getCurrency();
                int period = command.getHistory().getPeriod();

                List<Map<String, Object>> historicalRatesData =
                        currencyService.getHistoricalRatesForAnyCurrency(currency, period);

                if (historicalRatesData.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("<error>Invalid currency or timestamp</error>");
                }

                List<HistoricalRate> historicalRates = historicalRatesData.stream().map(map -> {
                    String date = (String) map.get("date");
                    Double rateVal = Double.valueOf(map.get("rate").toString());
                    return new HistoricalRate(date, rateVal);
                }).collect(Collectors.toList());

                return ResponseEntity.ok(
                        convertToXml(new HistoricalRatesResponse(currency, period, historicalRates))
                );
            } else {

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("<error>Invalid command format</error>");
            }
        } catch (CurrencyNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("<error>" + e.getMessage() + "</error>");
        }
    }

    private <T> String convertToXml(T object) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter writer = new StringWriter();
            marshaller.marshal(object, writer);
            return writer.toString();
        } catch (JAXBException e) {
            return "<error>XML conversion error</error>";
        }
    }
}
