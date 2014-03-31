package com.v5.test.worker.handler;


import com.lmax.disruptor.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by piguangtao on 14-3-25.
 */
public class DisruptorExceptionHandler implements ExceptionHandler {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Override
    public void handleEventException(Throwable ex, long sequence, Object event) {
        LOGGER.error("handleEventException.sequence:{},event:{}.ex:{}",sequence,event,ex);
    }

    @Override
    public void handleOnStartException(Throwable ex) {
        LOGGER.error("handleOnStartException.ex:{}",ex);
    }

    @Override
    public void handleOnShutdownException(Throwable ex) {
        LOGGER.error("handleOnShutdownException.ex:{}",ex);
    }
}
