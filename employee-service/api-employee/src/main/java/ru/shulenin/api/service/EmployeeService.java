package ru.shulenin.api.service;

import ru.shulenin.api.dto.EmployeeEditDto;
import ru.shulenin.api.dto.EmployeeReadDto;
import ru.shulenin.api.dto.EmployeeSaveDto;
import ru.shulenin.api.dto.EmployeeShortInfoDto;

import java.util.List;

public interface EmployeeService {
    EmployeeReadDto getEmployeeInfo(Long employeeId);
    EmployeeReadDto hireEmployee(EmployeeSaveDto employeeSaveDto);
    EmployeeReadDto deleteEmployee(Long employeeId);
    EmployeeReadDto updateEmployee(EmployeeEditDto employeeEditDto, Long employeeId);
    Integer findEmployeesCountInDepartment(Long departmentId);
    Integer getCommonPaymentForDepartment(Long departmentId);
    EmployeeShortInfoDto getLeaderShortInfo(Long departmentId);
    List<EmployeeShortInfoDto> getEmployeesInDepartment(Long departmentId);
}
