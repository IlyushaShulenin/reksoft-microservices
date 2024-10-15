package ru.shulenin.impl.configuration;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.shulenin.impl.aspect.DurationAspect;

@Configuration
public class MeterBinderConfiguration {
    @Bean
    public MeterBinder meterBinder(DurationAspect durationAspect) {
        return registry ->
            Gauge.builder("employee_saving_request_duration", durationAspect::getDurationMetric)
                    .register(registry);
    }
}
