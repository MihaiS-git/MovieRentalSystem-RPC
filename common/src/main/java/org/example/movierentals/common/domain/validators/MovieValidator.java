package org.example.movierentals.common.domain.validators;

import org.example.movierentals.common.domain.Movie;
import org.example.movierentals.common.domain.exceptions.ValidatorException;

import java.time.LocalDateTime;

public class MovieValidator implements Validator<Movie>{
    @Override
    public void validate(Movie entity){
        validateTitle(entity);
        validateMovieYear(entity);
        validateMovieGenre(entity);
        validateMovieAgeRestrictions(entity);
        validateRentalPrice(entity);
    }

    private static void validateRentalPrice(Movie movie){
        if(movie.getRentalPrice() < 0){
            throw new ValidatorException("Rental Price can not be negative.");
        }
    }

    private static void validateMovieYear(Movie movie){
        if(movie.getYear() < 1900 || movie.getYear() > LocalDateTime.now().getYear()){
            throw new ValidatorException("Year of the movie release should be in the (1900 - current year) interval.");
        }
    }

    private static void validateTitle(Movie movie){
        if(movie.getTitle().isBlank()){
            throw new ValidatorException("Movie Title cannot be blank.");
        }
    }

    private static void validateMovieGenre(Movie movie){
        if(movie.getGenre().getDescription().isEmpty()){
            throw new ValidatorException("Movie Genre can not be empty.");
        }
    }

    private static void validateMovieAgeRestrictions(Movie movie){
        if (movie.getAgeRestrictions().getDescription().isEmpty()) {
            throw new ValidatorException("Movie Age Restriction can not be empty.");
        }
    }
}
