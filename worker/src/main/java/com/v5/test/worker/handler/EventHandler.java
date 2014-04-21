package com.v5.test.worker.handler;

import com.v5.base.client.ClientChannel;
import com.v5.base.event.On;
import com.v5.test.worker.bean.TaskSnapshort;
import com.v5.test.worker.bean.User;
import com.v5.test.worker.client.ClientOnclientManager;
import com.v5.test.worker.constant.EventPath;
import com.v5.test.worker.service.MessageLogService;
import com.v5.test.worker.service.TcpService;
import com.v5.test.worker.service.UserService;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;


/**
 * Created by piguangtao on 14-2-18.
 */
public class EventHandler {

    private static Logger LOGGER = LoggerFactory.getLogger(EventHandler.class);

    @Autowired
    private TcpService tcpService;

    @Autowired
    private UserService userService;

    @Autowired
    private ClientOnclientManager onlineClientManager;

    @Autowired
    private MessageLogService messageLogService;


    @On(EventPath.USER_HTTP_LOGIN_SUCCESS)
    public void httpLoginSuccess(User user){
        TaskSnapshort.getInstance().getHttpLoginUserSuccessNum().getAndIncrement();
        userService.saveSession(user.getSessionId(),user.getId());
        tcpService.connect(user.getId());
    }

    @On(EventPath.USER_TCP_CONNECTED)
    public void tcpConnectSuccess(ChannelHandlerContext ctx){
        TaskSnapshort.getInstance().getTcpConnectUserSuccessNum().getAndIncrement();
        userService.saveUserChannel(ctx);
        tcpService.login(ctx.channel());
    }

    @On(EventPath.USER_TCP_LOGIN_SUCCESS)
    public void tcpLoginSuccess(final String userMd5,ClientChannel clientChannel){
       TaskSnapshort.getInstance().getTcpLoginUserSuccessNum().getAndIncrement();
       userService.loginSuccess(userMd5,clientChannel);
    }

    @On(EventPath.USER_TCP_DISCONNECTED)
    public void tcpDisconnected(final String userMd5){
        onlineClientManager.removeClient((Serializable)userMd5,null);
    }

    @On(EventPath.USER_MSG_SEND)
    public void sendMsg(String from,String to,String content,long sendTime){
        messageLogService.logSentMsg(from,to,content,sendTime);
    }

    @On(EventPath.USER_MSG_RECEIVE)
    public void receiveMsg(String from,String to,String content,long recTime){
        messageLogService.logReceiveMsg(from,to,content,recTime);
    }

    @On(EventPath.TASK_SEND_OVER)
    public void taskSendOver(){
        messageLogService.taskOver();
    }
}
