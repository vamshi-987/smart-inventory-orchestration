package com.vamshi.stockflow_backend.inventory.exception;

public class StockConflictException extends RuntimeException {

    public StockConflictException(String message) {
        super(message);
    }
}