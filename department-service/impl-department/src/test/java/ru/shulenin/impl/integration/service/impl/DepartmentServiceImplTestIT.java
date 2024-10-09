package ru.shulenin.impl.integration.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import ru.shulenin.api.dto.DepartmentEditDto;
import ru.shulenin.api.dto.DepartmentSaveDto;
import ru.shulenin.api.service.DepartmentService;
import ru.shulenin.impl.client.EmployeeClient;
import ru.shulenin.impl.exception.DepartmentStillHasEmployeesException;
import ru.shulenin.impl.exception.UniqueAttributeAlreadyExistException;
import ru.shulenin.impl.integration.TestBase;
import ru.shulenin.impl.integration.annotation.IntegrationTest;
import ru.shulenin.impl.mapper.DepartmentMapper;
import ru.shulenin.impl.repository.DepartmentRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@IntegrationTest
@RequiredArgsConstructor
class DepartmentServiceImplTestIT extends TestBase {
    private final DepartmentService departmentService;
    private final DepartmentRepository departmentRepository;
    private final EmployeeClient employeeClient;

    private final DepartmentMapper departmentMapper = DepartmentMapper.INSTANCE;

    @Test
    @Transactional
    public void findDepartmentById() {
        var department = departmentService.findDepartmentById(1L);
        var departmentFromRepository = departmentRepository.findById(department.getId()).get();
        var mappedDepartment = departmentMapper.mapEntityToRead(departmentFromRepository, "test", 1);
        assertThat(department).isEqualTo(mappedDepartment);
    }

