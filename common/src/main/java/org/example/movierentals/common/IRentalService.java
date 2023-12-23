package org.example.movierentals.common;

import org.example.movierentals.common.domain.Rental;

import java.util.concurrent.Future;

public interface IRentalService {

    Future<String> getAllRentals();

    Future<String> getRentalById(Long id);

    Future<String> rentAMovie(Rental rental);

    Future<String> updateRentalTransaction(Rental rental);

    Future<String> deleteMovieRental(Long rentalId);

    Future<String> moviesByRentNumber();

    Future<String> clientsByRentNumber();

    Future<String> generateReportByClient(Long id);

    Future<String> generateReportByMovie(Long id);

}
