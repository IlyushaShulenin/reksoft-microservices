package ru.shulenin.impl.integration.configuration;

import org.mockito.Mockito;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.shulenin.api.service.DepartmentService;
import ru.shulenin.api.dto.EmployeeShortInfoDto;
import ru.shulenin.impl.client.EmployeeClient;
import ru.shulenin.impl.repository.DepartmentPaymentRepository;
import ru.shulenin.impl.repository.DepartmentRepository;
import ru.shulenin.impl.service.impl.DepartmentServiceImpl;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Configuration
@Profile("test")
public class ServiceDepartmentImplConfiguration {
    @Bean
    public EmployeeClient mockEmployeeClient() {
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
                .thenReturn(new ResponseEntity<>(1, HttpStatus.OK));

        when(client.getLeaderInDepartment(anyLong()))
                .thenReturn(new ResponseEntity<>(new EmployeeShortInfoDto("test", "test"), HttpStatus.OK));

        return client;
    }

    @Bean
    ApplicationEventPublisher eventPublisher() {
        var mockPublisher = Mockito.mock(ApplicationEventPublisher.class);
        Mockito.doNothing().when(mockPublisher).publishEvent(any(ApplicationEvent.class));

        return mockPublisher;
    }

    @Bean
    public DepartmentService departmentService(
            DepartmentRepository departmentRepository,
            DepartmentPaymentRepository departmentPaymentRepository
    ) {
        return new DepartmentServiceImpl(
                departmentRepository,
                departmentPaymentRepository,
                mockEmployeeClient(),
                eventPublisher()
        );
    }
}
