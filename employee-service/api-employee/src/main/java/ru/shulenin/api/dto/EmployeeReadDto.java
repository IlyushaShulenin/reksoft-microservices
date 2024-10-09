package ru.shulenin.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeReadDto {
    Long id;
    String surname;
    String name;
    String phoneNumber;
    LocalDate birthday;
    String departmentName;
    String position;
    Integer payment;
}