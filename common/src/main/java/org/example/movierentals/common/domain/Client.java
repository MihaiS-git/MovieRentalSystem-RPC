package org.example.movierentals.common.domain;
import java.util.Objects;

/**
 * @author Cristina
 */
public class Client extends BaseEntity<Long>{

    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String email;
    private boolean subscribe;

    public Client() {
    }

    public Client(String firstName, String lastName, String dateOfBirth, String email, boolean subscribe) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.subscribe = subscribe;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isSubscribe() {
        return subscribe;
    }

    public void setSubscribe(boolean subscribe) {
        this.subscribe = subscribe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return subscribe == client.subscribe && Objects.equals(firstName, client.firstName) && Objects.equals(lastName, client.lastName) && Objects.equals(dateOfBirth, client.dateOfBirth) && Objects.equals(email, client.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, dateOfBirth, email, subscribe);
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + this.getId() + ", " +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", email='" + email + '\'' +
                ", subscribe=" + subscribe +
                '}';
    }
}
