package org.example.movierentals.server.service;

import org.example.movierentals.common.IRentalService;
import org.example.movierentals.common.domain.*;
import org.example.movierentals.common.domain.exceptions.MovieRentalsException;
import org.example.movierentals.server.repository.ClientDBRepository;
import org.example.movierentals.server.repository.MovieDBRepository;
import org.example.movierentals.server.repository.RentalDBRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

public class SRentalServiceImpl implements IRentalService {
    private RentalDBRepository rentalRepository;
    private ExecutorService executorService;
    private MovieDBRepository movieRepository;
    private ClientDBRepository clientRepository;

    public SRentalServiceImpl(ExecutorService executorService,
                              RentalDBRepository rentalRepository,
                              MovieDBRepository movieRepository,
                              ClientDBRepository clientRepository) {
        this.rentalRepository = rentalRepository;
        this.executorService = executorService;
        this.movieRepository = movieRepository;
        this.clientRepository = clientRepository;
    }


    @Override
    public Future<String> getAllRentals() {
        Iterable<Rental> rentals = rentalRepository.findAll();
        if (StreamSupport.stream(rentals.spliterator(), false).findAny().isPresent()) {
            StringBuilder sb = new StringBuilder();
            for (Rental rental : rentals) {
                sb.append(rental.getId()).append(",")
                        .append(rental.getMovieId()).append(",")
                        .append(rental.getClientId()).append(",")
                        .append(rental.getRentalCharge()).append(",")
                        .append(rental.getRentalDate()).append(",")
                        .append(rental.getDueDate()).append(";");
            }
            return executorService.submit(() -> sb.toString());
        } else {
            return executorService.submit(() -> "400 Error,No Rentals were found in the Database.");
        }
    }

    @Override
    public Future<String> getRentalById(Long id) {
        Optional<Rental> rentalOptional = rentalRepository.findOne(id);
        StringBuilder sb = new StringBuilder();
        if (rentalOptional.isPresent()) {
            Rental rental = rentalOptional.get();
            sb.append(rental.getId()).append(",")
                    .append(rental.getMovieId()).append(",")
                    .append(rental.getClientId()).append(",")
                    .append(rental.getRentalCharge()).append(",")
                    .append(rental.getRentalDate()).append(",")
                    .append(rental.getDueDate());
            return executorService.submit(() -> sb.toString());
        }
        return executorService.submit(() -> "400 Error,Rental not found.");
    }

    @Override
    public Future<String> rentAMovie(Rental rental) {
        Optional<Rental> rentalOptional = rentalRepository.save(rental);
        if (rentalOptional.isPresent()) {
            Rental rentalSaved = rentalOptional.get();
            StringBuilder sb = new StringBuilder();
            sb.append(rentalSaved.getMovieId()).append(",")
                    .append(rentalSaved.getClientId()).append(",")
                    .append(rentalSaved.getRentalCharge()).append(",")
                    .append(rentalSaved.getRentalDate()).append(",")
                    .append(rentalSaved.getDueDate());
            return executorService.submit(() -> sb.toString());
        }
        return executorService.submit(() -> "400 Error,Rental not saved.");
    }

    @Override
    public Future<String> updateRentalTransaction(Rental rental) {
        Optional<Rental> rentalOptional = rentalRepository.update(rental);
        if (rentalOptional.isPresent()) {
            Rental rentalUpdated = rentalOptional.get();
            StringBuilder sb = new StringBuilder();
            sb.append(rentalUpdated.getId()).append(",")
                    .append(rentalUpdated.getMovieId()).append(",")
                    .append(rentalUpdated.getClientId()).append(",")
                    .append(rentalUpdated.getRentalCharge()).append(",")
                    .append(rentalUpdated.getRentalDate()).append(",")
                    .append(rentalUpdated.getDueDate());
            return executorService.submit(() -> sb.toString());
        }
        return executorService.submit(() -> "400 Error,Rental not saved.");
    }

    @Override
    public Future<String> deleteMovieRental(Long rentalId) {
        Optional<Rental> rentalOptional = rentalRepository.delete(rentalId);
        StringBuilder sb = new StringBuilder();
        if (rentalOptional.isPresent()) {
            Rental rentalDeleted = rentalOptional.get();
            sb.append(rentalDeleted.getId()).append(",")
                    .append(rentalDeleted.getMovieId()).append(",")
                    .append(rentalDeleted.getClientId()).append(",")
                    .append(rentalDeleted.getRentalCharge()).append(",")
                    .append(rentalDeleted.getRentalDate()).append(",")
                    .append(rentalDeleted.getDueDate());
            return executorService.submit(() -> sb.toString());
        } else {
            return executorService.submit(() -> "400 Error,Rental not deleted.");
        }
    }