    @Test
    public void findUnknownDepartmentById() {
        assertThatThrownBy(() -> departmentService.findDepartmentById(-1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @Transactional
    public void findDepartmentByName() {
        var department = departmentService.findDepartmentByName("Main department");
        var departmentFromRepository = departmentRepository.findByName(department.getName()).get();
        var mappedDepartment = departmentMapper.mapEntityToRead(departmentFromRepository, "test", 1);
        assertThat(department).isEqualTo(mappedDepartment);
    }

    @Test
    public void findUnknownDepartmentByName() {
        assertThatThrownBy(() -> departmentService.findDepartmentByName("Unknown department"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    public void getPaymentForDepartment() {
        var payment = departmentService.getPaymentForDepartment(1L);
        var paymentFromClient = employeeClient.getCommonPaymentForDepartment(1L).getBody();
        assertThat(payment.getCommonPayment()).isEqualTo(paymentFromClient);
    }

    @Test
    public void getPaymentForUnknownDepartment() {
        assertThatThrownBy(() -> departmentService.getPaymentForDepartment(-1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @Transactional
    void saveDepartment() {
        var saveDto = getDepartmentSaveDto();
        var savedDepartment = departmentService.saveDepartment(saveDto);
        var department = departmentRepository.findById(savedDepartment.getId());

        assertThat(department).isPresent();
        assertThat(department.get().getName()).isEqualTo(savedDepartment.getName());

        var parentId = saveDto.getParentDepartmentId().get();
        var parentDepartment = departmentRepository.findById(parentId).get();
        var childDepartments = parentDepartment.getChildDepartment();


        assertThat(childDepartments).contains(department.get());
    }

    @Test
    @Transactional
    void saveNewMainDepartment() {
        var saveDto = getMainDepartmentSaveDto();
        var savedDepartment = departmentService.saveDepartment(saveDto);
        var department = departmentRepository.findById(savedDepartment.getId());

        var departmentEntity = department.get();

        assertThat(department).isPresent();
        assertThat(departmentEntity.getName()).isEqualTo(savedDepartment.getName());

        var currentMainDepartmentName = departmentRepository.findMainDepartment().getName();
        assertThat(departmentEntity.getName()).isEqualTo(currentMainDepartmentName);
    }

    @Test
    void saveDepartmentWithExistingName() {
        var saveDto = getExistingDepartmentSaveDto();
        assertThatThrownBy(() -> departmentService.saveDepartment(saveDto))
                .isInstanceOf(UniqueAttributeAlreadyExistException.class);
    }

    @Test
    @Transactional
    void deleteMainDepartment() {
        var saveDto = getMainDepartmentSaveDto();
        departmentService.saveDepartment(saveDto);

        var mainDepartmentBeforeDeleting = departmentRepository.findMainDepartment();
        departmentService.deleteDepartment(mainDepartmentBeforeDeleting.getId());
        var mainDepartmentAfterDeleting = departmentRepository.findMainDepartment();

        var department = departmentRepository.findById(mainDepartmentBeforeDeleting.getId());

        assertThat(department).isEmpty();
        assertThat(mainDepartmentAfterDeleting.getIsMain()).isTrue();
    }

    @Test
    @Transactional
    void deleteDepartment() {
        var saveDto = getDepartmentSaveDto();
        var savedDepartment = departmentService.saveDepartment(saveDto);
        departmentService.deleteDepartment(savedDepartment.getId());
        var departmentEntityAfterDeleting = departmentRepository.findById(savedDepartment.getId());
        assertThat(departmentEntityAfterDeleting).isEmpty();
    }

    @Test
    public void findDepartmentShortInfo() {
        var department = departmentService.findDepartmentById(1L);
        var departmentFromRepository = departmentRepository.findById(department.getId()).get();
        var mappedDepartment = departmentMapper.mapEntityToMessageDto(departmentFromRepository);
        assertThat(mappedDepartment.getId()).isEqualTo(department.getId());
        assertThat(mappedDepartment.getName()).isEqualTo(department.getName());
    }

    @Test
    public void findUnknownDepartmentShortInfo() {
        assertThatThrownBy(() -> departmentService.findDepartmentShortInfo(-1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @Transactional
    void updateDepartment() {
        var editDto = getDepartmentEditDto();
        var departmentBeforeUpdate = departmentRepository.findById(2L).get();
        var updatedDepartment = departmentService.updateDepartment(editDto, 2L);
        var departmentAfterUpdate = departmentRepository.findById(updatedDepartment.getId()).get();

        assertThat(departmentAfterUpdate.getName()).isEqualTo(updatedDepartment.getName());
        assertThat(departmentAfterUpdate.getId()).isEqualTo(departmentBeforeUpdate.getId());
        assertThat(departmentAfterUpdate.getCreatedAt()).isEqualTo(departmentBeforeUpdate.getCreatedAt());
        assertThat(departmentAfterUpdate.getChildDepartment()).isEqualTo(departmentBeforeUpdate.getChildDepartment());
    }

    @Test
    void updateDepartmentWithExistingName() {
        var editDto = getDepartmentEditDtoWithExistingName();
        assertThatThrownBy(() -> departmentService.updateDepartment(editDto, 2L))
                .isInstanceOf(UniqueAttributeAlreadyExistException.class);
    }

    @Test
    void deleteDepartmentWithEmployees() {
        assertThatThrownBy(() -> departmentService.deleteDepartment(1L))
                .isInstanceOf(DepartmentStillHasEmployeesException.class);
    }

    @Test
    void deleteDepartmentWithWrongId() {
        assertThatThrownBy(() -> departmentService.deleteDepartment(-1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findAllEmployeesInDepartment() {
        var employees = departmentService.findAllEmployeesInDepartment(2L);
        var employeesFromClient = employeeClient.getAllEmployees(2L).getBody();
        assertThat(employees).hasSize(employeesFromClient.size());
    }

    @Test
    void findAllEmployeesInUnregisteredDepartment() {
        assertThatThrownBy(() -> departmentService.findAllEmployeesInDepartment(-1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    private DepartmentSaveDto getDepartmentSaveDto() {
        return new DepartmentSaveDto("Test", 2L);
    }

    private DepartmentSaveDto getMainDepartmentSaveDto() {
        return new DepartmentSaveDto("Test");
    }

    private DepartmentSaveDto getExistingDepartmentSaveDto() {
        return new DepartmentSaveDto("Sales Department");
    }

    private DepartmentEditDto getDepartmentEditDto() {
        return new DepartmentEditDto("Test_");
    }

    private DepartmentEditDto getDepartmentEditDtoWithExistingName() {
        return new DepartmentEditDto("Accounting");
    }
}