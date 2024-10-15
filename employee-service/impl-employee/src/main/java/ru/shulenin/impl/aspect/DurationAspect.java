package ru.shulenin.impl.aspect;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class DurationAspect {
    private Long startTime;

    @Getter
    private Long durationMetric;

    @Pointcut("@annotation(ru.shulenin.impl.aspect.annotation.DurationCount)")
    public void callWhenFindAnnotation() {}

    @Before("callWhenFindAnnotation()")
    public void beforeMethod() {
        startTime = System.currentTimeMillis();
    }

    @After("callWhenFindAnnotation()")
    public void afterMethod() {
        var endTime = System.currentTimeMillis();
        durationMetric = endTime - startTime;
    }
}
