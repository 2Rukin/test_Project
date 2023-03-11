package com.example.currency.controller;

import com.example.currency.service.CbrService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/сurrency")
@RequiredArgsConstructor
public class CurrencyController {
    private final CbrService currencyService;

    @GetMapping("/rate/{code}")
    public BigDecimal currencyRate(@PathVariable("code") String code) {
        return currencyService.requestByCurrencyCode(code);
    }
}
