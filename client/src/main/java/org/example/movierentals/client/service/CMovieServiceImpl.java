package org.example.movierentals.client.service;

import org.example.movierentals.common.Message;
import org.example.movierentals.common.IMovieService;
import org.example.movierentals.client.tcp.TcpClient;
import org.example.movierentals.common.domain.Movie;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class CMovieServiceImpl implements IMovieService {
    ExecutorService executorService;
    TcpClient tcpClient;

    public CMovieServiceImpl(ExecutorService executorService, TcpClient tcpClient) {
        this.executorService = executorService;
        this.tcpClient = tcpClient;
    }

    @Override
    public Future<String> getAllMovies() {
        return executorService.submit(() -> {
            Message request = new Message("getAllMovies");
            return tcpClient.sendAndReceive(request).getBody();
        });
    }


    @Override
    public Future<String> addMovie(Movie movie) {
        StringBuilder sb = new StringBuilder();
        sb.append(movie.getTitle()).append(",")
                .append(movie.getYear()).append(",")
                .append(movie.getGenre()).append(",")
                .append(movie.getAgeRestrictions()).append(",")
                .append(movie.getRentalPrice()).append(",")
                .append(movie.isAvailable());

        return executorService.submit(() -> {
            Message request = new Message("addMovie", sb.toString());
            return tcpClient.sendAndReceive(request).getBody();
        });
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Future<String> getMovieById(Long id) {
        return executorService.submit(() -> {
            Message request = new Message("getMovieById", String.valueOf(id));
            return tcpClient.sendAndReceive(request).getBody();
        });
    }

    @Override
    public Future<String> updateMovie(Movie movie) {
        StringBuilder sb = new StringBuilder();
        sb.append(movie.getId()).append(",")
                .append(movie.getTitle()).append(",")
                .append(movie.getYear()).append(",")
                .append(movie.getGenre()).append(",")
                .append(movie.getAgeRestrictions()).append(",")
                .append(movie.getRentalPrice()).append(",")
                .append(movie.isAvailable());

        return executorService.submit(() -> {
            Message request = new Message("updateMovie", sb.toString());
            return tcpClient.sendAndReceive(request).getBody();
        });
    }

    @Override
    public Future<String> deleteMovieById(Long id) {
        return executorService.submit(() -> {
            Message request = new Message("deleteMovieById", String.valueOf(id));
            return tcpClient.sendAndReceive(request).getBody();
        });
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Future<String> filterMoviesByKeyword(String keyword) {
        return executorService.submit(() -> {
            Message request = new Message("filterMoviesByKeyword", keyword);
            return tcpClient.sendAndReceive(request).getBody();
        });
    }
}