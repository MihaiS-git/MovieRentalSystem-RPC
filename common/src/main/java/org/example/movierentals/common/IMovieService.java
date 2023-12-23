package org.example.movierentals.common;

import org.example.movierentals.common.domain.Movie;

import java.util.Set;
import java.util.concurrent.Future;

public interface IMovieService {

    Future<String> getAllMovies();

    Future<String> addMovie(Movie movie);

    Future<String> getMovieById(Long id);

    Future<String> updateMovie(Movie movie);

    Future<String> deleteMovieById(Long id);

    Future<String> filterMoviesByKeyword(String keyword);

}

