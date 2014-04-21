package com.v5.test.worker.task;

import com.lmax.disruptor.RingBuffer;
import com.v5.base.event.EventPublisher;
import com.v5.test.worker.bean.MessageInfo;
import com.v5.test.worker.bean.TaskSnapshort;
import com.v5.test.worker.constant.EventPath;
import com.v5.test.worker.util.MsgContentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by piguangtao on 14-2-19.
 * 启动分发线程，由该任务进行发送速率和发送策略的控制
 * 可以根据消息的类型组装不同的消息内容
 */
public class DispatcherTask extends Thread implements InitializingBean {
    private static Logger LOGGER = LoggerFactory.getLogger(DispatcherTask.class);

    @Value("${test.max.rate}")
    private int rate;

    @Value("${text.main.content}")
    private String textMainContent;

    @Autowired
    private RingBufferDispatcher dispatcher;

    @Value("${text.main.content.seq.start}")
    private long startSeq;

    @Value("${text.main.content.seq.end}")
    private long endSeq;

    @Value("${test.send.time}")
    private int sendTime;

    @Value("${test.send.msg.total}")
    private int msgTotal;

    @Autowired
    private EventPublisher eventPublisher;

    private AtomicLong seq;

    String from = null;

    String to = null;

    private Long beginDispatchTime = null;

    private Integer count = new Integer(0);

    private long startSendTime = 0;

    public DispatcherTask() {
        super("Task-dispatcher-thread.");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        seq = new AtomicLong(startSeq);
        start();
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try {

            while (true) {
                String userMd5 = TaskSnapshort.loginedUserSet.take();
                TaskSnapshort.loginedUserSet.put(userMd5);
                if (null == from) {
                    from = userMd5;
                    continue;
                } else {
                    to = userMd5;
                    long seqNum = seq.getAndIncrement();
                    if (0 == startSendTime) {
                        startSendTime = System.currentTimeMillis();
                    }
                    if (isOver(seqNum)) {
                        eventPublisher.send(EventPath.TASK_SEND_OVER);
                        LOGGER.info("task send over.");
                        return;
                    }
                    publishEvent(seqNum);
                    count++;
                    reset();
                    controllRate();
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void publishEvent(long seqNum) {

        RingBuffer<MessageInfo> ringBuffer = dispatcher.getDisruptor().getRingBuffer();
        long sequence = ringBuffer.next();
        MessageInfo messageInfo = ringBuffer.get(sequence);
        messageInfo.setFrom(from);
        messageInfo.setTo(to);

        messageInfo.setSeq(seqNum);
        messageInfo.setContent(MsgContentUtil.getContent(seqNum, textMainContent));
        ringBuffer.publish(sequence);
        if (null == beginDispatchTime) {
            beginDispatchTime = System.currentTimeMillis();
            count = 0;
        }
    }

    public void reset() {
        from = null;
        to = null;
    }


    private boolean isOver(long seqNum) {
        return seqNum > msgTotal || (System.currentTimeMillis() - startSendTime > sendTime * 1000);
    }

    public void controllRate() {
        long sendTime = System.currentTimeMillis() - beginDispatchTime;
        if (count >= rate) {
            try {
                if (sendTime < 1000) {
                    Thread.sleep(1000 - sendTime);
                }
                beginDispatchTime = System.currentTimeMillis();
                count = 0;
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
