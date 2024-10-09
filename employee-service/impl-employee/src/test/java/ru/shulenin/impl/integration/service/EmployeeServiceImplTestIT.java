package ru.shulenin.impl.integration.service;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import ru.shulenin.api.dto.EmployeeEditDto;
import ru.shulenin.api.dto.EmployeeSaveDto;
import ru.shulenin.api.dto.EmployeeShortInfoDto;
import ru.shulenin.api.service.EmployeeService;
import ru.shulenin.impl.entity.EmployeeEntity;
import ru.shulenin.impl.exception.BadValuesInObjectException;
import ru.shulenin.impl.exception.UniqueAttributeAlreadyExistException;
import ru.shulenin.impl.integration.TestBase;
import ru.shulenin.impl.integration.annotation.IntegrationTest;
import ru.shulenin.impl.mapper.EmployeeMapper;
import ru.shulenin.impl.repository.EmployeeRepository;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@IntegrationTest
@RequiredArgsConstructor
class EmployeeServiceImplTestIT extends TestBase {
    private final static long DEPARTMENT_ID = 1;

    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper = EmployeeMapper.INSTANCE;

    private final EntityManagerFactory entityManagerFactory;

    @Test
    @Transactional
    void getEmployeeInfo() {
        var employeeInfo = employeeService.getEmployeeInfo(1L);
        var employeeFoundByRepository = employeeRepository.findById(employeeInfo.getId());
        var mappedEmployee = employeeMapper.mapEntityToRead(employeeFoundByRepository.get(),
                employeeInfo.getDepartmentName());

        assertThat(employeeInfo).isEqualTo(mappedEmployee);
    }

