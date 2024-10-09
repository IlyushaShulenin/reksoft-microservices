package ru.shulenin.impl.configuration;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import ru.shulenin.impl.client.DepartmentClient;

@Configuration
@EnableFeignClients(clients = DepartmentClient.class)
public class EmployeeImplConfiguration {}
