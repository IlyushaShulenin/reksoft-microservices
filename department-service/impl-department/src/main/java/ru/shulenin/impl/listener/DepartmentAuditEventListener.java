package ru.shulenin.impl.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.shulenin.impl.entity.AuditDepartmentEntity;
import ru.shulenin.impl.listener.event.AuditObjectEvent;
import ru.shulenin.impl.repository.AuditDepartmentRepository;

@Component
@RequiredArgsConstructor
public class DepartmentAuditEventListener {
    private final AuditDepartmentRepository auditDepartmentRepository;

    @EventListener
    @Transactional
    public void acceptEvent(AuditObjectEvent auditObjectEvent) {
        var auditDepartmentEntity = new AuditDepartmentEntity(auditObjectEvent);
        auditDepartmentRepository.save(auditDepartmentEntity);
    }
}
