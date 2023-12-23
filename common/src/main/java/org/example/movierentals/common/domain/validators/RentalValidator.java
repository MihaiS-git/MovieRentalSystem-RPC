package org.example.movierentals.common.domain.validators;

import org.example.movierentals.common.domain.Rental;
import org.example.movierentals.common.domain.exceptions.ValidatorException;

public class RentalValidator implements Validator<Rental>{

    @Override
    public void validate(Rental entity) {
        validateMovieId(entity);
        validateClientId(entity);
        validateRentalCharge(entity);
        validateDueDate(entity);
    }

    private void validateDueDate(Rental entity) {
        if(entity.getDueDate().minusDays(1).isAfter(entity.getRentalDate())){
            throw new ValidatorException("Difference between Rental Date and Due Date can be of 1 single Day.");
        }
    }

    private void validateRentalCharge(Rental entity) {
        if((Float) entity.getRentalCharge() == null || entity.getRentalCharge() <= 0.00){
            throw new ValidatorException("Rental charge is not valid. ");
        }
    }

    private void validateClientId(Rental entity) {
        if(entity.getClientId() == null){
            throw new ValidatorException("Client ID can not be null. ");
        }
    }

    private void validateMovieId(Rental entity) {
        if(entity.getMovieId() == null){
            throw new ValidatorException("Movie ID can not be null. ");
        }
    }
}
