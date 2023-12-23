package org.example.movierentals.server.repository;

import org.example.movierentals.common.domain.AgeRestrictions;
import org.example.movierentals.common.domain.Movie;
import org.example.movierentals.common.domain.MovieGenres;
import org.example.movierentals.common.domain.exceptions.MovieRentalsException;
import org.example.movierentals.common.domain.validators.MovieValidator;
import org.example.movierentals.common.domain.validators.Validator;
import org.example.movierentals.common.domain.exceptions.ValidatorException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MovieDBRepository implements Repository<Long, Movie> {
    private ElephantSQLDataSource dataSource = new ElephantSQLDataSource();
    private Validator<Movie> validator = new MovieValidator();


    public MovieDBRepository() {
    }


    @Override
    public Optional<Movie> findOne(Long id) {
        Movie movie = new Movie();
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null.");
        }

        String query = "SELECT * FROM movies WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(query);
        ) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                try {
                    setFieldsOnMovie(resultSet, movie);
                } catch (ValidatorException e){
                    throw new MovieRentalsException(e);
                }
                return Optional.of(movie);
            }
        } catch (SQLException e) {
            throw new MovieRentalsException("Database connection exception. ", e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Movie> findAll() {
        Set<Movie> movies = new HashSet<>();

        String query = "SELECT * FROM movies";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery();
        ) {
            while (resultSet.next()) {
                Movie movie = new Movie();
                try {
                    setFieldsOnMovie(resultSet, movie);
                } catch (ValidatorException e){
                    throw new MovieRentalsException(e);
                }
                movies.add(movie);
            }
        } catch (SQLException e) {
            throw new MovieRentalsException("Database connection exception. ", e);
        }
        return movies;
    }

    private void setFieldsOnMovie(ResultSet resultSet, Movie movie) throws SQLException {
        Long resultId = resultSet.getLong("id");
        movie.setId(resultId);

        String title = resultSet.getString("title");
        movie.setTitle(title);

        int year = resultSet.getInt("year");
        movie.setYear(year);

        MovieGenres genre = MovieGenres.valueOf(resultSet.getString("genre").toUpperCase());
        movie.setGenre(genre);

        AgeRestrictions ageRestriction = AgeRestrictions.valueOf(resultSet.getString("age_restriction").toUpperCase());
        movie.setAgeRestrictions(ageRestriction);

        float rentalPrice = resultSet.getFloat("rental_price");
        movie.setRentalPrice(rentalPrice);

        boolean available = resultSet.getBoolean("available");
        movie.setAvailable(available);

        try {
            validator.validate(movie);
        } catch (ValidatorException e) {
            throw new ValidatorException("Database corrupted. There are not valid movies. ", e);
        }
    }


    @Override
    public Optional<Movie> save(Movie movie) {
        if (movie == null) {
            throw new IllegalArgumentException("Movie must not be null.");
        }

        try {
            validator.validate(movie);
        } catch (ValidatorException e){
            throw new ValidatorException(e);
        }

        String sqlString = "INSERT INTO movies (" +
                "title, year, genre, age_restriction, rental_price, available) " +
                "values (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(sqlString)) {
            statement.setString(1, movie.getTitle());
            statement.setInt(2, movie.getYear());
            statement.setString(3, String.valueOf(movie.getGenre()));
            statement.setString(4, String.valueOf(movie.getAgeRestrictions()));
            statement.setFloat(5, movie.getRentalPrice());
            statement.setBoolean(6, movie.isAvailable());
            statement.executeUpdate();
            return Optional.of(movie);
        } catch (SQLException e) {
            throw new MovieRentalsException("Database connection exception. ", e);
        }
    }


    @Override
    public Optional<Movie> update(Movie movie) throws ValidatorException {
        if (movie == null) {
            throw new IllegalArgumentException("Movie must not be null.");
        }
        String sqlString = "UPDATE movies " +
                "SET title = ?, year = ?, genre = ?, age_restriction = ?, " +
                "rental_price = ?, available = ? " +
                "WHERE id = ?";
        validator.validate(movie);
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(sqlString);
        ) {
            statement.setString(1, movie.getTitle());
            statement.setInt(2, movie.getYear());
            statement.setString(3, String.valueOf(movie.getGenre()));
            statement.setString(4, String.valueOf(movie.getAgeRestrictions()));
            statement.setFloat(5, movie.getRentalPrice());
            statement.setBoolean(6, movie.isAvailable());
            statement.setLong(7, movie.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new MovieRentalsException("Database connection exception. ", e);
        }
        return Optional.ofNullable(movie);
    }


    @Override
    public Optional<Movie> delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null.");
        }
        Optional<Movie> movieToDelete = findOne(id);

        if (movieToDelete.isPresent()) {
            String sqlString = "DELETE FROM movies WHERE id = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sqlString);
            ) {
                statement.setLong(1, id);
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new MovieRentalsException("Database connection exception. ", e);
            }
        }
        return movieToDelete;
    }
}
