package ru.shulenin.impl.unit.mock.service;

import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import ru.shulenin.api.dto.DepartmentMessageDto;
import ru.shulenin.api.service.EmployeeService;
import ru.shulenin.impl.client.DepartmentClient;
import ru.shulenin.impl.entity.EmployeeEntity;
import ru.shulenin.impl.entity.Gender;
import ru.shulenin.impl.repository.DepartmentSnapshotRepository;
import ru.shulenin.impl.repository.EmployeeRepository;
import ru.shulenin.impl.service.impl.EmployeeServiceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MockEmployeeServiceCreator {
    private final List<EmployeeEntity> employees;

    public MockEmployeeServiceCreator() {
        employees = new ArrayList<>();
        employees.add(new EmployeeEntity(
                1L,
                RandomStringUtils.randomAlphanumeric(8),
                RandomStringUtils.randomAlphanumeric(8),
                RandomStringUtils.randomAlphanumeric(8),
                Gender.MALE,
                LocalDate.now(),
                "phone-number-1",
                1L,
                LocalDate.now(),
                null,
                RandomStringUtils.randomAlphanumeric(8),
                48_000,
                true));

        employees.add(new EmployeeEntity(
                2L,
                RandomStringUtils.randomAlphanumeric(8),
                RandomStringUtils.randomAlphanumeric(8),
                RandomStringUtils.randomAlphanumeric(8),
                Gender.MALE,
                LocalDate.now(),
                "phone-number-2",
                1L,
                LocalDate.now(),
                null,
                RandomStringUtils.randomAlphanumeric(8),
                35_000,
                false));

        employees.add(new EmployeeEntity(
                3L,
                RandomStringUtils.randomAlphanumeric(8),
                RandomStringUtils.randomAlphanumeric(8),
                RandomStringUtils.randomAlphanumeric(8),
                Gender.MALE,
                LocalDate.now(),
                "phone-number-3",
                2L,
                LocalDate.now(),
                null,
                RandomStringUtils.randomAlphanumeric(8),
                39_000,
                true));
    }

    public EmployeeService getMockEmployeeService() {
        return new EmployeeServiceImpl(
                mockEmployeeRepository(),
                mockDepartmentSnapshotRepository(),
                mockDepartmentClint()
        );
    }

    public EmployeeRepository mockEmployeeRepository() {
        var mockRepository = Mockito.mock(EmployeeRepository.class);

        Mockito.when(mockRepository.findById(Mockito.anyLong()))
                .then(invocation -> {
                    var arg = invocation.getArgument(0);
                    return employees.stream()
                            .filter(employee -> employee.getId().equals(arg) && employee.getQuiteDate() == null)
                            .findFirst();
                });

        Mockito.when(mockRepository.findByPhoneNumber(Mockito.anyString()))
                .then(invocation -> {
                    var arg = invocation.getArgument(0);
                    return employees.stream()
                            .filter(employee -> employee.getPhoneNumber().equals(arg) && employee.getQuiteDate() == null)
                            .findFirst();
                });

        Mockito.when(mockRepository.saveAndFlush(Mockito.any()))
                .then(invocation -> {
                    var arg = (EmployeeEntity) invocation.getArgument(0);
                    if (arg.getId() != null && arg.getId() <= employees.size()) {
                        var oldEmployee = employees.stream()
                                .filter(employee -> employee.getId().equals(arg.getId()))
                                .findFirst()
                                .get();

                        oldEmployee.setPhoneNumber(arg.getPhoneNumber());
                        oldEmployee.setPayment(arg.getPayment());
                        oldEmployee.setDepartmentId(arg.getDepartmentId());
                        oldEmployee.setPosition(arg.getPosition());
                        oldEmployee.setQuiteDate(arg.getQuiteDate());

                        return oldEmployee;
                    }

                    var nextId = (long) employees.size() + 1;
                    arg.setId(nextId);
                    employees.add(arg);

                    return arg;
                });

        Mockito.when(mockRepository.findByDepartmentId(Mockito.anyLong()))
                        .then(invocation -> {
                            var arg = (Long) invocation.getArgument(0);
                            return employees.stream()
                                    .filter(employee -> employee.getDepartmentId().equals(arg))
                                    .toList();
                        });

        Mockito.when(mockRepository.findLeaderInDepartment(Mockito.anyLong()))
                .then(invocation -> {
                    var arg = invocation.getArgument(0);
                    return employees.stream()
                            .filter(employee -> employee.getDepartmentId().equals(arg) &&
                                    employee.getIsLeader())
                            .findFirst();
                });

        Mockito.when(mockRepository.commonPaymentForDepartment(Mockito.anyLong()))
                .then(invocation -> {
                    var arg = (Long) invocation.getArgument(0);

                    if (arg.equals(1L)) {
                        return 83_000;
                    }

                    if (arg.equals(2L)) {
                        return 39_000;
                    }

                    return 0;
                });

        return mockRepository;
    }

    public DepartmentSnapshotRepository mockDepartmentSnapshotRepository() {
        var mockRepository = Mockito.mock(DepartmentSnapshotRepository.class);

        Mockito.when(mockRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(mockRepository.saveAndFlush(Mockito.any()))
                .thenReturn(null);

        return mockRepository;
    }

    public DepartmentClient mockDepartmentClint() {
        var mockClient = Mockito.mock(DepartmentClient.class);

        Mockito.when(mockClient.findDepartmentById(Mockito.anyLong()))
                .then(invocation -> {
                    var arg = (long) invocation.getArgument(0);
                    HttpStatus status = HttpStatus.NOT_FOUND;

                    if (arg > 0 && arg <= 4) {
                        status = HttpStatus.OK;
                    }

                    return  new ResponseEntity<>(new DepartmentMessageDto(arg, "Department"), status);
                });

        return mockClient;
    }
}
