package org.example.movierentals.client.service;

import org.example.movierentals.client.tcp.TcpClient;
import org.example.movierentals.common.IRentalService;
import org.example.movierentals.common.Message;
import org.example.movierentals.common.domain.Rental;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class CRentalServiceImpl implements IRentalService {
    ExecutorService executorService;
    TcpClient tcpClient;

    public CRentalServiceImpl(ExecutorService executorService, TcpClient tcpClient) {
        this.executorService = executorService;
        this.tcpClient = tcpClient;
    }

    @Override
    public Future<String> getAllRentals() {
        return executorService.submit(() -> {
            Message request = new Message("getAllRentals");
            return tcpClient.sendAndReceive(request).getBody();
        });
    }

    @Override
    public Future<String> getRentalById(Long id) {
        return executorService.submit(() -> {
            Message request = new Message("getRentalById", String.valueOf(id));
            return tcpClient.sendAndReceive(request).getBody();
        });
    }

    @Override
    public Future<String> rentAMovie(Rental rental) {
        StringBuilder sb = new StringBuilder();
        sb.append(rental.getMovieId()).append(",")
                .append(rental.getClientId()).append(",")
                .append(rental.getRentalCharge()).append(",")
                .append(rental.getRentalDate()).append(",")
                .append(rental.getDueDate());

        return executorService.submit(() -> {
            Message request = new Message("rentAMovie", sb.toString());
            return tcpClient.sendAndReceive(request).getBody();
        });
    }

    @Override
    public Future<String> updateRentalTransaction(Rental rental) {
        StringBuilder sb = new StringBuilder();
        sb.append(rental.getId()).append(",")
                .append(rental.getMovieId()).append(",")
                .append(rental.getClientId()).append(",")
                .append(rental.getRentalCharge()).append(",")
                .append(rental.getRentalDate()).append(",")
                .append(rental.getDueDate());

        return executorService.submit(() -> {
            Message request = new Message("updateRentalTransaction", sb.toString());
            return tcpClient.sendAndReceive(request).getBody();
        });
    }

    @Override
    public Future<String> deleteMovieRental(Long rentalId) {
        return executorService.submit(() -> {
            Message request = new Message("deleteMovieRental", String.valueOf(rentalId));
            return tcpClient.sendAndReceive(request).getBody();
        });
    }

    @Override
    public Future<String> moviesByRentNumber() {
        return executorService.submit(() -> {
            Message request = new Message("moviesByRentNumber");
            return tcpClient.sendAndReceive(request).getBody();
        });
    }

    @Override
    public Future<String> clientsByRentNumber() {
        return executorService.submit(() -> {
            Message request = new Message("clientsByRentNumber");
            return tcpClient.sendAndReceive(request).getBody();
        });
    }

    @Override
    public Future<String> generateReportByClient(Long id) {
        return executorService.submit(() -> {
            Message request = new Message("generateReportByClient", String.valueOf(id));
            return tcpClient.sendAndReceive(request).getBody();
        });
    }

    @Override
    public Future<String> generateReportByMovie(Long id) {
        return executorService.submit(() -> {
            Message request = new Message("generateReportByMovie", String.valueOf(id));
            return tcpClient.sendAndReceive(request).getBody();
        });
    }

}
