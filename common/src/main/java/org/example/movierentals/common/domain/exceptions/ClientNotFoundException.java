package org.example.movierentals.common.domain.exceptions;

public class ClientNotFoundException extends MovieRentalsException {
    public ClientNotFoundException(String message) {
        super(message);
    }

    public ClientNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientNotFoundException(Throwable cause) {
        super(cause);
    }
}
