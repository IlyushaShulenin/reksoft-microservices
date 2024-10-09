package ru.shulenin.api.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.shulenin.api.dto.validation.IsAdult;
import ru.shulenin.api.dto.validation.IsGender;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSaveDto {
    @NotBlank(message = "Surname can't be nullable or empty")
    String surname;

    @NotBlank(message = "Name can't be nullable or empty")
    String name;

    @NotBlank(message = "Surname can't be nullable or empty")
    String fatherName;

    @NotBlank(message = "Gender can't be nullable or empty")
    @IsGender(message = "Gender must be MALE or FEMALE")
    String gender;

    @NotNull
    @Past
    @IsAdult(message = "New employee is too young")
    LocalDate birthday;

    @NotBlank
    @Pattern(
            regexp = "^\\+7 \\(\\d{3}\\) \\d{3}-\\d{2}-\\d{2}$",
            message = "It is not phone number"
    )
    String phoneNumber;

    @NotNull(message = "Department id can't be nullable")
    @Positive(message = "Department id must be positive")
    Long departmentId;

    @NotBlank
    String position;

    @NotNull(message = "Payment must be declared")
    @Min(value = 19_242, message = "Payment is less than minimum")
    Integer payment;
}