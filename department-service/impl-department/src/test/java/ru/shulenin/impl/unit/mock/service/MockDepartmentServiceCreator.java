package ru.shulenin.impl.unit.mock.service;

import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.shulenin.api.dto.EmployeeShortInfoDto;
import ru.shulenin.api.service.DepartmentService;
import ru.shulenin.impl.client.EmployeeClient;
import ru.shulenin.impl.entity.DepartmentEntity;
import ru.shulenin.impl.exception.UniqueAttributeAlreadyExistException;
import ru.shulenin.impl.repository.DepartmentPaymentRepository;
import ru.shulenin.impl.repository.DepartmentRepository;
import ru.shulenin.impl.service.impl.DepartmentServiceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class MockDepartmentServiceCreator {
    private final List<DepartmentEntity> departments;

    public MockDepartmentServiceCreator() {
        departments = new ArrayList<>();
        departments.add( new DepartmentEntity(1L, "Department1", LocalDate.now(), true, new ArrayList<>()));
        departments.add(new DepartmentEntity(2L, "Department2", LocalDate.now(), false, new ArrayList<>()));
        departments.add(new DepartmentEntity(3L, "Department3", LocalDate.now(), false, new ArrayList<>()));

        List<DepartmentEntity> children = new ArrayList<>();
        children.add(departments.get(1));
        children.add(departments.get(2));

        departments.get(0).setChildDepartment(children);
    }

    public DepartmentService getDepartmentService() {
        var departmentService = new DepartmentServiceImpl(
                getMockDepartmentRepository(),
                getMockDepartmentPaymentRepository(),
                getMockEmployeeClient(),
                getMockEventPublisher()
        );

        return departmentService;
    }

    public DepartmentRepository getMockDepartmentRepository() {
        var mockRepository = Mockito.mock(DepartmentRepository.class);

        Mockito.when(mockRepository.findById(Mockito.anyLong()))
                .then(invocation -> {
                    var arg = (Long) invocation.getArgument(0);

                    return departments.stream()
                            .filter(department -> department.getId().equals(arg))
                            .findFirst();
                });

        Mockito.when(mockRepository.findByName(Mockito.anyString()))
                .then(invocation -> {
                    var arg = (String) invocation.getArgument(0);

                    return departments.stream()
                            .filter(department -> department.getName().equals(arg))
                            .findFirst();
                });

        Mockito.when(mockRepository.findDepartmentWithChildId(Mockito.anyLong()))
                        .then(invocation -> {
                            var arg = (Long) invocation.getArgument(0);

                            return departments.stream()
                                    .filter(department -> {
                                        var children = department.getChildDepartment();
                                        return children.stream()
                                                .map(DepartmentEntity::getId)
                                                .toList()
                                                .contains(arg);
                                    }).findFirst()
                                    .get();
                        });

        Mockito.when(mockRepository.saveAndFlush(Mockito.any()))
                .then(invocation -> {
                    var department = (DepartmentEntity) invocation.getArgument(0);

                    if (department.getName().equals("Sales Department")) {
                        throw new UniqueAttributeAlreadyExistException();
                    }

                    var ids = departments.stream()
                            .map(DepartmentEntity::getId)
                            .toList();

                    if (!ids.contains(department.getId())) {
                        var nextId = (long) departments.size() + 1;
                        department.setId(nextId);
                        departments.add(department);
                    }

                    var old = departments.stream()
                            .filter(dprt -> dprt.getId().equals(department.getId()))
                            .findFirst()
                            .get();

                    if (!department.equals(old)) {
                        old.setName(department.getName());
                        old.setIsMain(department.getIsMain());
                        old.setChildDepartment(department.getChildDepartment());
                    }

                    return department;
                });

        Mockito.when(mockRepository.findMainDepartment())
                        .then(invocation ->
                             departments.stream()
                                    .filter(DepartmentEntity::getIsMain)
                                    .findFirst()
                                     .get()
                        );

        Mockito.doAnswer(invocation -> {
                    var id = (long) invocation.getArgument(0);
                    departments.remove((int) id - 1);
                    return null;
                })
                .when(mockRepository).deleteById(Mockito.anyLong());

        Mockito.doAnswer(invocation -> {
            var parentId = (long) invocation.getArgument(0);
            var childId = (long) invocation.getArgument(1);

            var child = departments.remove((int) childId - 1);
            var parent = departments.remove((int) parentId - 1);
            parent.getChildDepartment().add(child);

            departments.add((int) parentId - 1, parent);
            departments.add((int) childId - 1, child);

            return Optional.empty();
        }).when(mockRepository).addChildDepartment(Mockito.anyLong(), Mockito.anyLong());

        return mockRepository;
    }

    public DepartmentPaymentRepository getMockDepartmentPaymentRepository() {
        var mockRepository = Mockito.mock(DepartmentPaymentRepository.class);
        Mockito.when(mockRepository.saveAll(Mockito.anyList())).thenReturn(Collections.emptyList());

        return mockRepository;
    }

    public EmployeeClient getMockEmployeeClient() {
        var client = mock(EmployeeClient.class);

        when(client.getAllEmployees(anyLong()))
                .thenAnswer(invocation -> {
                    if ((Long) invocation.getArgument(0) != 1) {
                        return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
                    }

                    return new ResponseEntity<>(List.of(new EmployeeShortInfoDto()), HttpStatus.OK);
                });

        when(client.getCommonPaymentForDepartment(anyLong()))
                .thenReturn(new ResponseEntity<>(0, HttpStatus.OK));

        when(client.countEmployeesInDepartment(anyLong()))
                .then(invocation -> {
                    var arg = (Long) invocation.getArgument(0);

                    if (arg > 1) {
                        return new ResponseEntity<>(0, HttpStatus.OK);
                    }

                    return new ResponseEntity<>(1, HttpStatus.OK);
                });

        when(client.getLeaderInDepartment(anyLong()))
                .thenReturn(new ResponseEntity<>(new EmployeeShortInfoDto("test", "test"), HttpStatus.OK));

        return client;
    }

    public ApplicationEventPublisher getMockEventPublisher() {
        var mockPublisher = Mockito.mock(ApplicationEventPublisher.class);
        Mockito.doNothing().when(mockPublisher).publishEvent(Mockito.any());

        return mockPublisher;
    }

    public DepartmentEntity getCorrectEntity() {
        return new DepartmentEntity(
                9L,
                "test",
                LocalDate.now(),
                false,
                null
        );
    }

    public DepartmentEntity getExistingEntity() {
        return new DepartmentEntity(
                9L,
                "Sales Department",
                LocalDate.now(),
                false,
                null
        );
    }

    public DepartmentEntity getMainDepartment() {
        return new DepartmentEntity(
                1L,
                "Main department",
                LocalDate.now(),
                true,
                List.of(getCorrectEntity())
        );
    }
}
