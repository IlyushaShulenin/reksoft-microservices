package ru.shulenin.impl.listener.event;

import lombok.Getter;
import lombok.Setter;

import java.util.EventObject;

@Getter
@Setter
public class DepartmentMessageEvent extends EventObject {
    public static enum MessageOperationType {
        MODIFYING, DELETE
    }

    private MessageOperationType operation;

    public DepartmentMessageEvent(Object source, MessageOperationType operation) {
        super(source);
        this.operation = operation;
    }
}
