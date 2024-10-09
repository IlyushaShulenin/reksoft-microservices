package ru.shulenin.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentReadDto {
    Long id;
    String name;
    LocalDate createdAt;
    String leaderSurname;
    Integer employeesNumber;
}