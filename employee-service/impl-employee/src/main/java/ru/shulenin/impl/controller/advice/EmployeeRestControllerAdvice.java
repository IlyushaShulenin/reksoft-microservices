package ru.shulenin.impl.controller.advice;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.shulenin.impl.exception.DepartmentStillHasEmployeesException;
import ru.shulenin.impl.exception.UniqueAttributeAlreadyExistException;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class EmployeeRestControllerAdvice {

    @ExceptionHandler({ EntityNotFoundException.class })
    public ResponseEntity<String> handleEntityNotFound(Throwable e) {
        return new ResponseEntity<>(e.getMessage(), NOT_FOUND);
    }

    @ExceptionHandler({
            DepartmentStillHasEmployeesException.class,
            UniqueAttributeAlreadyExistException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<String> handleExceptionsPresentedBadRequest(Throwable e) {
        return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class })
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.badRequest().body(errors);
    }
}
