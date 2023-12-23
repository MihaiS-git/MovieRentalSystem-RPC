package org.example.movierentals.common.domain.exceptions;

public class MovieRentalsException extends RuntimeException{
    public MovieRentalsException(String message) {
        super(message);
    }

    public MovieRentalsException(String message, Throwable cause) {
        super(message, cause);
    }

    public MovieRentalsException(Throwable cause) {
        super(cause);
    }
}
