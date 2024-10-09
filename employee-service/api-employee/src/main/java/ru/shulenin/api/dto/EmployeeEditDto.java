package ru.shulenin.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Value;

@Value
public class EmployeeEditDto {
    @NotBlank(message = "Surname can't be nullable")
    String surname;

    @NotBlank(message = "Name can't be nullable or empty")
    String name;

    @Pattern(
            regexp = "^\\+7 \\(\\d{3}\\) \\d{3}-\\d{2}-\\d{2}$",
            message = "It is not phone number"
    )
    String phoneNumber;

    @NotNull(message = "Department can't be nullable")
    @Positive(message = "Department must be positive")
    Long departmentId;

    @NotBlank(message = "Position in department can't be nullable")
    String position;

    @Positive(message = "Payment must be positive")
    Integer payment;

    @NotNull(message = "IsLeader must declared")
    Boolean isLeader;
}