package org.example.movierentals.common.domain.exceptions;

public class MovieNotFoundException extends MovieRentalsException {
    public MovieNotFoundException(String message) {
        super(message);
    }

    public MovieNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MovieNotFoundException(Throwable cause) {
        super(cause);
    }
}
