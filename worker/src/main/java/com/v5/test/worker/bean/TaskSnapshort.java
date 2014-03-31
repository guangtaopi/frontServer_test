package com.v5.test.worker.bean;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by piguangtao on 14-2-19.
 * 存储任务执行信息的快照
 */
public class TaskSnapshort {

    private static TaskSnapshort instance = new TaskSnapshort();

    /**
     * 存放已经登录完成的用户列表
     */
    public static LinkedBlockingQueue<String> loginedUserSet = new LinkedBlockingQueue();

    private TaskSnapshort() {
    };

    public static TaskSnapshort getInstance() {
        return instance;
    }

    private AtomicLong httpLoginUserNum = new AtomicLong(0);

    private AtomicLong httpLoginUserSuccessNum = new AtomicLong(0);

    private AtomicLong tcpConnectUserNum = new AtomicLong(0);

    private AtomicLong tcpConnectUserSuccessNum = new AtomicLong(0);

    private AtomicLong tcpLoginUserNum = new AtomicLong(0);

    private AtomicLong tcpLoginUserSuccessNum = new AtomicLong(0);

    private AtomicLong tcpMessageSendNum = new AtomicLong(0);

    private AtomicLong tcpMessageReceiveNum = new AtomicLong(0);


    public AtomicLong getHttpLoginUserNum() {
        return httpLoginUserNum;
    }

    public AtomicLong getHttpLoginUserSuccessNum() {
        return httpLoginUserSuccessNum;
    }

    public AtomicLong getTcpConnectUserNum() {
        return tcpConnectUserNum;
    }

    public AtomicLong getTcpLoginUserNum() {
        return tcpLoginUserNum;
    }

    public AtomicLong getTcpLoginUserSuccessNum() {
        return tcpLoginUserSuccessNum;
    }

    public AtomicLong getTcpConnectUserSuccessNum() {
        return tcpConnectUserSuccessNum;
    }

    public AtomicLong getTcpMessageSendNum() {
        return tcpMessageSendNum;
    }

    public AtomicLong getTcpMessageReceiveNum() {
        return tcpMessageReceiveNum;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaskSnapshort{");
        sb.append("httpLoginUserNum=").append(httpLoginUserNum);
        sb.append(", httpLoginUserSuccessNum=").append(httpLoginUserSuccessNum);
        sb.append(", tcpConnectUserNum=").append(tcpConnectUserNum);
        sb.append(", tcpConnectUserSuccessNum=").append(tcpConnectUserSuccessNum);
        sb.append(", tcpLoginUserNum=").append(tcpLoginUserNum);
        sb.append(", tcpLoginUserSuccessNum=").append(tcpLoginUserSuccessNum);
        sb.append(", tcpMessageSendNum=").append(tcpMessageSendNum);
        sb.append(", tcpMessageReceiveNum=").append(tcpMessageReceiveNum);
        sb.append('}');
        return sb.toString();
    }
}
