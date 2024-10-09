package ru.shulenin.api.dto.validation.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.shulenin.api.dto.validation.IsGender;

public class IsGenderValidator implements ConstraintValidator<IsGender, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && (value.equals("MALE") || value.equals("FEMALE"));
    }
}