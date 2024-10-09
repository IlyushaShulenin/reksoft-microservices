package ru.shulenin.impl.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.shulenin.api.dto.DepartmentMessageDto;
import ru.shulenin.impl.listener.event.DepartmentMessageEvent;

@Component
@RequiredArgsConstructor
public class DepartmentKafkaEventListener {
    private final KafkaTemplate<Long, DepartmentMessageDto> kafkaTemplate;

    @EventListener
    public void acceptEvent(DepartmentMessageEvent event) {
        var message = (DepartmentMessageDto) event.getSource();
        var topic = "department." + event.getOperation().name();

        kafkaTemplate.send(topic, message);
    }
}
