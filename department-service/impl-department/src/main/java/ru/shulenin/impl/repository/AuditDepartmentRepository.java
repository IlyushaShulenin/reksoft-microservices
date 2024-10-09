package ru.shulenin.impl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.shulenin.impl.entity.AuditDepartmentEntity;

public interface AuditDepartmentRepository extends JpaRepository<AuditDepartmentEntity, Long> {
}
