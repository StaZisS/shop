package com.example.shop.public_interface.exception;

public class ExceptionInApplication extends RuntimeException {
    private final ExceptionType type;

    public ExceptionInApplication(String message, ExceptionType type) {
        super(message);
        this.type = type;
    }

    public ExceptionType getType() {
        return type;
    }
}
