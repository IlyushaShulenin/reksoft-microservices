package ru.shulenin.impl.unit.controller;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ru.shulenin.api.dto.DepartmentEditDto;
import ru.shulenin.api.dto.DepartmentReadDto;
import ru.shulenin.api.dto.DepartmentSaveDto;
import ru.shulenin.api.service.DepartmentService;
import ru.shulenin.impl.controller.DepartmentRestControllerImpl;
import ru.shulenin.impl.exception.DepartmentStillHasEmployeesException;
import ru.shulenin.impl.exception.UniqueAttributeAlreadyExistException;
import ru.shulenin.impl.unit.mock.service.MockDepartmentServiceCreator;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class DepartmentRestControllerTest {
    private final MockDepartmentServiceCreator mockDepartmentServiceCreator = new MockDepartmentServiceCreator();

    private final DepartmentService departmentService = mockDepartmentServiceCreator.getDepartmentService();

    private final DepartmentRestControllerImpl departmentRestController = new DepartmentRestControllerImpl(departmentService);

    @Test
    void findDepartmentById() {
        var departmentFoundByController = departmentRestController.findDepartmentById(1L);
        var departmentFoundByService = departmentService.findDepartmentById(1L);

        assertThat(departmentFoundByController.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(departmentFoundByController.getBody()).isEqualTo(departmentFoundByService);
    }

    @Test
    void findUnknownDepartmentId() {
        assertThatThrownBy(() -> departmentRestController.findDepartmentById(100L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findDepartmentByName() {
        var departmentFoundByController = departmentRestController.findDepartmentByName("Department1");
        var departmentFoundByService = departmentService.findDepartmentByName("Department1");

        assertThat(departmentFoundByController.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(departmentFoundByController.getBody()).isEqualTo(departmentFoundByService);
    }

    @Test
    void findUnknownDepartmentByName() {
        assertThatThrownBy(() -> departmentRestController.findDepartmentByName("Unknown department"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void saveDepartment() {
        var departmentSaveDto = getSaveDtoThatIsNotMain();
        var departmentSavedByController = departmentRestController.createDepartment(departmentSaveDto);
        var departmentFoundByService = departmentService.findDepartmentByName(departmentSaveDto.getName());

        assertThat(departmentSavedByController.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(departmentSavedByController.getBody()).isEqualTo(departmentFoundByService);
    }

    @Test
    void saveExistentDepartment() {
        assertThatThrownBy(() -> departmentRestController.createDepartment(getExistingDto()))
                .isInstanceOf(UniqueAttributeAlreadyExistException.class);
    }

    @Test
    void saveDepartmentWithUnknownParentDepartment() {
        assertThatThrownBy(() -> departmentRestController.createDepartment(getSaveDtoWithUnknownParentId()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteDepartment() {
        var departmentDeletedByController = departmentRestController.deleteDepartment(3L);

        assertThat(departmentDeletedByController.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThatThrownBy(() -> departmentService.findDepartmentById(3L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteDepartmentWithIllegalId() {
        assertThatThrownBy(() -> departmentRestController.deleteDepartment(-1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteUnRegisteredDepartment() {
        assertThatThrownBy(() -> departmentRestController.deleteDepartment(100L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteNotEmptyDepartment() {
        assertThatThrownBy(() -> departmentRestController.deleteDepartment(1L))
                .isInstanceOf(DepartmentStillHasEmployeesException.class);
    }

    @Test
    void updateDepartment() {
        var updatedDepartment = departmentRestController.updateDepartment(2L, getDepartmentEditDto());
        assertThat(updatedDepartment.getStatusCode()).isEqualTo(HttpStatus.OK);

        var updatedDepartmentFoundByService = departmentService.findDepartmentById(2L);
        assertThat(updatedDepartment.getBody()).isEqualTo(updatedDepartmentFoundByService);
    }

    @Test
    void updateUnknownDepartment() {
        assertThatThrownBy(() -> departmentRestController.updateDepartment(100L, getDepartmentEditDto()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    private DepartmentReadDto getReadDto() {
        return new DepartmentReadDto(
                1L,
                "Department",
                LocalDate.now(),
                "Leader",
                1
        );
    }

    private DepartmentSaveDto getSaveDtoThatIsNotMain() {
        return new DepartmentSaveDto(
                "New Department",
                2L
        );
    }

    private DepartmentSaveDto getSaveDtoWithNullableName() {
        return new DepartmentSaveDto(
                "",
                2L
        );
    }

    private DepartmentSaveDto getExistingDto() {
        return new DepartmentSaveDto(
                "Department1",
                2L
        );
    }

    private DepartmentSaveDto getSaveDtoWithUnknownParentId() {
        return new DepartmentSaveDto(
                "New Department",
                -2L
        );
    }

    private DepartmentEditDto getDepartmentEditDto() {
        return new DepartmentEditDto("_test");
    }
}
