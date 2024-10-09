package ru.shulenin.impl.unit.controller;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.shulenin.api.controller.CommutatorWithEmployeeServiceRestController;
import ru.shulenin.api.service.DepartmentService;
import ru.shulenin.impl.controller.CommutatorWithEmployeeServiceRestControllerImpl;
import ru.shulenin.impl.unit.mock.service.MockDepartmentServiceCreator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommutatorWithEmployeeServiceRestControllerImplTest {
    private final MockDepartmentServiceCreator mockDepartmentServiceCreator = new MockDepartmentServiceCreator();

    private final DepartmentService departmentService = mockDepartmentServiceCreator.getDepartmentService();

    private final CommutatorWithEmployeeServiceRestController commutatorWithEmployeeServiceRestController =
            new CommutatorWithEmployeeServiceRestControllerImpl(departmentService);

    @Test
    void getDepartmentShortInfo() {
        var departmentShortInfo = commutatorWithEmployeeServiceRestController.getDepartmentShortInfo(1L);
        var departmentFoundByService = departmentService.findDepartmentShortInfo(1L);

        assertThat(departmentShortInfo.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(departmentShortInfo.getBody()).isEqualTo(departmentFoundByService);
    }

    @Test
    void findUnknownDepartment() {
        assertThatThrownBy(() -> commutatorWithEmployeeServiceRestController.getDepartmentShortInfo(100L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}