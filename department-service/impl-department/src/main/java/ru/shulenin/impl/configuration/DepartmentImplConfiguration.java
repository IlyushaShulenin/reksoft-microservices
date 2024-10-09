package ru.shulenin.impl.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.shulenin.impl.client.EmployeeClient;
import ru.shulenin.impl.client.fallback.EmployeeClientFallbackFactory;
import ru.shulenin.impl.entity.AuditDepartmentEntity;
import ru.shulenin.impl.entity.DepartmentEntity;
import ru.shulenin.impl.entity.DepartmentPaymentEntity;
import ru.shulenin.impl.listener.DepartmentAuditEventListener;
import ru.shulenin.impl.listener.DepartmentKafkaEventListener;
import ru.shulenin.impl.repository.AuditDepartmentRepository;
import ru.shulenin.impl.repository.DepartmentPaymentRepository;
import ru.shulenin.impl.repository.DepartmentRepository;
import ru.shulenin.impl.service.impl.DepartmentServiceImpl;

@Configuration
@EntityScan(basePackageClasses = {
        DepartmentEntity.class,
        DepartmentPaymentEntity.class,
        AuditDepartmentEntity.class
})
@ComponentScan(basePackageClasses = {
        DepartmentServiceImpl.class,
        DepartmentAuditEventListener.class,
        DepartmentKafkaEventListener.class,
        EmployeeClientFallbackFactory.class
})
@EnableJpaRepositories(basePackageClasses = {
        DepartmentRepository.class,
        DepartmentPaymentRepository.class,
        AuditDepartmentRepository.class
})
@EnableFeignClients(clients = EmployeeClient.class)
@EnableScheduling
public class DepartmentImplConfiguration {
}
