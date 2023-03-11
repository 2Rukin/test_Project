package com.example.currency.Client;

import java.time.LocalDate;

public interface HttpCurrencyDateRateClient {
    String requestByDate(LocalDate date);
}
