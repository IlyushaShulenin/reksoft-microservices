package ru.shulenin.impl.integration.configuration;

import lombok.RequiredArgsConstructor;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import ru.shulenin.api.controller.CommunicatorWithDepartmentServiceRestController;
import ru.shulenin.api.dto.DepartmentMessageDto;
import ru.shulenin.api.service.EmployeeService;
import ru.shulenin.impl.client.DepartmentClient;
import ru.shulenin.impl.controller.CommutatorWithDepartmentServiceRestControllerImpl;
import ru.shulenin.impl.repository.DepartmentSnapshotRepository;
import ru.shulenin.impl.repository.EmployeeRepository;
import ru.shulenin.impl.service.impl.EmployeeServiceImpl;

@TestConfiguration
@RequiredArgsConstructor
public class ServiceEmployeeImplConfiguration {
//    @MockBean
//    private final DepartmentClient departmentClient;
//
//    @Bean
//    public DepartmentClient departmentClient() {
//        Mockito.when(departmentClient.findDepartmentById(1L))
//                .thenReturn(new ResponseEntity<>(new DepartmentMessageDto(1L, "test1"), HttpStatus.OK));
//
//        Mockito.when(departmentClient.findDepartmentById(Mockito.anyLong()))
//                .thenReturn(new ResponseEntity<>(new DepartmentMessageDto(2L, "test2"), HttpStatus.OK));
//
//        Mockito.when(departmentClient.findDepartmentById(100L))
//                .thenReturn(new ResponseEntity<>(new DepartmentMessageDto(100L, "test3"), HttpStatus.NOT_FOUND));
//
//        return departmentClient;
//    }

    @Bean
//    @Primary
//    @Profile("test")
    public DepartmentClient departmentClient() {
        var client = Mockito.mock(DepartmentClient.class);

        Mockito.when(client.findDepartmentById(1L))
                .thenReturn(new ResponseEntity<>(new DepartmentMessageDto(1L, "test1"), HttpStatus.OK));

        Mockito.when(client.findDepartmentById(2L))
                .thenReturn(new ResponseEntity<>(new DepartmentMessageDto(2L, "test2"), HttpStatus.OK));

        Mockito.when(client.findDepartmentById(100L))
                .thenReturn(new ResponseEntity<>(new DepartmentMessageDto(100L, "test3"), HttpStatus.NOT_FOUND));

        return client;
    }
    @Bean
    public EmployeeService employeeService(EmployeeRepository employeeRepository,
                                           DepartmentSnapshotRepository departmentSnapshotRepository) {
        return new EmployeeServiceImpl(
                employeeRepository,
                departmentSnapshotRepository,
                departmentClient()
        );
    }
}
