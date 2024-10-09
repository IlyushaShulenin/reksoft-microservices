package ru.shulenin.impl.configuration;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.shulenin.api.dto.DepartmentMessageDto;

import java.util.HashMap;
import java.util.Map;

@Configuration
@PropertySource("classpath:kafka.properties")
public class KafkaProducerConfig {
    @Value("${kafka.server}")
    private String kafkaServer;

    @Value("${kafka.producer.id}")
    private String kafkaProducerId;

    @Bean
    public ProducerFactory<Long, DepartmentMessageDto> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaProducerId);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<Long, DepartmentMessageDto> kafkaTemplate() {
        KafkaTemplate<Long, DepartmentMessageDto> template = new KafkaTemplate<>(producerConfigs());
        template.setMessageConverter(new StringJsonMessageConverter());

        return template;
    }
}
