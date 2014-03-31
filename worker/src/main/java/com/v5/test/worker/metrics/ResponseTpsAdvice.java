package com.v5.test.worker.metrics;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.BeforeAdvice;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by piguangtao on 14-3-15.
 */
public class ResponseTpsAdvice implements BeforeAdvice, InitializingBean {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private MetricRegistry metrics;

    private Meter meter;

    @Override
    public void afterPropertiesSet() throws Exception {
        meter = metrics.meter("receive.tps");
    }

    public void meter(JoinPoint joinPoint) {
        meter.mark();
        LOGGER.debug("declareTypeName:{}. method:{}", joinPoint.getSignature().getDeclaringType(), joinPoint.getSignature().getName());

    }

}
