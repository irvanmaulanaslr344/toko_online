package com.tokoonline.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StockNotAvailableException extends RuntimeException {

    public StockNotAvailableException(String productName, Integer requested, Integer available) {
        super(String.format("Stok '%s' tidak mencukupi. Diminta: %d, Tersedia: %d",
                productName, requested, available));
    }
}