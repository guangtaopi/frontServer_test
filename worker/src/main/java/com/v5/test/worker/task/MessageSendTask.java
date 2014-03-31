package com.v5.test.worker.task;

import com.lmax.disruptor.EventHandler;
import com.v5.base.message.text.SimpleMessagePacket;
import com.v5.test.worker.bean.MessageInfo;
import com.v5.test.worker.service.TcpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by piguangtao on 14-2-20.
 * 发送文本消息的任务
 */
public class MessageSendTask implements EventHandler<MessageInfo>{

    /**
     * 文本消息发送
     */
    private static final String TASK_TYPE_TEXT = "text";

    /**
     * 消息转发
     */
    private static final String TASK_TYPE_TRANS = "trans";

    private static Logger LOGGER = LoggerFactory.getLogger(MessageSendTask.class);

    private RingBufferDispatcher dispatcher;

    private TcpService tcpService;

    private String taskType;

    public MessageSendTask(RingBufferDispatcher dispatcher,TcpService tcpService,String taskType){
        this.dispatcher = dispatcher;
        this.tcpService = tcpService;
        this.taskType = taskType;
    }


    @Override
    public void onEvent(MessageInfo event, long sequence, boolean endOfBatch) throws Exception {
        LOGGER.debug("[Disraptor-handle-event].event:{},sequence:{},endOfBatch:{}",event,sequence,endOfBatch);
        if(TASK_TYPE_TEXT.equalsIgnoreCase(taskType)){
            sendText(event);
        }
        else if(TASK_TYPE_TRANS.equalsIgnoreCase(taskType)){
            transText(event);
        }
    }

    private void sendText(MessageInfo event){
        SimpleMessagePacket packet = tcpService.formTextSimpleMessagePacket(event);
        tcpService.sendSimpleMessagePacket(packet);
    }

    private void transText(MessageInfo event){
        tcpService.sendTransMessagePacket(event);
    }
}
