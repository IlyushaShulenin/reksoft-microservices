package ru.shulenin.impl.unit.controller;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.shulenin.api.controller.EmployeeRestController;
import ru.shulenin.api.dto.EmployeeEditDto;
import ru.shulenin.api.dto.EmployeeSaveDto;
import ru.shulenin.api.service.EmployeeService;
import ru.shulenin.impl.controller.EmployeeRestControllerImpl;
import ru.shulenin.impl.unit.mock.service.MockEmployeeServiceCreator;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmployeeRestControllerImplTest {
    private final MockEmployeeServiceCreator employeeServiceCreator = new MockEmployeeServiceCreator();
    private final EmployeeService employeeService = employeeServiceCreator.getMockEmployeeService();

    private final EmployeeRestController employeeRestController = new EmployeeRestControllerImpl(employeeService);

    @Test
    void getEmployee() {
        var employeeFoundByService = employeeService.getEmployeeInfo(1L);
        var employeeFoundByController = employeeRestController.getEmployee(employeeFoundByService.getId());

        assertThat(employeeFoundByController.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(employeeFoundByController.getBody()).isEqualTo(employeeFoundByService);
    }

    @Test
    void getUnregisteredEmployee() {
        assertThatThrownBy(() -> employeeService.getEmployeeInfo(100L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void saveEmployee() {
        var saveDto = getEmployeeSaveDto();
        var savedEmployee = employeeRestController.saveEmployee(saveDto);
        var newEmployee = employeeService.getEmployeeInfo(savedEmployee.getBody().getId());

        assertThat(savedEmployee.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(savedEmployee.getBody()).isEqualTo(newEmployee);
    }

    @Test
    void saveEmployeeFromUnregisteredEmployee() {
        var saveDto = getEmployeeFromUnregisteredDepartment();
        assertThatThrownBy(() -> employeeRestController.saveEmployee(saveDto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteEmployee() {
        var deletedEmployee = employeeRestController.deleteEmployee(1L);
        assertThat(deletedEmployee.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThatThrownBy(() -> employeeService.getEmployeeInfo(deletedEmployee.getBody().getId()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteUnregisteredEmployee() {
        assertThatThrownBy(() -> employeeRestController.deleteEmployee(100L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void updateEmployee() {
        var editDto = getEmployeeEditDto(1L);

        var updatedEmployee = employeeRestController.updateEmployee(1L, editDto);
        assertThat(updatedEmployee.getStatusCode()).isEqualTo(HttpStatus.OK);

        var updatedEmployeeFromService = employeeService.getEmployeeInfo(1L);
        assertThat(updatedEmployee.getBody()).isEqualTo(updatedEmployeeFromService);
    }

    @Test
    void updateUnregisteredEmployee() {
        assertThatThrownBy(() -> employeeRestController.updateEmployee(100L, getEmployeeEditDto(1L)))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void updatedEmployeeFromUnregisteredDepartment() {
        assertThatThrownBy(() -> employeeRestController.updateEmployee(1L, getEmployeeEditDto(100L)))
                .isInstanceOf(EntityNotFoundException.class);
    }

    private EmployeeSaveDto getEmployeeSaveDto() {
        return new EmployeeSaveDto(
                "Test",
                "Test",
                "Test",
                "MALE",
                LocalDate.of(1980, 11, 10),
                "+7 (900) 000-00-00",
                1L,
                "Test",
                20000
        );
    }

    private EmployeeEditDto getEmployeeEditDto(Long departmentId) {
        return new EmployeeEditDto(
                "Test",
                "Test",
                "+7 (900) 374-82-47",
                departmentId,
                "Test",
                20000,
                true
        );
    }

    private EmployeeSaveDto getEmployeeFromUnregisteredDepartment() {
        return new EmployeeSaveDto(
                "Test",
                "Test",
                "Test",
                "MALE",
                LocalDate.of(1999, 11, 10),
                "+7 (900) 001-00-00",
                100L,
                "Test",
                20000
        );
    }
}