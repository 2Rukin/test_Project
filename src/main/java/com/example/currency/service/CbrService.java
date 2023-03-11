package com.example.currency.service;


import com.example.currency.Client.HttpCurrencyDateRateClient;
import com.example.currency.schema.ValCurs;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class CbrService {
    private final Cache<LocalDate, Map<String, BigDecimal>> cache;
    private final HttpCurrencyDateRateClient client;

    public CbrService(HttpCurrencyDateRateClient client) {
        this.cache = CacheBuilder.newBuilder().build();
        this.client = client;
    }

    public BigDecimal requestByCurrencyCode(String code) {
        try {
            return cache.get(LocalDate.now(), this::callAllByCurrentDate).get(code);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            throw new RuntimeException("Currency not found", e);
        }
    }


    private Map<String, BigDecimal> callAllByCurrentDate() {
        String xml = client.requestByDate(LocalDate.now());
        ValCurs response = unmarshall(xml);

        return response.getValute()
                .stream()
                .collect(Collectors.toMap(
                        ValCurs.Valute::getCharCode,
                        item -> parseWithLocale(item.getValue())
                ));
    }

    private BigDecimal parseWithLocale(String currency) {
        try {
            double v = NumberFormat.getNumberInstance(Locale.getDefault()).parse(currency).doubleValue();
            return BigDecimal.valueOf(v);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private ValCurs unmarshall(String xml) {
        try (StringReader reader = new StringReader(xml)) {
            JAXBContext context = JAXBContext.newInstance(ValCurs.class);
            return (ValCurs) context.createUnmarshaller().unmarshal(reader);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
