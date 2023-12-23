package org.example.movierentals.common.domain;

import java.time.LocalDateTime;
import java.util.List;

public class MovieRentReportDTO {
    private Movie movie;
    private List<Client> clientsList;
    private float totalCharges;
    private List<LocalDateTime> rentDates;
    private int counter;

    public MovieRentReportDTO() {
    }

    public MovieRentReportDTO(Movie movie, List<Client> clientsList,
                              float totalCharges, List<LocalDateTime> rentDates, int counter) {
        this.movie = movie;
        this.clientsList = clientsList;
        this.totalCharges = totalCharges;
        this.rentDates = rentDates;
        this.counter = counter;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public List<Client> getClientsList() {
        return clientsList;
    }

    public void setClientsList(List<Client> clientsList) {
        this.clientsList = clientsList;
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
        return "MovieRentReportDTO{" +
                "movie=" + movie +
                ", clientsList=" + clientsList +
                ", totalCharges=" + totalCharges +
                ", rentDates=" + rentDates +
                ", counter=" + counter +
                '}';
    }
}
