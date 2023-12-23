package org.example.movierentals.server.repository;

import org.example.movierentals.common.domain.Rental;
import org.example.movierentals.common.domain.exceptions.MovieRentalsException;
import org.example.movierentals.common.domain.validators.RentalValidator;
import org.example.movierentals.common.domain.validators.Validator;
import org.example.movierentals.common.domain.exceptions.ValidatorException;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class RentalDBRepository implements Repository<Long, Rental> {
    private ElephantSQLDataSource dataSource = new ElephantSQLDataSource();
    private Validator<Rental> validator = new RentalValidator();

    @Override
    public Optional<Rental> findOne(Long id) {
        Rental rental = new Rental();
        if (id == null) {
            throw new IllegalArgumentException("Rental ID can not be null");
        }

        String query = "SELECT * FROM rentals WHERE id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setLong(1, id);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                Long resultId = resultSet.getLong("id");
                rental.setId(resultId);

                Long movieId = resultSet.getLong("movie_id");
                rental.setMovieId(movieId);

                Long clientId = resultSet.getLong("client_id");
                rental.setClientId(clientId);

                float rentalCharge = resultSet.getFloat("rental_charge");
                rental.setRentalCharge(rentalCharge);

                Timestamp rentalDate = resultSet.getTimestamp("rental_date");
                rental.setRentalDate(rentalDate.toLocalDateTime());

                Timestamp dueDate = resultSet.getTimestamp("due_date");
                rental.setDueDate(dueDate.toLocalDateTime());

                try {
                    validator.validate(rental);
                    return Optional.of(rental);
                } catch (ValidatorException ve) {
                    throw new MovieRentalsException("Rental transaction is not valid. " + ve.getMessage());
                }
            }
        } catch (SQLException e) {
            throw new MovieRentalsException("Database connexion exception. " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Rental> findAll() {
        Set<Rental> rentals = new HashSet<>();
        String query = "SELECT * FROM rentals";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
        ) {
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Rental rental = new Rental();

                Long resultId = resultSet.getLong("id");
                rental.setId(resultId);

                Long movieId = resultSet.getLong("movie_id");
                rental.setMovieId(movieId);

                Long clientId = resultSet.getLong("client_id");
                rental.setClientId(clientId);

                float rentalCharge = resultSet.getFloat("rental_charge");
                rental.setRentalCharge(rentalCharge);

                Timestamp rentalDate = resultSet.getTimestamp("rental_date");
                rental.setRentalDate(rentalDate.toLocalDateTime());

                Timestamp dueDate = resultSet.getTimestamp("due_date");
                rental.setDueDate(dueDate.toLocalDateTime());

                try {
                    validator.validate(rental);
                    rentals.add(rental);
                } catch (ValidatorException ve) {
                    throw new MovieRentalsException("Rental transaction is not valid. " + ve.getMessage());
                }
            }
        } catch (SQLException e) {
            throw new MovieRentalsException("Database connexion exception. " + e.getMessage());
        }
        return rentals;
    }

    @Override
    public Optional<Rental> save(Rental rental) throws ValidatorException {
        if (rental == null) {
            throw new IllegalArgumentException("Rental transaction must not be null.");
        }
        try {
            validator.validate(rental);
        } catch (ValidatorException e) {
            throw new ValidatorException(e);
        }
        String sqlString = "INSERT INTO rentals (" +
                "movie_id, client_id, rental_charge, rental_date, due_date) " +
                "values (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(sqlString)) {
            statement.setLong(1, rental.getMovieId());
            statement.setLong(2, rental.getClientId());
            statement.setFloat(3, rental.getRentalCharge());
            statement.setDate(4, Date.valueOf(rental.getRentalDate().toLocalDate()));
            statement.setDate(5, Date.valueOf(rental.getDueDate().toLocalDate()));
            statement.executeUpdate();
            return Optional.ofNullable(rental);
        } catch (SQLException e) {
            throw new MovieRentalsException("Database connection exception. ", e);
        }
    }

    @Override
    public Optional<Rental> delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null.");
        }

        try {
            Optional<Rental> rentalOptional = findOne(id);

            String sqlString = "DELETE FROM rentals WHERE id = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sqlString);
            ) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return rentalOptional;
        } catch (IllegalArgumentException | MovieRentalsException e) {
            throw new MovieRentalsException("Repository exception: " + e.getMessage());
        }
    }

    @Override
    public Optional<Rental> update(Rental rental) throws ValidatorException {
        if (rental == null) {
            throw new IllegalArgumentException("Rental transaction must not be null.");
        }

        String sqlString = "UPDATE rentals " +
                "SET movie_id=?, client_id=?, rental_charge=?, rental_date=?, due_date=? " +
                "WHERE id= ?";

        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sqlString);
        ) {
            stmt.setLong(1, rental.getMovieId());
            stmt.setLong(2, rental.getClientId());
            stmt.setFloat(3, rental.getRentalCharge());
            stmt.setDate(4, Date.valueOf(rental.getRentalDate().toLocalDate()));
            stmt.setDate(5, Date.valueOf(rental.getDueDate().toLocalDate()));
            stmt.setLong(6, rental.getId());
            stmt.executeUpdate();
            return Optional.ofNullable(rental);
        } catch (SQLException e) {
            throw new MovieRentalsException("Repository exception: " + e.getMessage());
        }
    }
}
