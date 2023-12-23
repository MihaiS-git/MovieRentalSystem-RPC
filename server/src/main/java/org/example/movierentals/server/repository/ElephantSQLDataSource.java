package org.example.movierentals.server.repository;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class ElephantSQLDataSource implements DataSource {
    private String HOSTNAME = "cornelius.db.elephantsql.com";
    private String PORT = "5432";
    private String DATABASENAME = "vqiikuda";
    private final String USERNAME = System.getenv("username");
    private final String PASSWORD = System.getenv("password");

    public ElephantSQLDataSource() {
    }

    @Override
    public Connection getConnection() {
        String connectionUrl = "jdbc:postgresql://" + HOSTNAME + ":" + PORT + "/" + DATABASENAME;
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(connectionUrl, USERNAME, PASSWORD);
        } catch (SQLException e){
            System.err.println("Database connection exception. " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) {
        return getConnection();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
