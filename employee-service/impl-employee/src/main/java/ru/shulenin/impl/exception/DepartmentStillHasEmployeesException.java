package ru.shulenin.impl.exception;

public class DepartmentStillHasEmployeesException extends RuntimeException {
    public DepartmentStillHasEmployeesException() {
    }

    public DepartmentStillHasEmployeesException(String message) {
        super(message);
    }
}
