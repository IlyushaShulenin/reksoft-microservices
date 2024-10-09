package ru.shulenin.impl.exception;

public class UniqueAttributeAlreadyExistException extends RuntimeException {
    public UniqueAttributeAlreadyExistException(String message) {
        super(message);
    }

    public UniqueAttributeAlreadyExistException() {
    }
}
