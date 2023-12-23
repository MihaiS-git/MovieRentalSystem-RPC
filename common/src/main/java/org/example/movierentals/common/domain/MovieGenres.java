package org.example.movierentals.common.domain;

/**
 * @author mihai
 */

public enum MovieGenres {
    ACTION("Action"),
    COMEDY("Comedy"),
    DRAMA("Drama"),
    FANTASY("Fantasy"),
    HORROR("Horror"),
    MYSTERY("Mystery"),
    ROMANCE("Romance"),
    THRILLER("Thriller"),
    UNKNOWN("Unknown"),
    WESTERN("Western");

    private final String description;

    MovieGenres(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
