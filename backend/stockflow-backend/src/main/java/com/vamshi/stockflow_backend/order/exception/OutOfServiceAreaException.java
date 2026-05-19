package com.vamshi.stockflow_backend.order.exception;

public class OutOfServiceAreaException extends RuntimeException {

    public OutOfServiceAreaException(String message) {
        super(message);
    }
}