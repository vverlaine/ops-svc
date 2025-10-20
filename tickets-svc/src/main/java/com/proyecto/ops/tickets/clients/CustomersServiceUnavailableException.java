package com.proyecto.ops.tickets.clients;

public class CustomersServiceUnavailableException extends RuntimeException {
    public CustomersServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}