package com.aaharrakshak.company;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FssaiLicenceNumberValidator implements ConstraintValidator<FssaiLicenceNumber, String> {

    private static final String FSSAI_FORMAT = "\\d{14}";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches(FSSAI_FORMAT);
    }
}
