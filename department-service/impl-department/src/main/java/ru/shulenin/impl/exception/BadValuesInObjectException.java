package ru.shulenin.impl.exception;

public class BadValuesInObjectException extends RuntimeException {
    public BadValuesInObjectException() {
    }

    public BadValuesInObjectException(String message) {
        super(message);
    }
}