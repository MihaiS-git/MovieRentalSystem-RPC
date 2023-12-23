package org.example.movierentals.server.service;

import org.example.movierentals.common.IMovieService;
import org.example.movierentals.common.domain.Movie;
import org.example.movierentals.server.repository.MovieDBRepository;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SMovieServiceImpl implements IMovieService {
    private MovieDBRepository movieRepository;
    private ExecutorService executorService;

    public SMovieServiceImpl(ExecutorService executorService, MovieDBRepository movieRepository) {
        this.executorService = executorService;
        this.movieRepository = movieRepository;
    }

    @Override
    public Future<String> getAllMovies() {
        Iterable<Movie> movies = movieRepository.findAll();
        if (StreamSupport.stream(movies.spliterator(), false).findAny().isPresent()) {
            StringBuilder sb = new StringBuilder();
            for (Movie movie : movies) {
                sb.append(movie.getId()).append(",")
                        .append(movie.getTitle()).append(",")
                        .append(movie.getYear()).append(",")
                        .append(movie.getGenre()).append(",")
                        .append(movie.getAgeRestrictions()).append(",")
                        .append(movie.getRentalPrice()).append(",")
                        .append(movie.isAvailable()).append(";");
            }
            return executorService.submit(() -> sb.toString());
        } else {
            return executorService.submit(() -> "400 Error,No movies were found in the Database.");
        }
    }

    @Override
    public Future<String> addMovie(Movie movie) {
        Optional<Movie> savedMovie = movieRepository.save(movie);
        if (savedMovie.isPresent()) {
            Movie responseMovie = savedMovie.get();
            StringBuilder sb = new StringBuilder();
            sb.append(movie.getId()).append(",")
                    .append(movie.getTitle()).append(",")
                    .append(movie.getYear()).append(",")
                    .append(movie.getGenre()).append(",")
                    .append(movie.getAgeRestrictions()).append(",")
                    .append(movie.getRentalPrice()).append(",")
                    .append(movie.isAvailable());
            return executorService.submit(() -> sb.toString());
        }
        return executorService.submit(() -> "400 Error,Movie not saved.");
    }

    @Override
    public Future<String> getMovieById(Long id) {
        Optional<Movie> movieOptional = movieRepository.findOne(id);
        if (movieOptional.isPresent()) {
            Movie movie = movieOptional.get();
            StringBuilder sb = new StringBuilder();
            sb.append(movie.getId()).append(",")
                    .append(movie.getTitle()).append(",")
                    .append(movie.getYear()).append(",")
                    .append(movie.getGenre()).append(",")
                    .append(movie.getAgeRestrictions()).append(",")
                    .append(movie.getRentalPrice()).append(",")
                    .append(movie.isAvailable());
            return executorService.submit(() -> sb.toString());
        } else {
            return executorService.submit(() -> "400 Error,Movie not found.");
        }
    }

    @Override
    public Future<String> updateMovie(Movie movie) {
        Optional<Movie> updatedMovie = movieRepository.update(movie);
        if (updatedMovie.isPresent()) {
            Movie responseMovie = updatedMovie.get();
            StringBuilder sb = new StringBuilder();
            sb.append(movie.getId()).append(",")
                    .append(movie.getTitle()).append(",")
                    .append(movie.getYear()).append(",")
                    .append(movie.getGenre()).append(",")
                    .append(movie.getAgeRestrictions()).append(",")
                    .append(movie.getRentalPrice()).append(",")
                    .append(movie.isAvailable());
            return executorService.submit(() -> sb.toString());
        }
        return executorService.submit(() -> "400 Error,Movie not found for update.");
    }

    @Override
    public Future<String> deleteMovieById(Long id) {
        Optional<Movie> movieOptional = movieRepository.delete(id);
        if (movieOptional.isPresent()) {
            Movie movie = movieOptional.get();
            StringBuilder sb = new StringBuilder();
            sb.append(movie.getId()).append(",")
                    .append(movie.getTitle()).append(",")
                    .append(movie.getYear()).append(",")
                    .append(movie.getGenre()).append(",")
                    .append(movie.getAgeRestrictions()).append(",")
                    .append(movie.getRentalPrice()).append(",")
                    .append(movie.isAvailable());
            return executorService.submit(() -> sb.toString());
        } else {
            return executorService.submit(() -> "400 Error,Movie not found to delete.");
        }
    }

    @Override
    public Future<String> filterMoviesByKeyword(String keyword) {
        Iterable<Movie> moviesSet = movieRepository.findAll();
        if (StreamSupport.stream(moviesSet.spliterator(), false).findAny().isPresent()) {
            StringBuilder sb = new StringBuilder();
            Set<Movie> filteredMovies = StreamSupport.stream(moviesSet.spliterator(), false)
                    .filter(m -> m.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toSet());
            if (filteredMovies.size() > 0) {
                filteredMovies.forEach(m -> {
                    sb.append(m.getId()).append(",")
                            .append(m.getTitle()).append(",")
                            .append(m.getYear()).append(",")
                            .append(m.getGenre()).append(",")
                            .append(m.getAgeRestrictions()).append(",")
                            .append(m.getRentalPrice()).append(",")
                            .append(m.isAvailable()).append(";");
                });
            } else {
                sb.append("400 Error, Invalid Keyword.");
            }
            return executorService.submit(() -> sb.toString());
        } else {
            return executorService.submit(() -> "400 Error,No movies in the Database.");
        }
    }
}
