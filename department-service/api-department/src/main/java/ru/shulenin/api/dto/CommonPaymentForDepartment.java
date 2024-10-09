package ru.shulenin.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonPaymentForDepartment {
    String departmentName;
    Integer commonPayment;
}