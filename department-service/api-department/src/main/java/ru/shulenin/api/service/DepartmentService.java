package ru.shulenin.api.service;

import ru.shulenin.api.dto.DepartmentEditDto;
import ru.shulenin.api.dto.DepartmentReadDto;
import ru.shulenin.api.dto.DepartmentSaveDto;
import ru.shulenin.api.dto.DepartmentMessageDto;
import ru.shulenin.api.dto.EmployeeShortInfoDto;
import ru.shulenin.api.dto.CommonPaymentForDepartment;

import java.util.List;

public interface DepartmentService {
    DepartmentReadDto findDepartmentById(Long departmentId);
    DepartmentReadDto findDepartmentByName(String name);
    CommonPaymentForDepartment getPaymentForDepartment(Long departmentId);
    DepartmentReadDto saveDepartment(DepartmentSaveDto departmentSaveDto);
    DepartmentReadDto deleteDepartment(Long departmentId);
    DepartmentReadDto updateDepartment(DepartmentEditDto departmentEditDto, Long departmentId);
    List<EmployeeShortInfoDto> findAllEmployeesInDepartment(Long departmentId);
    DepartmentMessageDto findDepartmentShortInfo(Long departmentId);
    void updatePaymentInfo();
}