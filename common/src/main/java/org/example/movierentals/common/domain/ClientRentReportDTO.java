package org.example.movierentals.common.domain;

import java.time.LocalDateTime;
import java.util.List;

public class ClientRentReportDTO {
    private Client client;
    private List<Movie> moviesList;
    private float totalCharges;
    private List<LocalDateTime> rentDates;
    private int counter;

    public ClientRentReportDTO() {
    }

    public ClientRentReportDTO(Client client, List<Movie> moviesList, float totalCharges, List<LocalDateTime> rentDates, int counter) {
        this.client = client;
        this.moviesList = moviesList;
        this.totalCharges = totalCharges;
        this.rentDates = rentDates;
        this.counter = counter;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Movie> getMoviesList() {
        return moviesList;
    }

    public void setMoviesList(List<Movie> moviesList) {
        this.moviesList = moviesList;
    }

    public float getTotalCharges() {
        return totalCharges;
    }

    public void setTotalCharges(float totalCharges) {
        this.totalCharges = totalCharges;
    }

    public List<LocalDateTime> getRentDates() {
        return rentDates;
    }

    public void setRentDates(List<LocalDateTime> rentDates) {
        this.rentDates = rentDates;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    @Override
    public String toString() {
        return "ClientRentReportDTO{" +
                "client=" + client +
                ", moviesList=" + moviesList +
                ", totalCharges=" + totalCharges +
                ", rentDates=" + rentDates +
                ", counter=" + counter +
                '}';
    }
}
