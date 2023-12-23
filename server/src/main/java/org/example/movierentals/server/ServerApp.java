package org.example.movierentals.server;

import org.example.movierentals.common.IClientService;
import org.example.movierentals.common.IRentalService;
import org.example.movierentals.common.Message;
import org.example.movierentals.common.IMovieService;
import org.example.movierentals.common.domain.*;
import org.example.movierentals.server.repository.ClientDBRepository;
import org.example.movierentals.server.repository.MovieDBRepository;
import org.example.movierentals.server.repository.RentalDBRepository;
import org.example.movierentals.server.service.SClientServiceImpl;
import org.example.movierentals.server.service.SMovieServiceImpl;
import org.example.movierentals.server.service.SRentalServiceImpl;
import org.example.movierentals.server.tcp.TcpServer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.UnaryOperator;

public class ServerApp {
    public static void main(String[] args) {
        System.out.println("Server is running...");

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        MovieDBRepository movieRepository = new MovieDBRepository();
        ClientDBRepository clientRepository = new ClientDBRepository();
        RentalDBRepository rentalRepository = new RentalDBRepository();

        IMovieService movieService = new SMovieServiceImpl(executorService, movieRepository);
        IClientService clientService = new SClientServiceImpl(executorService, clientRepository);
        IRentalService rentalService = new SRentalServiceImpl(executorService, rentalRepository, movieRepository, clientRepository);

        TcpServer tcpServer = new TcpServer(executorService);

        /**
         * Handle get all Movies from Repository
         */
        UnaryOperator<Message> getAllMoviesHandler = (Message m) -> {
            Future<String> response = movieService.getAllMovies();
            try {
                return new Message("200,OK", response.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return new Message("400,Error: ", e.getMessage());
            }
        };
        tcpServer.addMessageHandler("getAllMovies", getAllMoviesHandler);


        /**
         * Handle add new Movie to Repository
         */
        UnaryOperator<Message> addMovieHandler = (Message m) -> {
            String[] bodyArray = m.getBody().split(",");
            String title = bodyArray[0];
            int year = Integer.parseInt(bodyArray[1]);
            MovieGenres genre = MovieGenres.valueOf(bodyArray[2]);
            AgeRestrictions ageRestriction = AgeRestrictions.valueOf(bodyArray[3]);
            float rentalPrice = Float.parseFloat(bodyArray[4]);
            boolean available = Boolean.parseBoolean(bodyArray[5]);

            Movie movie = new Movie(title, year, genre, ageRestriction, rentalPrice, available);

            Future<String> response = movieService.addMovie(movie);

            try {
                return new Message("200,OK", response.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return new Message("400,Error: ", e.getMessage());
            }
        };
        tcpServer.addMessageHandler("addMovie", addMovieHandler);

        /**
         * Handle get Movie from Repository by ID
         */
        UnaryOperator<Message> getMovieByIdHandler = (Message m) -> {
            Long id = Long.parseLong(m.getBody());
            Future<String> response = movieService.getMovieById(id);
            try {
                return new Message("200,OK", response.get());
            } catch (InterruptedException | ExecutionException e) {
                return new Message("400,Error: ", e.getMessage());
            }
        };
        tcpServer.addMessageHandler("getMovieById", getMovieByIdHandler);


        /**
         * Handle update Movie from Repository
         */
        UnaryOperator<Message> updateMovieHandler = (Message m) -> {
            String[] bodyArray = m.getBody().split(",");
            Long id = Long.parseLong(bodyArray[0]);
            String title = bodyArray[1];
            int year = Integer.parseInt(bodyArray[2]);
            MovieGenres genre = MovieGenres.valueOf(bodyArray[3]);
            AgeRestrictions ageRestriction = AgeRestrictions.valueOf(bodyArray[4]);
            float rentalPrice = Float.parseFloat(bodyArray[5]);
            boolean available = Boolean.parseBoolean(bodyArray[6]);

            Movie movie = new Movie(title, year, genre, ageRestriction, rentalPrice, available);
            movie.setId(id);

            Future<String> response = movieService.updateMovie(movie);

            try {
                return new Message("200,OK", response.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return new Message("400,Error: ", e.getMessage());
            }
        };
        tcpServer.addMessageHandler("updateMovie", updateMovieHandler);


        /**
         * Handle delete Movie from Repository by ID
         */
        UnaryOperator<Message> deleteMovieByIdHandler = (Message m) -> {
            Long id = Long.parseLong(m.getBody());
            Future<String> response = movieService.deleteMovieById(id);
            try {
                return new Message("200,OK", response.get());
            } catch (InterruptedException | ExecutionException e) {
                return new Message("400,Error: ", e.getMessage());
            }
        };
        tcpServer.addMessageHandler("deleteMovieById", deleteMovieByIdHandler);


        /**
         * Handle filter Movies by keyword
         */
        UnaryOperator<Message> filterMoviesByKeywordHandler = (Message m) -> {
            String keyword = m.getBody();
            Future<String> response = movieService.filterMoviesByKeyword(keyword);
            try {
                return new Message("200,OK", response.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return new Message("400,Error: ", e.getMessage());
            }
        };
        tcpServer.addMessageHandler("filterMoviesByKeyword", filterMoviesByKeywordHandler);


        /**
         * Handle get all Clients from Repository
         */
        UnaryOperator<Message> getAllClientsHandler = (Message m) -> {
            Future<String> response = clientService.getAllClients();
            try {
                return new Message("200,OK", response.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return new Message("400,Error: ", e.getMessage());
            }
        };
        tcpServer.addMessageHandler("getAllClients", getAllClientsHandler);


        /**
         * Handle add new Client in Repository
         */
        UnaryOperator<Message> addClientHandler = (Message m) -> {
            String[] clientString = m.getBody().split(",");
            String firstName = clientString[0];
            String lastName = clientString[1];
            String dateOfBirth = clientString[2];
            String email = clientString[3];
            boolean subscribe = Boolean.parseBoolean(clientString[4]);

            Client client = new Client(firstName, lastName, dateOfBirth, email, subscribe);

            Future<String> response = clientService.addClient(client);
            try {
                return new Message("200,OK", response.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return new Message("400,Error", e.getMessage());
            }
        };
        tcpServer.addMessageHandler("addClient", addClientHandler);


        /**
         * Handle get Client by ID
         */
        UnaryOperator<Message> getClientByIdHandler = (Message m) -> {
            Long id = Long.parseLong(m.getBody());
            Future<String> response = clientService.getClientById(id);
            try {
                return new Message("200,OK", response.get());
            } catch (InterruptedException | ExecutionException e) {
                return new Message("400,Error", e.getMessage());
            }
        };
        tcpServer.addMessageHandler("getClientById", getClientByIdHandler);


        /**
         * Handle update Client
         */
        UnaryOperator<Message> updateClientHandler = (Message m) -> {
            String[] clientString = m.getBody().split(",");
            Long clientId = Long.parseLong(clientString[0]);
            String firstName = clientString[1];
            String lastName = clientString[2];
            String dateOfBirth = clientString[3];
            String email = clientString[4];
            boolean subscribe = Boolean.parseBoolean(clientString[5]);

            Client client = new Client(firstName, lastName, dateOfBirth, email, subscribe);
            client.setId(clientId);

            Future<String> response = clientService.updateClient(client);
            try {
                return new Message("200,OK", response.get());
            } catch (InterruptedException | ExecutionException e) {
                return new Message("400,Error", e.getMessage());
            }
        };
        tcpServer.addMessageHandler("updateClient", updateClientHandler);


        /**
         * Handle Delete Client by ID
         */
        UnaryOperator<Message> deleteClientByIdHandler = (Message m) -> {
            Long id = Long.parseLong(m.getBody());
            Future<String> response = clientService.deleteClientById(id);
            try {
                return new Message("200,OK", response.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return new Message("400,Error: ", e.getMessage());
            }
        };
        tcpServer.addMessageHandler("deleteClientById", deleteClientByIdHandler);


        /**
         * Handle filter Clients by keyword
         */
        UnaryOperator<Message> filterClientsByKeywordHandler = (Message m) -> {
            String keyword = m.getBody();
            Future<String> response = clientService.filterClientsByKeyword(keyword);
            try {
                return new Message("200,OK", response.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return new Message("400,Error: ", e.getMessage());
            }
        };
        tcpServer.addMessageHandler("filterClientsByKeyword", filterClientsByKeywordHandler);


        /**
         * Handle get a Rental by ID
         */
        UnaryOperator<Message> getRentalByIdHandler = (Message m) -> {
            Long rentalId = Long.parseLong(m.getBody());
            Future<String> response = rentalService.getRentalById(rentalId);
            try {
                return new Message("200,OK", response.get());
            } catch (InterruptedException | ExecutionException e) {
                return new Message("400,Error: ", e.getMessage());
            }
        };
        tcpServer.addMessageHandler("getRentalById", getRentalByIdHandler);

        /**
         * Handle get all Rentals from Repository
         */
        UnaryOperator<Message> getAllRentalsHandler = (Message m) -> {
            Future<String> response = rentalService.getAllRentals();
            try {
                return new Message("200,OK", response.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return new Message("400,Error: ", e.getMessage());
            }
        };
        tcpServer.addMessageHandler("getAllRentals", getAllRentalsHandler);

        /**
         * Handle rent a Movie
         */
        UnaryOperator<Message> rentAMovieHandler = (Message m) -> {
            String[] rentalStringArray = m.getBody().split(",");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");

            Long movieId = Long.parseLong(rentalStringArray[0]);
            Long clientId = Long.parseLong(rentalStringArray[1]);
            float rentalCharge = Float.parseFloat(rentalStringArray[2]);
            LocalDateTime rentalDate = LocalDateTime.parse(rentalStringArray[3], formatter);
            LocalDateTime dueDate = LocalDateTime.parse(rentalStringArray[4], formatter);

            Rental rental = new Rental(movieId, clientId, rentalCharge, rentalDate, dueDate);

            Future<String> response = rentalService.rentAMovie(rental);
            try {
                return new Message("200,OK", response.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return new Message("400,Error: ", e.getMessage());
            }
        };
        tcpServer.addMessageHandler("rentAMovie", rentAMovieHandler);

        /**
         * Handle update a Rent Transaction
         */
        UnaryOperator<Message> updateRentalTransactionHandler = (Message m) -> {
            String[] rentalStringArray = m.getBody().split(",");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");

            Long rentalId = Long.parseLong(rentalStringArray[0]);
            Long movieId = Long.parseLong(rentalStringArray[1]);
            Long clientId = Long.parseLong(rentalStringArray[2]);
            float rentalCharge = Float.parseFloat(rentalStringArray[3]);
            LocalDateTime rentalDate = LocalDateTime.parse(rentalStringArray[4], formatter);
            LocalDateTime dueDate = LocalDateTime.parse(rentalStringArray[5], formatter);

            Rental rental = new Rental(movieId, clientId, rentalCharge, rentalDate, dueDate);
            rental.setId(rentalId);

            Future<String> response = rentalService.updateRentalTransaction(rental);
            try {
                System.out.println(response.get());
                return new Message("200,OK", response.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return new Message("400,Error: ", e.getMessage());
            }
        };
        tcpServer.addMessageHandler("updateRentalTransaction", updateRentalTransactionHandler);

        /**
         * Handle Delete a Rental Transaction by ID
         */
        UnaryOperator<Message> deleteMovieRentalHandler = (Message m) -> {
            Long id = Long.parseLong(m.getBody());
            Future<String> response = rentalService.deleteMovieRental(id);
            try {
                return new Message("200,OK", response.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return new Message("400,Error: ", e.getMessage());
            }
        };
        tcpServer.addMessageHandler("deleteMovieRental", deleteMovieRentalHandler);

        /**
         * Handle generate Movies by rent counter Report
         */
        UnaryOperator<Message> moviesByRentNumberHandler = (Message m) -> {
            Future<String> response = rentalService.moviesByRentNumber();
            try {
                return new Message("200,OK", response.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return new Message("400,Error: ", e.getMessage());
            }
        };
        tcpServer.addMessageHandler("moviesByRentNumber", moviesByRentNumberHandler);

        /**
         * Handle generate Clients by rent counter Report
         */
        UnaryOperator<Message> clientsByRentNumberHandler = (Message m) -> {
            Future<String> response = rentalService.clientsByRentNumber();
            try {
                return new Message("200,OK", response.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return new Message("400,Error: ", e.getMessage());
            }
        };
        tcpServer.addMessageHandler("clientsByRentNumber", clientsByRentNumberHandler);

        /**
         * Handle generate Report by Client ID
         */
        UnaryOperator<Message> generateReportByClientHandler = (Message m) -> {
            Long id = Long.parseLong(m.getBody());
            Future<String> response = rentalService.generateReportByClient(id);
            try {
                return new Message("200,OK", response.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return new Message("400,Error: ", e.getMessage());
            }
        };
        tcpServer.addMessageHandler("generateReportByClient", generateReportByClientHandler);

        /**
         * Handle generate Report by Movie ID
         */
        UnaryOperator<Message> generateReportByMovieHandler = (Message m) -> {
            Long id = Long.parseLong(m.getBody());
            Future<String> response = rentalService.generateReportByMovie(id);
            try {
                return new Message("200,OK", response.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return new Message("400,Error: ", e.getMessage());
            }
        };
        tcpServer.addMessageHandler("generateReportByMovie", generateReportByMovieHandler);

        tcpServer.startServer();
        executorService.shutdown();
    }
}