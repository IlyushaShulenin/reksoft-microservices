package ru.shulenin.impl.listener.event;

import lombok.Getter;
import lombok.Setter;
import ru.shulenin.impl.entity.Operation;

import java.time.Instant;
import java.util.EventObject;

@Getter
@Setter
public class AuditObjectEvent extends EventObject {
    private final Instant timestamp;
    private final Operation operation;

    public AuditObjectEvent(Object source, Instant timestamp, Operation operation) {
        super(source);
        this.timestamp = timestamp;
        this.operation = operation;
    }
}
