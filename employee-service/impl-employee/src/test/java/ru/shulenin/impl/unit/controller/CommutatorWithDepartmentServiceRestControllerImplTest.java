package ru.shulenin.impl.unit.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.shulenin.api.controller.CommunicatorWithDepartmentServiceRestController;
import ru.shulenin.api.service.EmployeeService;
import ru.shulenin.impl.controller.CommutatorWithDepartmentServiceRestControllerImpl;
import ru.shulenin.impl.unit.mock.service.MockEmployeeServiceCreator;

import static org.assertj.core.api.Assertions.assertThat;

class CommutatorWithDepartmentServiceRestControllerImplTest {
    private final MockEmployeeServiceCreator mockEmployeeServiceCreator = new MockEmployeeServiceCreator();
    private final EmployeeService employeeService = mockEmployeeServiceCreator.getMockEmployeeService();
    private final CommunicatorWithDepartmentServiceRestController controller =
            new CommutatorWithDepartmentServiceRestControllerImpl(employeeService);

    @Test
    void getAllEmployees() {
        var employees = controller.getAllEmployees(1L);
        var employeesFromService = employeeService.getEmployeesInDepartment(1L);

        assertThat(employees.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(employees.getBody()).isEqualTo(employeesFromService);
    }

    @Test
    void countEmployeesInDepartment() {
        var countEmployees = controller.countEmployeesInDepartment(1L);
        var countEmployeesFromService = employeeService.findEmployeesCountInDepartment(1L);

        assertThat(countEmployees.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(countEmployees.getBody()).isEqualTo(countEmployeesFromService);
    }

    @Test
    void getCommonPaymentForDepartment() {
        var commonPaymentForDepartment = controller.getCommonPaymentForDepartment(1L);
        var commonPaymentForDepartmentFromService = employeeService.getCommonPaymentForDepartment(1L);

        assertThat(commonPaymentForDepartment.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(commonPaymentForDepartment.getBody()).isEqualTo(commonPaymentForDepartmentFromService);
    }

    @Test
    void getLeaderInDepartment() {
        var leaderInDepartment = controller.getLeaderInDepartment(1L);
        var leaderFoundByService = employeeService.getLeaderShortInfo(1L);

        assertThat(leaderInDepartment.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(leaderInDepartment.getBody()).isEqualTo(leaderFoundByService);
    }
}