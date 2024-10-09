package ru.shulenin.impl.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.shulenin.impl.listener.event.AuditObjectEvent;

import java.time.Instant;

import static jakarta.persistence.GenerationType.*;

@Entity
@Table(name = "audit_department", schema = "departments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditDepartmentEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private Long departmentId;

    private Instant timestamp;

    @Enumerated(EnumType.STRING)
    private Operation operation;

    public AuditDepartmentEntity(Long departmentId, Instant timestamp, Operation operation) {
        this.departmentId = departmentId;
        this.timestamp = timestamp;
        this.operation = operation;
    }

    public AuditDepartmentEntity(AuditObjectEvent auditObjectEvent) {
        this.departmentId = (Long) auditObjectEvent.getSource();
        this.timestamp = auditObjectEvent.getTimestamp();
        this.operation = auditObjectEvent.getOperation();
    }
}
