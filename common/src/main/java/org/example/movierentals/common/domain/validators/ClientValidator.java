package org.example.movierentals.common.domain.validators;

import org.example.movierentals.common.domain.Client;
import org.example.movierentals.common.domain.exceptions.ValidatorException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientValidator implements Validator<Client> {

    @Override
    public void validate(Client entity) throws ValidatorException {

        validateClientFirstName(entity);
        validateClientLastName(entity);
        validateClientDateOfBirth(entity);
        validateClientEmail(entity);
    }

    private static void validateClientEmail(Client client) throws ValidatorException{
        String email = client.getEmail();
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            throw new ValidatorException("Invalid email address");
        }
    }

    private static void validateClientDateOfBirth(Client client) throws ValidatorException{
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date dob = dateFormat.parse(client.getDateOfBirth());
            Date currentDate = new Date();
            if (dob.after(currentDate)) {
                throw new ValidatorException("Date of birth cannot be in the future.");
            }
        } catch (ParseException e) {
            throw new ValidatorException("Invalid date of birth format.");
        }
    }

    private static void validateClientLastName(Client client) throws ValidatorException{
        if(client.getLastName().isBlank()){
            throw new ValidatorException("Client last name can't be blank!");
        }
    }

    private static void validateClientFirstName(Client client) throws ValidatorException{
        if(client.getFirstName().isBlank()){
            throw new ValidatorException("Client first name can't be blank!");
        }
    }
}
