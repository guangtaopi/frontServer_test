package com.v5.test.worker.handler;

import com.v5.base.client.ClientChannel;
import com.v5.base.event.On;
import com.v5.base.message.command.CallStatus;
import com.v5.base.message.command.UDPServerPacket;
import com.v5.base.message.notify.SystemNotifyPackage;
import com.v5.base.message.text.ForwardMessagePacket;
import com.v5.base.message.text.ImageMessagePacket;
import com.v5.base.message.text.TextMessagePacket;
import com.v5.base.message.text.VoiceMessagePacket;
import com.v5.test.worker.bean.TaskSnapshort;
import com.v5.test.worker.bean.User;
import com.v5.test.worker.client.ClientOnclientManager;
import com.v5.test.worker.client.ForwardMessageHandler;
import com.v5.test.worker.client.gameCall.GameCallRespPacket;
import com.v5.test.worker.client.gameCall.GameServerRespPacket;
import com.v5.test.worker.constant.EventPath;
import com.v5.test.worker.packet.StatusResponsePackage;
import com.v5.test.worker.service.MessageLogService;
import com.v5.test.worker.service.TcpService;
import com.v5.test.worker.service.UserService;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.nio.charset.Charset;


/**
 * Created by piguangtao on 14-2-18.
 */
public class EventHandler {

    @Autowired
    private TcpService tcpService;

    @Autowired
    private UserService userService;

    @Autowired
    private ClientOnclientManager onlineClientManager;

    @Autowired
    private MessageLogService messageLogService;

    @Autowired(required = false)
    private ITestResultHandler testResultHandler;


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
    public void receiveMsg(String from,String to,TextMessagePacket packet,long recTime){
        String content = new String(packet.getContent(), Charset.forName("utf-8"));

        messageLogService.logReceiveMsg(from,to,content,recTime);
        if(null != testResultHandler){
            testResultHandler.receiveSingleTextMsg(from,to,packet,recTime);
        }
    }

    @On(EventPath.TASK_SEND_OVER)
    public void taskSendOver(){
        messageLogService.taskOver();
    }

    @On(EventPath.USER_TRANS_MSG_RECEIVE)
    public void receiveTransMsg(String from,String to,ForwardMessagePacket packet){
        if(null != testResultHandler){
            testResultHandler.receiveTransTextMsg(from, to, packet);
        }
    }

    @On(EventPath.USER_IMAGE_MSG_RECEIVE)
    public void receiveImageMsg(String from,String to,ImageMessagePacket packet,long recTime){
        if(null != testResultHandler){
            testResultHandler.receiveImageMsg(from,to,packet);
        }
    }

    @On(EventPath.USER_MSG_STATUS)
    public void receiveMsgStatus(StatusResponsePackage packet){
        if(null != testResultHandler){
            testResultHandler.receiveMsgStatus(packet);
        }
    }
    @On(EventPath.USER_AUDIO_MSG_RECEIVE)
    public void receiveAudioMsg(String from,String to,VoiceMessagePacket packet,long recTime){
        if(null != testResultHandler){
            testResultHandler.receiveAudioMsg(from,to,packet);
        }
    }

    @On(EventPath.USER_CALL_RECEIVE_REQUEST)
    public void receiveCallReqeust(String from ,String to,CallStatus callStatus){
        if(null != testResultHandler){
            testResultHandler.receiveCallRequest(from,to,callStatus);
        }
    }

    @On(EventPath.USER_CALL_UDP_SERVER_RECEIVE)
    public void callUDPServerReceive(String userId,UDPServerPacket packet){
        if(null != testResultHandler){
            testResultHandler.receiveUDPServerPacket(userId,packet);
        }
    }

    @On(EventPath.USER_SYSTEM_NOTIFY_RECEIVE)
    public void receiveSystemNotify(String receiver,SystemNotifyPackage notifyPackage){
        if(null != testResultHandler){
            testResultHandler.receiveSystemNotify(receiver,notifyPackage);
        }
    }

    @On(EventPath.USER_GAME_CALL_RECEIVE)
    public void receiveGameCallPacket(String from,String to,GameCallRespPacket respPacket,long currenTime){
        if(null != testResultHandler){
            testResultHandler.receiveGameCallRespPacket(from,to,respPacket,currenTime);
        }
    }

    @On(EventPath.USER_GAME_SERVER_RECEIVE)
    public void receiveGameCallPacket(String userId,GameServerRespPacket respPacket,long currenTime){
        if(null != testResultHandler){
            testResultHandler.receiveGameServerInfo(userId,respPacket,currenTime);
        }
    }


}
