package ru.shulenin.impl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.shulenin.impl.entity.DepartmentPaymentEntity;

public interface DepartmentPaymentRepository extends JpaRepository<DepartmentPaymentEntity, Long> {
}
