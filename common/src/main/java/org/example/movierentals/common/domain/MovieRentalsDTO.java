package org.example.movierentals.common.domain;

public class MovieRentalsDTO {
    Movie movie;

    int rentCounter;

    public MovieRentalsDTO(Movie movie, int rentCounter) {
        this.movie = movie;
        this.rentCounter = rentCounter;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public int getRentCounter() {
        return rentCounter;
    }

    public void setRentCounter(int rentCounter) {
        this.rentCounter = rentCounter;
    }

    @Override
    public String toString() {
        return "MovieRentalsDTO{" +
                "movie=" + movie +
                ", rentCounter=" + rentCounter +
                '}';
    }
}