    @Test
    void findUnregisteredEmployee() {
        assertThatThrownBy(() -> employeeService.getEmployeeInfo(-1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findQuitedEmployee() {
        assertThatThrownBy(() -> {
            var quitedEmployee = employeeService.deleteEmployee(4L);
            employeeService.getEmployeeInfo(quitedEmployee.getId());
        }).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @Transactional
    void hireEmployee() {
        var saveDto = getSaveDto();
        var savedEmployeeReadDto = employeeService.hireEmployee(saveDto);
        var savedEmployee = employeeRepository.findById(savedEmployeeReadDto.getId());
        assertThat(savedEmployee).isPresent();

        var mappedSavedEmployee = employeeMapper.mapEntityToRead(savedEmployee.get(),
                savedEmployeeReadDto.getDepartmentName());
        assertThat(mappedSavedEmployee).isEqualTo(savedEmployeeReadDto);
    }

    @Test
    void hireEmployeeWithBigPayment() {
        assertThatThrownBy(() -> {
            var saveDto = getSaveDtoPaymentThatIsGreaterThanLeaderHas();
            employeeService.hireEmployee(saveDto);
        }).isInstanceOf(BadValuesInObjectException.class);
    }

    @Test
    void hireEmployeeWithExistingPhoneNumber() {
        assertThatThrownBy(() -> {
            var saveDto = getSaveDtoWithExistingPhoneNumber();
            employeeService.hireEmployee(saveDto);
        }).isInstanceOf(UniqueAttributeAlreadyExistException.class);
    }

    @Test
    @Transactional
    void deleteEmployee() {
        var deletedEmployee = employeeService.deleteEmployee(2L);
        var deletedEmployeeFromRepository = employeeRepository.findById(deletedEmployee.getId());
        assertThat(deletedEmployeeFromRepository).isEmpty();
    }

    @Test
    void deleteUnregisteredEmployee() {
        assertThatThrownBy(() -> {
            employeeService.deleteEmployee(-1L);
        }).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @Transactional
    void updateEmployee() {
        var updateDto = getEditDto();
        var updatedEmployee = employeeService.updateEmployee(updateDto, 1L);
        var updatedEmployeeFromRepository = employeeRepository.getReferenceById(updatedEmployee.getId());
        var mappedEmployee = employeeMapper.mapEntityToRead(updatedEmployeeFromRepository, updatedEmployee.getDepartmentName());
        assertThat(mappedEmployee).isEqualTo(updatedEmployee);
    }

    @Test
    void updateEmployeeWithExistingPhoneNumber() {
        assertThatThrownBy(() -> {
            var updateDto = getEditDtoWithExistingPhoneNumber();
            employeeService.updateEmployee(updateDto, 2L);
        }).isInstanceOf(UniqueAttributeAlreadyExistException.class);
    }

    @Test
    void updateEmployeeWithWrongPayment() {
        assertThatThrownBy(() -> {
            var editDto = getEditDtoWithWrongPayment();
            employeeService.updateEmployee(editDto, 1L);
        }).isInstanceOf(BadValuesInObjectException.class);
    }

    @Test
    void findEmployeesCountInDepartment() {
        var entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        var employeesCount = entityManager.createQuery("select count(e) from EmployeeEntity e " +
                        "where e.departmentId = :departmentId", Long.class)
                .setParameter("departmentId", DEPARTMENT_ID)
                .getSingleResult();
        entityManager.getTransaction().commit();

        var employeesCountFromService = (long) employeeService.findEmployeesCountInDepartment(DEPARTMENT_ID);
        assertThat(employeesCountFromService).isEqualTo(employeesCount);
    }

    @Test
    void getCommonPaymentForDepartment() {
        var entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        var employeesPayment = entityManager.createQuery("select sum(e.payment) from EmployeeEntity e " +
                        "where e.departmentId = :departmentId", Long.class)
                .setParameter("departmentId", DEPARTMENT_ID)
                .getSingleResult();
        entityManager.getTransaction().commit();

        var employeesPaymentFromService = (long) employeeService.getCommonPaymentForDepartment(DEPARTMENT_ID);
        assertThat(employeesPaymentFromService).isEqualTo(employeesPayment);
    }

    @Test
    void getLeaderShortInfo() {
        var entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        var leader = entityManager.createQuery("select e from EmployeeEntity e " +
                        "where e.departmentId = :departmentId and e.isLeader = true", EmployeeEntity.class)
                .setParameter("departmentId", DEPARTMENT_ID)
                .getSingleResult();
        entityManager.getTransaction().commit();

        var leaderFoundByService = employeeService.getLeaderShortInfo(DEPARTMENT_ID);
        assertThat(leaderFoundByService.getSurname()).isEqualTo(leader.getSurname());
        assertThat(leaderFoundByService.getName()).isEqualTo(leader.getName());
    }

    @Test
    void getEmployeesInDepartment() {
        var entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        var employeesInDepartment = entityManager.createQuery("select e from EmployeeEntity e " +
                        "where e.departmentId = :departmentId", EmployeeEntity.class)
                .setParameter("departmentId", DEPARTMENT_ID)
                .getResultList();
        var mappedEmployeesInDepartment = employeesInDepartment.stream()
                        .map(employee -> new EmployeeShortInfoDto(
                                employee.getName(),
                                employee.getSurname()
                        ))
                        .toList();
        entityManager.getTransaction().commit();

        var employeesInDepartmentFoundByService = employeeService.getEmployeesInDepartment(DEPARTMENT_ID);
        assertThat(employeesInDepartmentFoundByService).isEqualTo(mappedEmployeesInDepartment);
    }

    private EmployeeSaveDto getSaveDto() {
        return new EmployeeSaveDto(
                "Testov",
                "Test",
                "Testovich",
                "MALE",
                LocalDate.of(2000, 10, 11),
                "+7 (900) 100-11-12",
                1L,
                "Manager",
                45_000

        );
    }

    private EmployeeSaveDto getSaveDtoPaymentThatIsGreaterThanLeaderHas() {
        return new EmployeeSaveDto(
                "Testov",
                "Test",
                "Testovich",
                "MALE",
                LocalDate.of(2000, 10, 11),
                "+7 (900) 100-11-12",
                1L,
                "Manager",
                1_000_000
        );
    }

    private EmployeeSaveDto getSaveDtoWithExistingPhoneNumber() {
        return new EmployeeSaveDto(
                "Testov",
                "Test",
                "Testovich",
                "MALE",
                LocalDate.of(2000, 10, 11),
                "+7 (930) 364-01-64",
                1L,
                "Manager",
                45_000
        );
    }

    private EmployeeEditDto getEditDto() {
        return new EmployeeEditDto(
                "Testov",
                "Test",
                "+7 (900) 100-11-12",
                1L,
                "Manager",
                45_000,
                true
        );
    }

    private EmployeeEditDto getEditDtoWithExistingPhoneNumber() {
        return new EmployeeEditDto(
                "Testov",
                "Test",
                "+7 (930) 364-01-64",
                1L,
                "Manager",
                45_000,
                true
        );
    }

    private EmployeeEditDto getEditDtoWithWrongPayment() {
        return new EmployeeEditDto(
                "Testov",
                "Test",
                "+7 (900) 100-11-13",
                1L,
                "Manager",
                1_000_000,
                false
        );
    }
}