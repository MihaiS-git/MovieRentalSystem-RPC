package org.example.movierentals.common.domain;

/**
 * @author mihai
 */

public enum AgeRestrictions {
    GA("General Audiences"),
    PG("Parental Guidance Suggested"),
    PG13("Parents Strongly Cautioned"),
    R("Restricted"),
    NC17("Adults Only"),
    UNKNOWN("Unknown");

    private final String description;

    AgeRestrictions(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
