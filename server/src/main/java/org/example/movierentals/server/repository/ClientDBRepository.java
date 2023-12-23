package org.example.movierentals.server.repository;

import org.example.movierentals.common.domain.Client;
import org.example.movierentals.common.domain.validators.ClientValidator;
import org.example.movierentals.common.domain.exceptions.MovieRentalsException;
import org.example.movierentals.common.domain.validators.Validator;
import org.example.movierentals.common.domain.exceptions.ValidatorException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ClientDBRepository implements Repository<Long, Client> {
    private ElephantSQLDataSource dataSource = new ElephantSQLDataSource();
    private Validator<Client> validator = new ClientValidator();


    public ClientDBRepository() {
    }

    @Override
    public Optional<Client> findOne(Long id) {
        Client client = new Client();
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }

        String query = "SELECT * FROM clients WHERE id =?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(query);
        ) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                try {
                    setFieldsOnClient(resultSet, client);
                } catch (ValidatorException e) {
                    throw new MovieRentalsException(e);
                }
                return Optional.of(client);
            }
        } catch (SQLException e) {
            throw new MovieRentalsException("Database connection exception. ", e);
        }
        return Optional.empty();
    }

    public Iterable<Client> findAll() {
        Set<Client> clients = new HashSet<>();

        String query = "SELECT * FROM clients";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery();) {
            while (resultSet.next()) {
                Client client = new Client();
                try {
                    setFieldsOnClient(resultSet, client);
                } catch (ValidatorException e) {
                    throw new MovieRentalsException(e);
                }
                clients.add(client);
            }
        } catch (SQLException e) {
            throw new MovieRentalsException("Database connection exception. ", e);
        }
        return clients;
    }

    @Override
    public Optional<Client> save(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Client must not be null");
        }
        try {
            validator.validate(client);
        } catch (ValidatorException e) {
            throw new ValidatorException(e);
        }
        String sqlQuery = "INSERT INTO clients " +
                "(first_name, last_name, date_of_birth, email, subscribe) values " +
                "(?,?,?,?,?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(sqlQuery)) {
            statement.setString(1, client.getFirstName());
            statement.setString(2, client.getLastName());
            statement.setString(3, client.getDateOfBirth());
            statement.setString(4, client.getEmail());
            statement.setBoolean(5, client.isSubscribe());
            statement.executeUpdate();
            return Optional.of(client);
        } catch (SQLException e) {
            throw new MovieRentalsException("Database connection exception ", e);
        }
    }

    @Override
    public Optional<Client> update(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Client must not be null");
        }
        String sqlString = "UPDATE clients " +
                "SET first_name = ?, last_name = ?, date_of_birth = ?, " +
                "email = ?, subscribe = ? WHERE id = ?";
        validator.validate(client);
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement statement = conn.prepareStatement(sqlString);
        ) {
            statement.setString(1, client.getFirstName());
            statement.setString(2, client.getLastName());
            statement.setString(3, client.getDateOfBirth());
            statement.setString(4, client.getEmail());
            statement.setBoolean(5, client.isSubscribe());
            statement.setLong(6, client.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new MovieRentalsException("Database connection exception ", e);
        }

        return Optional.ofNullable(client);
    }

    @Override
    public Optional<Client> delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        Optional<Client> clientToDelete = findOne(id);

        if (clientToDelete.isPresent()) {
            String sqlString = "DELETE FROM clients WHERE id  = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sqlString);
            ) {
                statement.setLong(1, id);
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new MovieRentalsException("Database connection exception ", e);
            }
        }
        return clientToDelete;
    }

    private void setFieldsOnClient(ResultSet resultSet, Client client) throws SQLException {
        Long resultId = resultSet.getLong("id");
        client.setId(resultId);

        String firstName = resultSet.getString("first_name");
        client.setFirstName(firstName);

        String lastName = resultSet.getString("last_name");
        client.setLastName(lastName);

        String dateOfBirth = resultSet.getString("date_of_birth");
        client.setDateOfBirth(dateOfBirth);

        String email = resultSet.getString("email");
        client.setEmail(email);

        Boolean subscribe = resultSet.getBoolean("subscribe");
        client.setSubscribe(subscribe);

        try {
            validator.validate(client);
        } catch (ValidatorException e) {
            throw new ValidatorException("Database corrupted. There are not valid clients. ", e);
        }
    }
}
