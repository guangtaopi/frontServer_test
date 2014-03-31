package com.v5.test.worker.task;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.v5.test.worker.bean.MessageInfo;
import com.v5.test.worker.handler.DisruptorExceptionHandler;
import com.v5.test.worker.service.TcpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by piguangtao on 14-2-19.
 */
public class RingBufferDispatcher implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(RingBufferDispatcher.class);

    @Value("${test.cousumer.thread.num}")
    private int consumerThreadNum;

    @Value("${disruptor.waitStrategy}")
    private String waitStrategy;

    /**
     * ringBuffer大小，要可以配置
     */
    private int ringBufferSize = 1024;

    private Disruptor<MessageInfo> disruptor;

    @Autowired
    private TcpService tcpService;

    @Value("${test.task.type}")
    private String taskType;

    /**
     * Invoked by a BeanFactory after it has set all bean properties supplied
     * (and satisfied BeanFactoryAware and ApplicationContextAware).
     * <p>This method allows the bean instance to perform initialization only
     * possible when all bean properties have been set and to throw an
     * exception in the event of misconfiguration.
     *
     * @throws Exception in the event of misconfiguration (such
     *                   as failure to set an essential property) or if initialization fails.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        //消费者启动的线程数
        ExecutorService exec = Executors.newCachedThreadPool();
        disruptor = new Disruptor<MessageInfo>(MessageInfo.EVENT_FACTORY,ringBufferSize,exec, ProducerType.SINGLE,getWaitStrategy());
        disruptor.handleExceptionsWith(new DisruptorExceptionHandler());
        MessageSendTask[] handlers = new MessageSendTask[consumerThreadNum];
        for(int i =0;i<consumerThreadNum;i++){
            handlers[i] = new MessageSendTask(this,tcpService,taskType.trim());
        }
        disruptor.handleEventsWith(handlers);
        disruptor.start();
    }

    public Disruptor<MessageInfo> getDisruptor() {
        return disruptor;
    }

    private WaitStrategy getWaitStrategy(){
        WaitStrategy result = null;
        switch (waitStrategy){
            case "yield":{
                result = new YieldingWaitStrategy();
                break;
            }
            case "blocking":{
                result = new BlockingWaitStrategy();
                break;
            }
            case "busySpin":{
                result = new BusySpinWaitStrategy();
                break;
            }
            case "timeoutBlocking":{
                result = new TimeoutBlockingWaitStrategy(5,TimeUnit.MILLISECONDS);
                break;
            }
            case "sleeping":{
                result = new SleepingWaitStrategy();
                break;
            }
            default:{
               LOGGER.error("waitStrategy:{} not support.",waitStrategy);
                break;
            }
        }

        return result;
    }
}
