package com.example.currency.Client;


import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class CbrCurrencyRateClient implements HttpCurrencyDateRateClient {
    private static final String DATE_PATTERN = "dd/MM/yyyy";

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);


    @Override
    public String requestByDate(LocalDate date) {
        var baseUrl = "https://cbr.ru/scripts/XML_daily.asp";
        var client = HttpClient.newHttpClient();
        var url = buildUriRequest(baseUrl, date);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String buildUriRequest(String baseUrl, LocalDate date) {
        return UriComponentsBuilder.fromUri(URI.create(baseUrl)).queryParam("date_req", DATE_TIME_FORMATTER.format(date))
                .build().toUriString();
    }
}
