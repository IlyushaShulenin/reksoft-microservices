package ru.shulenin.api.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.shulenin.api.dto.validation.impl.IsGenderValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = IsGenderValidator.class)
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface IsGender {
    String message() default "Invalid gender";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}