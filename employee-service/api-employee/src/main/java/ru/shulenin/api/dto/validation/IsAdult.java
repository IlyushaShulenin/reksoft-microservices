package ru.shulenin.api.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.shulenin.api.dto.validation.impl.IsAdultValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = IsAdultValidator.class)
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface IsAdult {
    String message() default "Is younger than 18 years";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}