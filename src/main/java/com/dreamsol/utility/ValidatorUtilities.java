package com.dreamsol.utility;

import javax.validation.ConstraintViolation;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.stereotype.Component;

@Component
public class ValidatorUtilities {
    private Validator validator;

    public <T> Set<String> validateDtoMessages(T dto) {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        Set<String> failedValidations = violations.stream().map((violation) -> {
            return violation.getMessage() + "for : " + violation.getInvalidValue();
        }).collect(Collectors.toSet());

        return failedValidations;
    }

    public <T> boolean validateDto(T dto) {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (violations.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }
}