    @Override
    public Future<String> moviesByRentNumber() {
        Map<Long, Integer> mapMovieIdRentCounter = new HashMap<>();
        List<MovieRentalsDTO> moviesByRentCounterDesc = new ArrayList<>();

        Iterable<Rental> rentals = rentalRepository.findAll();
        for (Rental r : rentals) {
            Integer counter = 0;
            for (Rental rental : rentals) {
                if (rental.getMovieId() == r.getMovieId()) {
                    counter++;
                }
            }
            mapMovieIdRentCounter.put(r.getMovieId(), counter);
        }

        mapMovieIdRentCounter.forEach((k, v) -> {
            MovieRentalsDTO movieDTO = new MovieRentalsDTO(movieRepository.findOne(k).get(), v);
            if (moviesByRentCounterDesc.isEmpty()) {
                moviesByRentCounterDesc.add(movieDTO);
            } else {
                boolean flag = false;
                for (MovieRentalsDTO m : moviesByRentCounterDesc) {
                    if (m.getRentCounter() <= movieDTO.getRentCounter()) {
                        moviesByRentCounterDesc.add(moviesByRentCounterDesc.indexOf(m), movieDTO);
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    moviesByRentCounterDesc.add(movieDTO);
                }
            }
        });

        if (StreamSupport.stream(moviesByRentCounterDesc.spliterator(), false).findAny().isPresent()) {
            StringBuilder sb = new StringBuilder();
            for (MovieRentalsDTO movieDTO : moviesByRentCounterDesc) {
                Movie m = movieDTO.getMovie();
                int rentCounter = movieDTO.getRentCounter();
                sb.append(m.getId()).append(",")
                        .append(m.getTitle()).append(",")
                        .append(m.getYear()).append(",")
                        .append(m.getGenre()).append(",")
                        .append(m.getAgeRestrictions()).append(",")
                        .append(m.getRentalPrice()).append(",")
                        .append(m.isAvailable()).append(",")
                        .append(rentCounter).append(";");
            }
            return executorService.submit(() -> sb.toString());
        } else {
            return executorService.submit(() -> "400 Error,No Rentals were found in the Database.");
        }
    }

    @Override
    public Future<String> clientsByRentNumber() {
        Map<Long, Integer> mapClientIdRentCounter = new HashMap<>();
        List<ClientRentalsDTO> clientsByRentCounterDesc = new ArrayList<>();

        Iterable<Rental> rentals = rentalRepository.findAll();
        for (Rental r : rentals) {
            Integer counter = 0;
            for (Rental rental : rentals) {
                if (rental.getClientId() == r.getClientId()) {
                    counter++;
                }
            }
            mapClientIdRentCounter.put(r.getClientId(), counter);
        }

        mapClientIdRentCounter.forEach((k, v) -> {
            ClientRentalsDTO clientRentalsDTO = new ClientRentalsDTO(clientRepository.findOne(k).get(), v);
            if (clientsByRentCounterDesc.isEmpty()) {
                clientsByRentCounterDesc.add(clientRentalsDTO);
            } else {
                boolean flag = false;
                for (ClientRentalsDTO c : clientsByRentCounterDesc) {
                    if (c.getRentCounter() <= clientRentalsDTO.getRentCounter()) {
                        clientsByRentCounterDesc.add(clientsByRentCounterDesc.indexOf(c), clientRentalsDTO);
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    clientsByRentCounterDesc.add(clientRentalsDTO);
                }
            }
        });

        if (StreamSupport.stream(clientsByRentCounterDesc.spliterator(), false).findAny().isPresent()) {
            StringBuilder sb = new StringBuilder();
            for (ClientRentalsDTO clientDTO : clientsByRentCounterDesc) {
                Client c = clientDTO.getClient();
                int rentCounter = clientDTO.getRentCounter();
                sb.append(c.getId()).append(",")
                        .append(c.getFirstName()).append(",")
                        .append(c.getLastName()).append(",")
                        .append(c.getDateOfBirth()).append(",")
                        .append(c.getEmail()).append(",")
                        .append(c.isSubscribe()).append(",")
                        .append(rentCounter).append(";");
            }
            return executorService.submit(() -> sb.toString());
        } else {
            return executorService.submit(() -> "400 Error,No Rentals were found in the Database.");
        }
    }

    @Override
    public Future<String> generateReportByClient(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null. ");
        }

        List<Movie> moviesList = new ArrayList<>();
        List<LocalDateTime> rentDates = new ArrayList<>();
        float totalCharges = 0.00f;
        int counter = 0;

        Client client = clientRepository.findOne(id).get();

        try {
            Predicate<Rental> clientIdFilter = rental -> rental.getClientId() == id;
            rentalRepository.findAll().forEach(rental -> {
                if (clientIdFilter.test(rental)) {
                    moviesList.add(movieRepository.findOne(rental.getMovieId()).get());
                    rentDates.add(rental.getRentalDate());
                }
            });

            for (Rental rental :
                    rentalRepository.findAll()) {
                if (clientIdFilter.test(rental)) {
                    totalCharges += rental.getRentalCharge();
                    counter++;
                }
            }
        } catch (MovieRentalsException e) {
            throw new MovieRentalsException("Rental Service exception: " + e.getMessage());
        }
        ClientRentReportDTO crDTO = new ClientRentReportDTO(client, moviesList, totalCharges, rentDates, counter);

        StringBuilder sb = new StringBuilder();
        if (crDTO != null) {
            //append Client
            sb.append(client.getId()).append(",")
                    .append(client.getFirstName()).append(",")
                    .append(client.getLastName()).append(",")
                    .append(client.getDateOfBirth()).append(",")
                    .append(client.getEmail()).append(",")
                    .append(client.isSubscribe()).append(";");
            //append Movie List
            for (Movie m : moviesList) {
                sb.append(m.getId()).append(":")
                        .append(m.getTitle()).append(":")
                        .append(m.getYear()).append(":")
                        .append(m.getGenre()).append(":")
                        .append(m.getAgeRestrictions()).append(":")
                        .append(m.getRentalPrice()).append(":")
                        .append(m.isAvailable()).append(",");
            }
            sb.append(";");
            //append totalCharges
            sb.append(totalCharges).append(";");
            //append Rental Dates List
            for (LocalDateTime d : rentDates) {
                sb.append(d).append(",");
            }
            sb.append(";");
            //append counter
            sb.append(counter);

            return executorService.submit(() -> sb.toString());
        } else {
            return executorService.submit(() -> "400 Error,No Report for the Client.");
        }
    }

    @Override
    public Future<String> generateReportByMovie(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null. ");
        }

        List<Client> clientsList = new ArrayList<>();
        List<LocalDateTime> rentDates = new ArrayList<>();
        float totalCharges = 0.00f;
        int counter = 0;

        Movie movie = movieRepository.findOne(id).get();

        try {
            Predicate<Rental> movieIdFilter = rental -> rental.getMovieId() == id;
            rentalRepository.findAll().forEach(rental -> {
                if (movieIdFilter.test(rental)) {
                    clientsList.add(clientRepository.findOne(rental.getClientId()).get());
                    rentDates.add(rental.getRentalDate());
                }
            });

            for (Rental rental :
                    rentalRepository.findAll()) {
                if (movieIdFilter.test(rental)) {
                    totalCharges += rental.getRentalCharge();
                    counter++;
                }
            }
        } catch (MovieRentalsException e) {
            throw new MovieRentalsException("Rental Service exception: " + e.getMessage());
        }
        MovieRentReportDTO mrDTO = new MovieRentReportDTO(movie, clientsList, totalCharges, rentDates, counter);

        StringBuilder sb = new StringBuilder();
        if (mrDTO != null) {
            //append Movie
            sb.append(movie.getId()).append(",")
                    .append(movie.getTitle()).append(",")
                    .append(movie.getYear()).append(",")
                    .append(movie.getGenre()).append(",")
                    .append(movie.getAgeRestrictions()).append(",")
                    .append(movie.getRentalPrice()).append(",")
                    .append(movie.isAvailable()).append(";");
            //append Movie List
            for (Client c : clientsList) {
                sb.append(c.getId()).append(":")
                        .append(c.getFirstName()).append(":")
                        .append(c.getLastName()).append(":")
                        .append(c.getDateOfBirth()).append(":")
                        .append(c.getEmail()).append(":")
                        .append(c.isSubscribe()).append(",");
            }
            sb.append(";");
            //append totalCharges
            sb.append(totalCharges).append(";");
            //append Rental Dates List
            for (LocalDateTime d : rentDates) {
                sb.append(d).append(",");
            }
            sb.append(";");
            //append counter
            sb.append(counter);

            System.out.println(sb);
            return executorService.submit(() -> sb.toString());
        } else {
            return executorService.submit(() -> "400 Error,No Report for the Movie.");
        }
    }

}