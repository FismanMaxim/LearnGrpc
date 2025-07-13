package com.example.server.exception;

public class ModelNotFoundException extends RuntimeException {
    public ModelNotFoundException(String message) {
        super(message);
    }

    public ModelNotFoundException(Long id) {
        super("Model not found by id " + id);
    }

    public ModelNotFoundException(Class<?> clazz) {
        super("Model not found: " + clazz);
    }

    public ModelNotFoundException(Class<?> clazz, Long id) {
        super("Model not found: " + clazz + " by id " + id);
    }
}
