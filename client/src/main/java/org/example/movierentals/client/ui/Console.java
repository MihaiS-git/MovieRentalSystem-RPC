package org.example.movierentals.client.ui;

import org.example.movierentals.client.service.CClientServiceImpl;
import org.example.movierentals.client.service.CMovieServiceImpl;
import org.example.movierentals.client.service.CRentalServiceImpl;
import org.example.movierentals.common.IClientService;
import org.example.movierentals.common.IMovieService;
import org.example.movierentals.common.IRentalService;
import org.example.movierentals.common.domain.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Console {
    final String ERROR_MESSAGE = "400 Error";

    Scanner scanner = new Scanner(System.in);
    private IMovieService movieService;
    private IClientService clientService;
    private IRentalService rentalService;

    public Console(CMovieServiceImpl movieService, CClientServiceImpl clientService, CRentalServiceImpl rentalService) {
        this.movieService = movieService;
        this.clientService = clientService;
        this.rentalService = rentalService;
    }


    /**
     * Run Console
     */
    public void runConsole() {
        while (true) {
            this.showMenu();
            if (scanner.hasNextInt()) {
                int option = scanner.nextInt();
                switch (option) {
                    case 1:
                        this.runSubMenuMovies();
                        break;
                    case 2:
                        this.runSubMenuClients();
                        break;
                    case 3:
                        this.runSubMenuRentals();
                        break;
                    case 0:
                        return;
                    default:
                        System.err.println("Unsupported command.");
                }
            } else {
                String invalidInput = scanner.next();
                System.err.println("Invalid input. Please enter a valid number.");
            }
        }
    }


    /**
     * Print the General Menu
     */
    public void showMenu() {
        System.out.println();
        System.out.println("MENU");
        System.out.println("=".repeat(50));
        System.out.println("1. Movies Menu");
        System.out.println("2. Clients Menu");
        System.out.println("3. Rent Movie & Reports Menu");
        System.out.println("0. Exit");
        System.out.print("\nEnter your option: ");
    }


    /**
     * Run the Movies submenu.
     */
    private void runSubMenuMovies() {
        while (true) {
            System.out.println();
            System.out.println("MOVIES MENU");
            System.out.println("=".repeat(50));
            System.out.println("1. Add Movie");
            System.out.println("2. Print Movie");
            System.out.println("3. Print All Movies");
            System.out.println("4. Update Movie");
            System.out.println("5. Delete Movie");
            System.out.println("6. Filter Movies by Keyword");
            System.out.println("0. Back");
            System.out.print("\nEnter your option: ");

            if (scanner.hasNextInt()) {
                int option = scanner.nextInt();
                switch (option) {
                    case 1:
                        this.handleAddMovie();
                        break;
                    case 2:
                        this.handlePrintMovie();
                        break;
                    case 3:
                        this.handleGetAllMovies();
                        break;
                    case 4:
                        this.handleUpdateMovie();
                        break;
                    case 5:
                        this.handleDeleteMovieById();
                        break;
                    case 6:
                        this.handleFilterMoviesByKeyword();
                        break;
                    case 0:
                        return;
                    default:
                        System.err.println("Unsupported command.");
                }
            } else {
                String invalidInput = scanner.next();
                System.err.println("Invalid input. Please enter a valid number");
            }
        }
    }


    /**
     * Handle filter Movies by Keyword.
     */
    private void handleFilterMoviesByKeyword() {
        System.out.print("Enter the filter keyword: ");
        String keyword = scanner.next();
        Set<Movie> moviesSet = new HashSet<>();
        try {
            String response = movieService.filterMoviesByKeyword(keyword).get();
            if (response != null) {
                String[] responseArray = response.split(";");
                if (responseArray[0].contains(ERROR_MESSAGE)) {
                    System.err.println("No Movie found for the provided Keyword.");
                } else {
                    for (String r : responseArray) {
                        String[] movieArray = r.split(",");
                        Long movieId = Long.parseLong(movieArray[0]);
                        String title = movieArray[1];
                        int year = Integer.parseInt(movieArray[2]);
                        MovieGenres genre = MovieGenres.valueOf(movieArray[3]);
                        AgeRestrictions ageRestrictions = AgeRestrictions.valueOf(movieArray[4]);
                        float rentalPrice = Float.parseFloat(movieArray[5]);
                        boolean available = Boolean.parseBoolean(movieArray[6]);

                        Movie movie = new Movie(title, year, genre, ageRestrictions, rentalPrice, available);
                        movie.setId(movieId);

                        moviesSet.add(movie);
                    }
                    moviesSet.forEach(System.out::println);
                }
            } else {
                System.out.println("No Movie found.");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    /**
     * Handle delete Movie by ID
     */
    private void handleDeleteMovieById() {
        System.out.print("Enter the ID of the Movie: ");
        Long id = null;
        while (id == null) {
            if (scanner.hasNextLong()) {
                id = scanner.nextLong();
            }
        }
        try {
            String response = movieService.deleteMovieById(id).get();
            if (response != null) {
                String[] responseArray = response.split(",");
                if (responseArray[0].contains(ERROR_MESSAGE)) {
                    System.err.println(responseArray[1]);
                } else {
                    Long movieId = Long.parseLong(responseArray[0]);
                    String title = responseArray[1];
                    int year = Integer.parseInt(responseArray[2]);
                    MovieGenres genre = MovieGenres.valueOf(responseArray[3]);
                    AgeRestrictions ageRestrictions = AgeRestrictions.valueOf(responseArray[4]);
                    float rentalPrice = Float.parseFloat(responseArray[5]);
                    boolean available = Boolean.parseBoolean(responseArray[6]);

                    Movie deletedMovie = new Movie(title, year, genre, ageRestrictions, rentalPrice, available);
                    deletedMovie.setId(movieId);

                    System.out.println("Movie was successfully deleted.");
                    System.out.println(deletedMovie);
                }
            } else {
                System.err.println("Movie was not deleted.");
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Handle update Movie
     */
    private void handleUpdateMovie() {
        System.out.print("Enter the ID of the Movie: ");
        Long id = null;
        while (id == null) {
            if (scanner.hasNextLong()) {
                id = scanner.nextLong();
            }
        }
        try {
            Movie movie = readMovie();
            movie.setId(id);
            String verifyExistence = movieService.getMovieById(id).get();
            if (verifyExistence != null) {
                String[] existenceArray = verifyExistence.split(",");
                if (existenceArray[0].contains(ERROR_MESSAGE)) {
                    System.err.println(existenceArray[1]);
                } else {
                    String response = movieService.updateMovie(movie).get();
                    if (response != null) {
                        String[] responseArray = response.split(",");
                        Long movieId = Long.parseLong(responseArray[0]);
                        String title = responseArray[1];
                        int year = Integer.parseInt(responseArray[2]);
                        MovieGenres genre = MovieGenres.valueOf(responseArray[3]);
                        AgeRestrictions ageRestrictions = AgeRestrictions.valueOf(responseArray[4]);
                        float rentalPrice = Float.parseFloat(responseArray[5]);
                        boolean available = Boolean.parseBoolean(responseArray[6]);

                        Movie updatedMovie = new Movie(title, year, genre, ageRestrictions, rentalPrice, available);
                        updatedMovie.setId(movieId);

                        System.out.println("Movie updated successfully.");
                        System.out.println(updatedMovie);
                    } else {
                        System.out.println("Movie not updated.");
                    }
                }
            } else {
                System.out.println("Couldn't verify if the Movie exists in the Database.");
            }
        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Handle print Movie by ID.
     */
    private void handlePrintMovie() {
        System.out.print("Enter the ID of the Movie: ");
        Long id = null;
        while (id == null) {
            if (scanner.hasNextLong()) {
                id = scanner.nextLong();
            }
        }
        try {
            String response = movieService.getMovieById(id).get();
            if (response != null) {
                String[] responseArray = response.split(",");
                if (responseArray[0].contains(ERROR_MESSAGE)) {
                    System.err.println(responseArray[1]);
                } else {
                    long movieId = Long.parseLong(responseArray[0]);
                    String title = responseArray[1];
                    int year = Integer.parseInt(responseArray[2]);
                    MovieGenres genre = MovieGenres.valueOf(responseArray[3]);
                    AgeRestrictions ageRestriction = AgeRestrictions.valueOf(responseArray[4]);
                    float rentalPrice = Float.parseFloat(responseArray[5]);
                    boolean available = Boolean.parseBoolean(responseArray[6]);

                    Movie movie = new Movie(title, year, genre, ageRestriction, rentalPrice, available);
                    movie.setId(movieId);

                    System.out.println(movie);
                }
            } else {
                System.out.println("Couldn't get the Movie.");
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Handle Add Movie feature.
     */
    private void handleAddMovie() {
        try {
            Movie movie = readMovie();
            String response = movieService.addMovie(movie).get();
            if (response != null) {
                System.out.println(response);
                String[] responseArray = response.split(",");
                if (responseArray[0].contains(ERROR_MESSAGE)) {
                    System.err.println(responseArray[1]);
                } else {
                    String title = responseArray[1];
                    int year = Integer.parseInt(responseArray[2]);
                    MovieGenres genre = MovieGenres.valueOf(responseArray[3]);
                    AgeRestrictions ageRestriction = AgeRestrictions.valueOf(responseArray[4]);
                    float rentalPrice = Float.parseFloat(responseArray[5]);
                    boolean available = Boolean.parseBoolean(responseArray[6]);

                    Movie savedMovie = new Movie(title, year, genre, ageRestriction, rentalPrice, available);
                    System.out.println("Movie saved successfully.");
                    System.out.println(savedMovie);
                }
            } else {
                System.err.println("Not valid entity.");
            }
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Handle Get All Movies feature.
     */
    private void handleGetAllMovies() {
        List<Movie> moviesList = new ArrayList<>();
        try {
            String response = movieService.getAllMovies().get();
            if (response != null) {
                String[] responseArray = response.split(";");
                if (responseArray[0].split(",")[0].contains(ERROR_MESSAGE)) {
                    System.out.println(responseArray[1]);
                } else {
                    for (int i = 0; i < responseArray.length; i++) {
                        String[] movieArray = responseArray[i].split(",");
                        long movieId = Long.parseLong(movieArray[0]);
                        String title = movieArray[1];
                        int year = Integer.parseInt(movieArray[2]);
                        MovieGenres genre = MovieGenres.valueOf(movieArray[3]);
                        AgeRestrictions ageRestriction = AgeRestrictions.valueOf(movieArray[4]);
                        float rentalPrice = Float.parseFloat(movieArray[5]);
                        boolean available = Boolean.parseBoolean(movieArray[6]);

                        Movie movie = new Movie(title, year, genre, ageRestriction, rentalPrice, available);
                        movie.setId(movieId);
                        moviesList.add(movie);
                    }
                    moviesList.forEach(System.out::println);
                }
            } else {
                System.out.println("No Movies found.");
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Read user entered info for a Movie.
     *
     * @return a Movie entity.
     */
    private Movie readMovie() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        // Read Movie Title
        String title = "";
        while (title.equals("")) {
            System.out.print("Enter the movie title: ");
            title = reader.readLine().trim();
        }

        // Read Movie Year
        int year = 0;
        boolean validYear = false;
        while (!validYear) {
            System.out.print("Enter the year of the movie: ");
            try {
                year = Integer.parseInt(reader.readLine().trim());
                validYear = true;
            } catch (NumberFormatException e) {
                System.err.println("Invalid input. Please enter a valid (int) year. ");
            }
        }

        // Read Movie Genre
        MovieGenres genre = null;
        while (genre == null) {
            System.out.print("Enter the genre of the movie" +
                    "(Action/Comedy/Drama/Fantasy/Horror/Mystery/Romance/Thriller/Western): ");
            String genreInput = reader.readLine().trim();
            try {
                genre = MovieGenres.valueOf(genreInput.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid input. Please enter a valid Movie genre. ");
            }
        }

        // Read Movie AgeRestriction
        AgeRestrictions ageRestriction = null;
        while (ageRestriction == null) {
            System.out.print("Enter the age restrictions of the movie(GA/PG/PG13/R/NC17): ");
            String ageRestrictionInput = reader.readLine().trim();
            try {
                ageRestriction = AgeRestrictions.valueOf(ageRestrictionInput.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid input. Please enter a valid Movie age restriction.");
            }
        }

        // Read Movie price for rent.
        float rentalPrice = 0.0f;
        boolean validRentalPrice = false;
        while (!validRentalPrice) {
            System.out.print("Enter the price for rent of the Movie: ");
            try {
                rentalPrice = Float.parseFloat(reader.readLine().trim());
                validRentalPrice = true;
            } catch (NumberFormatException e) {
                System.err.println("Invalid input. Please enter a valid (Float) price.");
            }
        }

        // Read Movie availability
        boolean available;
        while (true) {
            System.out.print("Is it available for rent?(true/false): ");
            String availableInput = reader.readLine();
            if (availableInput.trim().equalsIgnoreCase("false") ||
                    availableInput.trim().equalsIgnoreCase("true")) {
                available = Boolean.parseBoolean(availableInput);
                break;
            } else {
                System.err.println("Invalid input. Please enter 'true' or 'false'.");
            }
        }

        Movie movie = new Movie(title, year, genre, ageRestriction, rentalPrice, available);
        return movie;
    }


    /**
     * Run Clients SubMenu
     */
    private void runSubMenuClients() {
        while (true) {
            System.out.println();
            System.out.println("CLIENTS MENU");
            System.out.println("=".repeat(50));
            System.out.println("1. Add Client");
            System.out.println("2. Print Client");
            System.out.println("3. Print All Clients");
            System.out.println("4. Update Client");
            System.out.println("5. Delete Client");
            System.out.println("6. Filter Client by Keyword");
            System.out.println("0. Back");
            System.out.print("\nEnter your option: ");

            if (scanner.hasNextInt()) {
                int option = scanner.nextInt();
                switch (option) {
                    case 1:
                        this.handleAddClient();
                        break;
                    case 2:
                        this.handlePrintClient();
                        break;
                    case 3:
                        this.handleGetAllClients();
                        break;
                    case 4:
                        this.handleUpdateClient();
                        break;
                    case 5:
                        this.handleDeleteClientByID();
                        break;
                    case 6:
                        this.handleFilterClientsByKeyword();
                        break;
                    case 0:
                        return;
                    default:
                        System.err.println("Unsupported command.");
                }
            } else {
                String invalidInput = scanner.next();
                System.err.println("Invalid input. Please enter a valid number!");
            }
        }
    }


    /**
     * Handle filter Clients by Keyword.
     */
    private void handleFilterClientsByKeyword() {
        System.out.print("Enter the filter keyword: ");
        String keyword = scanner.next();
        Set<Client> clientsSet = new HashSet<>();
        try {
            String response = clientService.filterClientsByKeyword(keyword).get();
            if (response != null) {
                String[] responseArray = response.split(";");
                if (responseArray[0].contains(ERROR_MESSAGE)) {
                    System.err.println("No Client found for the provided Keyword.");
                } else {
                    for (String r : responseArray) {
                        String[] clientArray = r.split(",");
                        Long clientId = Long.parseLong(clientArray[0]);
                        String firstName = clientArray[1];
                        String lastName = clientArray[2];
                        String dateOfBirth = clientArray[3];
                        String email = clientArray[4];
                        boolean subscribe = Boolean.parseBoolean(clientArray[5]);

                        Client client = new Client(firstName, lastName, dateOfBirth, email, subscribe);
                        client.setId(clientId);

                        clientsSet.add(client);
                    }
                    clientsSet.forEach(System.out::println);
                }
            } else {
                System.out.println("No Client found.");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    /**
     * Handle delete Client by ID.
     */
    private void handleDeleteClientByID() {
        System.out.println("Enter the ID of the Client: ");
        Long id = null;
        while (id == null) {
            if (scanner.hasNextLong()) {
                id = scanner.nextLong();
            }
        }
        try {
            String response = clientService.deleteClientById(id).get();
            if (response != null) {
                String[] responseArray = response.split(",");
                if (responseArray[0].contains(ERROR_MESSAGE)) {
                    System.err.println(responseArray[1]);
                } else {
                    Long clientId = Long.parseLong(responseArray[0]);
                    String firstName = responseArray[1];
                    String lastName = responseArray[2];
                    String dateOfBirth = responseArray[3];
                    String email = responseArray[4];
                    boolean subscribe = Boolean.parseBoolean(responseArray[5]);

                    Client deletedClient = new Client(firstName, lastName, dateOfBirth, email, subscribe);
                    deletedClient.setId(clientId);

                    System.out.println("Client was deleted successfully.");
                    System.out.println(deletedClient);
                }
            } else {
                System.out.println("Client not deleted.");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    /**
     * Handle update Client
     */
    private void handleUpdateClient() {
        System.out.println("Enter the id of the client: ");
        Long id = null;
        while (id == null) {
            if (scanner.hasNextLong()) {
                id = scanner.nextLong();
            }
        }
        try {
            Client client = readClient();
            client.setId(id);
            String verifyExistence = clientService.getClientById(id).get();
            if (verifyExistence != null) {
                String[] existenceArray = verifyExistence.split(",");
                if (existenceArray[0].contains(ERROR_MESSAGE)) {
                    System.err.println(existenceArray[1]);
                } else {
                    String response = clientService.updateClient(client).get();
                    if (response != null) {
                        String[] responseArray = response.split(",");
                        Long clientId = Long.parseLong(responseArray[0]);
                        String firstName = responseArray[1];
                        String lastName = responseArray[2];
                        String dateOfBirth = responseArray[3];
                        String email = responseArray[4];
                        boolean subscribe = Boolean.parseBoolean(responseArray[5]);

                        Client updatedClient = new Client(firstName, lastName, dateOfBirth, email, subscribe);
                        updatedClient.setId(clientId);

                        System.out.println("Client updated successfully.");
                        System.out.println(updatedClient);
                    } else {
                        System.err.println("Not valid entity.");
                    }
                }
            } else {
                System.out.println("Couldn't verify if the Client exists in the Database.");
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Handle print Client by ID
     */
    private void handlePrintClient() {
        System.out.print("Enter the id of the Client: ");
        Long id = null;
        while (id == null) {
            if (scanner.hasNextLong()) {
                id = scanner.nextLong();
            }
        }
        try {
            String response = clientService.getClientById(id).get();
            if (response != null) {
                String[] responseArray = response.split(",");
                if (responseArray[0].contains(ERROR_MESSAGE)) {
                    System.err.println(responseArray[1]);
                } else {
                    Long clientId = Long.parseLong(responseArray[0]);
                    String firstName = responseArray[1];
                    String lastName = responseArray[2];
                    String dateOfBirth = responseArray[3];
                    String email = responseArray[4];
                    boolean subscribe = Boolean.parseBoolean(responseArray[5]);

                    Client client = new Client(firstName, lastName, dateOfBirth, email, subscribe);
                    client.setId(clientId);

                    System.out.println(client);
                }
            } else {
                System.out.println("Couldn't get the Client.");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    /**
     * Handle Add Client Feature
     */
    private void handleAddClient() {
        try {
            Client client = readClient();
            String response = clientService.addClient(client).get();
            if (response != null) {
                String[] responseArray = response.split(",");
                if (responseArray[0].contains(ERROR_MESSAGE)) {
                    System.out.println(responseArray[1]);
                } else {
                    String firstName = responseArray[1];
                    String lastName = responseArray[2];
                    String dateOfBirth = responseArray[3];
                    String email = responseArray[4];
                    boolean subscribe = Boolean.parseBoolean(responseArray[5]);

                    Client savedClient = new Client(firstName, lastName, dateOfBirth, email, subscribe);
                    System.out.println("Client saved successfully.");
                    System.out.println(savedClient);
                }
            } else {
                System.err.println("Not valid entity.");
            }
        } catch (ExecutionException | InterruptedException | IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }


    /**
     * Handle Get All Clients Feature
     */
    private void handleGetAllClients() {
        List<Client> clientsList = new ArrayList<>();
        try {
            String response = clientService.getAllClients().get();
            if (response != null) {
                String[] responseArray = response.split(";");
                if (responseArray[0].split(",")[0].contains(ERROR_MESSAGE)) {
                    System.out.println(responseArray[1]);
                } else {
                    for (int i = 0; i < responseArray.length; i++) {
                        String[] clientsString = responseArray[i].split(",");
                        long clientId = Long.parseLong(clientsString[0]);
                        String firstName = clientsString[1];
                        String lastName = clientsString[2];
                        String dateOfBirth = clientsString[3];
                        String email = clientsString[4];
                        boolean subscribe = Boolean.parseBoolean(clientsString[5]);

                        Client client = new Client(firstName, lastName, dateOfBirth, email, subscribe);
                        client.setId(clientId);

                        clientsList.add(client);
                    }
                    clientsList.forEach(System.out::println);
                }
            } else {
                System.out.println("No Clients found.");
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read client info from Keyboard
     *
     * @return a new Client entity.
     */
    private Client readClient() {

        scanner.nextLine();
        //Read First Name of the Client
        System.out.print("Enter firstName: ");
        String firstName = scanner.nextLine();

        //Read Last Name of the Client
        System.out.print("Enter lastName: ");
        String lastName = scanner.nextLine();

        //Read Date Of Birth of the Client by format yyy-MM-dd
        System.out.print("Enter dateOfBirth:yyyy-MM-dd: ");
        String dateOfBirth = scanner.nextLine();

        //Read the email of the Client
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        //Read Boolean for subscribe of the Client
        System.out.print("Do you want to subscribe?:true/false ");
        Boolean subscribe = scanner.nextBoolean();

        Client client = new Client(firstName, lastName, dateOfBirth, email, subscribe);

        return client;
    }

    /**
     * Run Rentals submenu.
     */
    private void runSubMenuRentals() {
        while (true) {
            System.out.println();
            System.out.println("RENT & REPORTS MENU");
            System.out.println("=".repeat(50));
            System.out.println("1. Print a rent transaction by ID");
            System.out.println("2. Print all rent transactions");
            System.out.println("3. Rent a Movie");
            System.out.println("4. Update a Rent Transaction");
            System.out.println("5. Delete a Rent Transaction");
            System.out.println("6. Print Movies by Rent Counter");
            System.out.println("7. Print Clients by Rent Counter");
            System.out.println("8. Print Client Rent Report by ID");
            System.out.println("9. Print Movie Rent Report by ID");
            System.out.println("0. Back");
            System.out.print("\nEnter your option: ");

            if (scanner.hasNextInt()) {
                int option = scanner.nextInt();
                switch (option) {
                    case 1:
                        this.handlePrintRental();
                        break;
                    case 2:
                        this.handlePrintAllRentals();
                        break;
                    case 3:
                        this.handleRentAMovie();
                        break;
                    case 4:
                        this.handleUpdateRentTransaction();
                        break;
                    case 5:
                        this.handleDeleteRentTransaction();
                        break;
                    case 6:
                        this.handleMoviesByRentals();
                        break;
                    case 7:
                        this.handleClientsByRentedMovies();
                        break;
                    case 8:
                        this.handleClientRentReport();
                        break;
                    case 9:
                        this.handleMovieRentReport();
                        break;
                    case 0:
                        return;
                    default:
                        System.err.println("Unsupported command.");
                }
            } else {
                String invalidInput = scanner.next();
                System.err.println("Invalid input. Please enter a valid number!");
            }
        }
    }


    /**
     * Handle Movie Rent Report by ID
     */
    private void handleMovieRentReport() {
        Long id = null;
        while (id == null) {
            System.out.print("Enter the ID of the Movie: ");
            if (scanner.hasNextLong()) {
                id = scanner.nextLong();
            }
        }

        try {
            String response = rentalService.generateReportByMovie(id).get();
            if (response != null) {
                String[] responseArray = response.split(";");
                if (responseArray[0].split(",")[0].contains(ERROR_MESSAGE)) {
                    System.out.println(responseArray[1]);
                } else {
                    //parse Movie
                    String[] movieArray = responseArray[0].split(",");
                    Long movieId = Long.parseLong(movieArray[0]);
                    String title = movieArray[1];
                    int year = Integer.parseInt(movieArray[2]);
                    MovieGenres genre = MovieGenres.valueOf(movieArray[3]);
                    AgeRestrictions ageRestrictions = AgeRestrictions.valueOf(movieArray[4]);
                    float rentalPrice = Float.parseFloat(movieArray[5]);
                    boolean available = Boolean.parseBoolean(movieArray[6]);

                    Movie movie = new Movie(title, year, genre, ageRestrictions, rentalPrice, available);
                    movie.setId(movieId);

                    //parse ClientList
                    List<Client> clientsList = new ArrayList<>();
                    String[] clientsListArray = responseArray[1].split(",");
                    for(int i=0; i < clientsListArray.length; i++){
                        String[] clientArray = clientsListArray[i].split(":");
                        Long clientId = Long.parseLong(clientArray[0]);
                        String firstName = clientArray[1];
                        String lastName = clientArray[2];
                        String dateOfBirth = clientArray[3];
                        String email = clientArray[4];
                        boolean subscribe = Boolean.parseBoolean(clientArray[5]);

                        Client client = new Client(firstName, lastName, dateOfBirth, email, subscribe);
                        client.setId(clientId);

                        clientsList.add(client);
                    }

                    //parse totalCharges
                    float totalCharges = Float.parseFloat(responseArray[2]);

                    //parse datesList
                    List<LocalDateTime> rentDates = new ArrayList<>();
                    String[] datesListArray = responseArray[3].split(",");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                    for(int i=0; i < datesListArray.length; i++){
                        LocalDateTime theDate = LocalDateTime.parse(datesListArray[i], formatter);
                        rentDates.add(theDate);
                    }

                    //parse counter
                    int counter = Integer.parseInt(responseArray[4]);

                    //crete the report DTO
                    MovieRentReportDTO clientReport = new MovieRentReportDTO(movie, clientsList, totalCharges, rentDates, counter);

                    //print the Report
                    System.out.println("\nMOVIE #" + id + " RENT REPORT");
                    System.out.println("*".repeat(50));
                    System.out.println("Movie information: " + clientReport.getMovie());
                    System.out.println("List of Clients: " + clientReport.getClientsList());
                    System.out.println("Total Charges: $" + clientReport.getTotalCharges());
                    System.out.println("Rent Dates List: " + clientReport.getRentDates());
                    System.out.println("Total number of rents: " + clientReport.getCounter());
                }
            } else {
                System.err.println("Movie not found");
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Handle Client Rent Report by ID
     */
    private void handleClientRentReport() {
        Long id = null;
        while (id == null) {
            System.out.print("Enter the ID of the Client: ");
            if (scanner.hasNextLong()) {
                id = scanner.nextLong();
            }
        }

        try {
            String response = rentalService.generateReportByClient(id).get();
            if (response != null) {
                String[] responseArray = response.split(";");
                if (responseArray[0].split(",")[0].contains(ERROR_MESSAGE)) {
                    System.out.println(responseArray[1]);
                } else {
                    //parse Client
                    String[] clientArray = responseArray[0].split(",");
                    Long clientId = Long.parseLong(clientArray[0]);
                    String firstName = clientArray[1];
                    String lastName = clientArray[2];
                    String dateOfBirth = clientArray[3];
                    String email = clientArray[4];
                    boolean subscribe = Boolean.parseBoolean(clientArray[5]);

                    Client client = new Client(firstName, lastName, dateOfBirth, email, subscribe);
                    client.setId(clientId);

                    //parse MovieList
                    List<Movie> moviesList = new ArrayList<>();
                    String[] movieListArray = responseArray[1].split(",");
                    for(int i=0; i < movieListArray.length; i++){
                        String[] movieArray = movieListArray[i].split(":");
                        Long movieId = Long.parseLong(movieArray[0]);
                        String title = movieArray[1];
                        int year = Integer.parseInt(movieArray[2]);
                        MovieGenres genre = MovieGenres.valueOf(movieArray[3]);
                        AgeRestrictions ageRestrictions = AgeRestrictions.valueOf(movieArray[4]);
                        float rentalPrice = Float.parseFloat(movieArray[5]);
                        boolean available = Boolean.parseBoolean(movieArray[6]);

                        Movie movie = new Movie(title, year, genre, ageRestrictions, rentalPrice, available);
                        movie.setId(movieId);

                        moviesList.add(movie);
                    }

                    //parse totalCharges
                    float totalCharges = Float.parseFloat(responseArray[2]);

                    //parse datesList
                    List<LocalDateTime> rentDates = new ArrayList<>();
                    String[] datesListArray = responseArray[3].split(",");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                    for(int i=0; i < datesListArray.length; i++){
                        LocalDateTime theDate = LocalDateTime.parse(datesListArray[i], formatter);
                        rentDates.add(theDate);
                    }

                    //parse counter
                    int counter = Integer.parseInt(responseArray[4]);

                    //crete the report DTO
                    ClientRentReportDTO clientReport = new ClientRentReportDTO(client, moviesList, totalCharges, rentDates, counter);

                    //print the Report
                    System.out.println("\nCLIENT #" + id + " RENT REPORT");
                    System.out.println("*".repeat(50));
                    System.out.println("Client information: " + clientReport.getClient());
                    System.out.println("List of rented Movies: " + clientReport.getMoviesList());
                    System.out.println("Total Charges: $" + clientReport.getTotalCharges());
                    System.out.println("Rent Dates List: " + clientReport.getRentDates());
                    System.out.println("Total Number of Rents: " + clientReport.getCounter());
                }
            } else {
                System.err.println("Client not found");
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handle generate Clients List by rent counter descending Report
     */
    private void handleClientsByRentedMovies() {
        List<ClientRentalsDTO> clientsList = new ArrayList<>();
        try {
            String response = rentalService.clientsByRentNumber().get();
            if (response != null) {
                String[] responseArray = response.split(";");
                if (responseArray[0].contains(ERROR_MESSAGE)) {
                    System.out.println(responseArray[1]);
                } else {
                    for (int i = 0; i < responseArray.length; i++) {
                        String[] clientArray = responseArray[i].split(",");
                        long clientId = Long.parseLong(clientArray[0]);
                        String firstName = clientArray[1];
                        String lastName = clientArray[2];
                        String dateOfBirth = clientArray[3];
                        String email = clientArray[4];
                        boolean subscribe = Boolean.parseBoolean(clientArray[5]);
                        int rentCounter = Integer.parseInt(clientArray[6]);

                        Client client = new Client(firstName, lastName, dateOfBirth, email, subscribe);
                        client.setId(clientId);

                        ClientRentalsDTO cDTO = new ClientRentalsDTO(client, rentCounter);
                        clientsList.add(cDTO);
                    }
                    clientsList.forEach(System.out::println);
                }
            } else {
                System.out.println("No Clients found.");
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Handle generate Movies List by rent counter descending Report
     */
    private void handleMoviesByRentals() {
        List<MovieRentalsDTO> moviesList = new ArrayList<>();
        try {
            String response = rentalService.moviesByRentNumber().get();
            if (response != null) {
                String[] responseArray = response.split(";");
                if (responseArray[0].contains(ERROR_MESSAGE)) {
                    System.out.println(responseArray[1]);
                } else {
                    for (int i = 0; i < responseArray.length; i++) {
                        String[] movieArray = responseArray[i].split(",");
                        long movieId = Long.parseLong(movieArray[0]);
                        String title = movieArray[1];
                        int year = Integer.parseInt(movieArray[2]);
                        MovieGenres genre = MovieGenres.valueOf(movieArray[3]);
                        AgeRestrictions ageRestriction = AgeRestrictions.valueOf(movieArray[4]);
                        float rentalPrice = Float.parseFloat(movieArray[5]);
                        boolean available = Boolean.parseBoolean(movieArray[6]);
                        int rentCounter = Integer.parseInt(movieArray[7]);

                        Movie movie = new Movie(title, year, genre, ageRestriction, rentalPrice, available);
                        movie.setId(movieId);

                        MovieRentalsDTO mDTO = new MovieRentalsDTO(movie, rentCounter);
                        moviesList.add(mDTO);
                    }
                    moviesList.forEach(System.out::println);
                }
            } else {
                System.out.println("No Movies found.");
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Handle delete Rental transaction by ID
     */
    private void handleDeleteRentTransaction() {
        Long rentalId = 0L;
        boolean validRentalId = false;
        while (!validRentalId) {
            System.out.print("Enter the rental transaction ID: ");
            try {
                rentalId = scanner.nextLong();
                validRentalId = true;
            } catch (NumberFormatException e) {
                System.err.println("Invalid input. Please enter a valid (Long) rental transaction ID. ");
            }
        }
        try {
            String verifyId = rentalService.getRentalById(rentalId).get();
            if (verifyId != null) {
                String[] verifyIdArray = verifyId.split(",");
                if (verifyIdArray[0].contains(ERROR_MESSAGE)) {
                    System.out.println(verifyIdArray[1]);
                } else {
                    String response = rentalService.deleteMovieRental(rentalId).get();
                    if (response != null) {
                        String[] responseArray = response.split(",");
                        if (responseArray[0].contains(ERROR_MESSAGE)) {
                            System.out.println(responseArray[1]);
                        } else {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                            Long rentId = Long.parseLong(responseArray[0]);
                            Long movieId = Long.parseLong(responseArray[1]);
                            Long clientId = Long.parseLong(responseArray[2]);
                            float rentalCharge = Float.parseFloat(responseArray[3]);
                            LocalDateTime rentalDate = LocalDateTime.parse(responseArray[4], formatter);
                            LocalDateTime dueDate = LocalDateTime.parse(responseArray[5], formatter);

                            Rental deletedRental = new Rental(movieId, clientId, rentalCharge, rentalDate, dueDate);
                            deletedRental.setId(rentId);

                            System.out.println("Rental successfully deleted.");
                            System.out.println(deletedRental);
                        }
                    } else {
                        System.err.println("Rental not deleted.");
                    }
                }
            } else {
                System.err.println("Rental not deleted.");
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Handle update Rent Transaction
     */
    private void handleUpdateRentTransaction() {
        Long id = null;
        while (id == null) {
            System.out.print("Enter the ID of the Rent Transaction: ");
            if (scanner.hasNextLong()) {
                id = scanner.nextLong();
            }
        }
        try {
            String verifyIdResponse = rentalService.getRentalById(id).get();
            if (verifyIdResponse != null) {
                String[] verifyIdResponseArray = verifyIdResponse.split(",");
                if (verifyIdResponseArray[0].contains(ERROR_MESSAGE)) {
                    System.out.println("Rental not found.");
                } else {
                    Rental rental = readRentTransaction();
                    if (rental != null) {
                        rental.setId(id);
                        String response = rentalService.updateRentalTransaction(rental).get();
                        if (response != null) {
                            String[] responseArray = response.split(",");
                            if (responseArray[0].contains(ERROR_MESSAGE)) {
                                System.out.println("Could not update Rental.");
                            } else {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");
                                Long rentalId = Long.parseLong(responseArray[0]);
                                Long movieId = Long.parseLong(responseArray[1]);
                                Long clientId = Long.parseLong(responseArray[2]);
                                float rentalCharge = Float.parseFloat(responseArray[3]);
                                LocalDateTime rentalDate = LocalDateTime.parse(responseArray[4], formatter);
                                LocalDateTime dueDate = LocalDateTime.parse(responseArray[5], formatter);

                                Rental updatedRental = new Rental(movieId, clientId, rentalCharge, rentalDate, dueDate);
                                updatedRental.setId(rentalId);

                                System.out.println("Rental updated successfully.");
                                System.out.println(updatedRental);
                            }
                        }
                    } else {
                        System.out.println("Non valid request for Rent a Movie.");
                    }
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Handle rent a Movie.
     */
    private void handleRentAMovie() {
        try {
            Rental rental = readRentTransaction();
            if (rental != null) {
                String response = rentalService.rentAMovie(rental).get();
                if (response != null) {
                    String[] responseArray = response.split(",");
                    if (responseArray[0].contains(ERROR_MESSAGE)) {
                        System.out.println("Could not save Rental in the Database.");
                    } else {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");

                        Long movieId = Long.parseLong(responseArray[0]);
                        Long clientId = Long.parseLong(responseArray[1]);
                        float rentalCharge = Float.parseFloat(responseArray[2]);
                        LocalDateTime rentalDate = LocalDateTime.parse(responseArray[3], formatter);
                        LocalDateTime dueDate = LocalDateTime.parse(responseArray[4], formatter);

                        Rental savedRental = new Rental(movieId, clientId, rentalCharge, rentalDate, dueDate);

                        System.out.println("Rental saved successfully.");
                        System.out.println(savedRental);
                    }
                }
            } else {
                System.out.println("Non valid request for Rent a Movie.");
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Read a Rent transaction from the Keyboard
     *
     * @return a new Rental entity.
     */
    private Rental readRentTransaction() throws ExecutionException, InterruptedException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Rental rental = null;

        // Read Movie ID
        Long movieId = 0L;
        boolean validMovieId = false;
        while (!validMovieId) {
            System.out.print("Enter the movie ID: ");
            try {
                movieId = Long.parseLong(reader.readLine().trim());
                validMovieId = true;
            } catch (IOException e) {
                System.err.println("Read exception: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.err.println("Invalid input. Please enter a valid (Long) movie ID. ");
            }
        }

        String movieResponse = movieService.getMovieById(movieId).get();

        if (movieResponse != null && !movieResponse.split(",")[0].contains(ERROR_MESSAGE)) {
            // Read Client ID
            Long clientId = 0L;
            boolean validClientId = false;
            while (!validClientId) {
                System.out.print("Enter the client ID: ");
                try {
                    clientId = Long.parseLong(reader.readLine().trim());
                    validClientId = true;
                } catch (IOException e) {
                    System.err.println("Read exception: " + e.getMessage());
                } catch (NumberFormatException e) {
                    System.err.println("Invalid input. Please enter a valid (Long) client ID. ");
                }
            }

            String clientResponse = clientService.getClientById(clientId).get();

            if (clientResponse != null && !clientResponse.split(",")[0].contains(ERROR_MESSAGE)) {
                // Rental Charge
                String[] movieResponseArray = movieResponse.split(",");
                float rentalCharge = Float.parseFloat(movieResponseArray[5]);

                // RentalDate
                LocalDateTime rentalDate = LocalDateTime.now();

                // DueDate
                LocalDateTime dueDate = rentalDate.plusDays(1);


                rental = new Rental(movieId, clientId, rentalCharge, rentalDate, dueDate);
            } else {
                System.err.println("No Client with ID " + clientId + " found in the Database.");
            }
        } else {
            System.err.println("No Movie with ID " + movieId + " found in the Database.");
        }
        return rental;
    }


    /**
     * Handle print a Rental by ID
     */
    private void handlePrintRental() {
        System.out.print("Enter the ID of the Rental transaction: ");
        Long id = null;
        while (id == null) {
            if (scanner.hasNextLong()) {
                id = scanner.nextLong();
            }
        }
        try {
            String response = rentalService.getRentalById(id).get();
            if (response != null) {
                String[] rentalString = response.split(",");
                if (rentalString[0].contains(ERROR_MESSAGE)) {
                    System.err.println("Rental with ID " + id + " not found");
                } else {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

                    Long rentalId = Long.parseLong(rentalString[0]);
                    Long movieId = Long.parseLong(rentalString[1]);
                    Long clientId = Long.parseLong(rentalString[2]);
                    float rentalCharge = Float.parseFloat(rentalString[3]);
                    LocalDateTime rentalDate = LocalDateTime.parse(rentalString[4], formatter);
                    LocalDateTime dueDate = LocalDateTime.parse(rentalString[5], formatter);

                    Rental rental = new Rental(movieId, clientId, rentalCharge, rentalDate, dueDate);
                    rental.setId(rentalId);

                    System.out.println(rental);
                }
            } else {
                System.out.println("Couldn't get the Rental from the Database.");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    /**
     * Handle print all Rentals from Repository
     */
    private void handlePrintAllRentals() {
        List<Rental> rentalsList = new ArrayList<>();
        try {
            String response = rentalService.getAllRentals().get();
            if (response != null) {
                String[] responseArray = response.split(";");
                if (responseArray[0].split(",")[0].contains(ERROR_MESSAGE)) {
                    System.out.println(responseArray[1]);
                } else {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                    for (int i = 0; i < responseArray.length; i++) {
                        String[] rentalString = responseArray[i].split(",");
                        Long rentalId = Long.parseLong(rentalString[0]);
                        Long movieId = Long.parseLong(rentalString[1]);
                        Long clientId = Long.parseLong(rentalString[2]);
                        float rentalCharge = Float.parseFloat(rentalString[3]);
                        LocalDateTime rentalDate = LocalDateTime.parse(rentalString[4], formatter);
                        LocalDateTime dueDate = LocalDateTime.parse(rentalString[5], formatter);

                        Rental rental = new Rental(movieId, clientId, rentalCharge, rentalDate, dueDate);
                        rental.setId(rentalId);
                        rentalsList.add(rental);
                    }
                    rentalsList.forEach(System.out::println);
                }
            } else {
                System.out.println("No Rentals found.");
            }
        } catch (InterruptedException | ExecutionException | DateTimeParseException e) {
            e.printStackTrace();
        }
    }
}