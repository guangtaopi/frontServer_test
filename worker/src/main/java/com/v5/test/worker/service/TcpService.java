package com.v5.test.worker.service;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.v5.base.client.ClientChannel;
import com.v5.base.client.OnlineClient;
import com.v5.base.event.EventPublisher;
import com.v5.base.message.command.CallPacket;
import com.v5.base.message.command.LoginPacket;
import com.v5.base.message.notify.SystemNotifyPackage;
import com.v5.base.message.text.*;
import com.v5.base.utils.DefaultCallBack;
import com.v5.test.worker.bean.MessageInfo;
import com.v5.test.worker.bean.TaskSnapshort;
import com.v5.test.worker.client.ClientChannelInitializer;
import com.v5.test.worker.client.ClientOnclientManager;
import com.v5.test.worker.client.gameCall.GameCallReqPacket;
import com.v5.test.worker.constant.EventPath;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by piguangtao on 14-2-18.
 */
public class TcpService implements InitializingBean{

    private static Logger LOGGER = LoggerFactory.getLogger(TcpService.class);

    @Value("${tcpServer.urls}")
    private String tcpServerUrls;

//    @Value("${tcpServer.ip}")
//    private String tcpIp;
//
//    @Value("${tcpServer.port}")
//    private short tcpPort;

    @Autowired
    private EventLoopGroup eventLoopGroup;

    @Autowired
    private ClientChannelInitializer clientChannelInitializer;

    @Autowired
    private ClientOnclientManager onclientManager;

    @Autowired
    private MetricRegistry metrics;

    @Value("${test.statistic.msgdelay.enable}")
    private String enableStatisticMsgdelay;

    @Autowired
    private EventPublisher eventPublisher;

    private Meter sendMsgMeter;

    private Meter ackMsgMeter;

    private String[][] frontServers;

    private AtomicInteger loginIndex = new AtomicInteger(1);

    @Override
    public void afterPropertiesSet() throws Exception {
        sendMsgMeter = metrics.meter("send-msg-meter.");
        ackMsgMeter = metrics.meter("ack-msg-meter");
        initTcpServer();
    }

    private void initTcpServer(){
        try{
            String[] tcpServerArrary = tcpServerUrls.split(",");
            frontServers = new String[tcpServerArrary.length][2];
            for(int i = 0;i<tcpServerArrary.length;i++){
                String[] tcpServer = tcpServerArrary[i].split(":");
                frontServers[i][0] = tcpServer[0];
                frontServers[i][1]= tcpServer[1];
            }
        }
        catch (Exception e){
            LOGGER.error("Fails to init tcpServer.and exit.",e);
            System.exit(-1);
        }
    }

    public void connect(final String userMd5) {

        TaskSnapshort.getInstance().getTcpConnectUserNum().getAndIncrement();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(clientChannelInitializer);

        String[] tcpServer = frontServers[loginIndex.getAndIncrement()%frontServers.length];

        bootstrap.connect(tcpServer[0], Integer.valueOf(tcpServer[1])).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    onclientManager.setChannnelUser(future.channel(), userMd5);
                } else {
                    LOGGER.error("userMd5:{}, can not connect to tcp server.", userMd5);
                }
            }
        });
    }

    public void login(final Channel channel) {
        String userMd5 = onclientManager.getUserByChannel(channel);
        String sessionId = onclientManager.getSessionByNameMd5(userMd5);
        final LoginPacket loginPacket = new LoginPacket();
        loginPacket.setSessionId(sessionId);

        onclientManager.getClient(userMd5, new DefaultCallBack<OnlineClient>() {
            @Override
            public void success(OnlineClient result) {
                TaskSnapshort.getInstance().getTcpLoginUserNum().getAndIncrement();
                result.getClientChannel().write(loginPacket);
            }
        });
    }


    public SimpleMessagePacket formTextSimpleMessagePacket(MessageInfo messageInfo) {
        TextMessagePacket simpleMessagePacket = new TextMessagePacket();
        simpleMessagePacket.setFrom(messageInfo.getFrom());
        simpleMessagePacket.setToUser(messageInfo.getTo());
        simpleMessagePacket.setContent(messageInfo.getContent().getBytes(Charset.forName("utf-8")));
        simpleMessagePacket.setMessageServiceType(SimpleMessagePacket.TO_USER);
        return simpleMessagePacket;
    }

    public void sendSimpleMessagePacket(final SimpleMessagePacket packet) {
        String from = packet.getFrom();
        onclientManager.getClient(from, new DefaultCallBack<OnlineClient>() {
            @Override
            public void success(OnlineClient result) {
                result.getClientChannel().write(packet);
                sendMsgMeter.mark();
                publishSendMsg(packet.getFrom(),packet.getToUser(),new String(packet.getContent(),Charset.forName("utf-8")));
            }
        });
    }

    public void sendTransMessagePacket(final MessageInfo messageInfo) {
        final ForwardMessagePacket packet = new ForwardMessagePacket();
        packet.setFrom(messageInfo.getFrom());
        packet.setToUser(messageInfo.getTo());
        packet.setData(messageInfo.getContent().getBytes(Charset.forName("UTF-8")));
        onclientManager.getClient(packet.getFrom(), new DefaultCallBack<OnlineClient>() {
            @Override
            public void success(OnlineClient result) {
                result.getClientChannel().write(packet);
                sendMsgMeter.mark();
                publishSendMsg(packet.getFrom(),packet.getToUser(),messageInfo.getContent());
            }
        });
    }

    public void sendAckMessage(final String from,final long messageId){
        final StatusMessagePacket statusMessagePacket = new StatusMessagePacket();
        statusMessagePacket.setMessageId(messageId);
        statusMessagePacket.setMessageStatus(MessageStatus.PEER_RECEIVED);
        onclientManager.getClient(from, new DefaultCallBack<OnlineClient>() {
            @Override
            public void success(OnlineClient result) {
                result.getClientChannel().write(statusMessagePacket);
                ackMsgMeter.mark();
            }
        });

    }


    public void sendVideoCall(final String from,final CallPacket callPacket){
        onclientManager.getClient(from, new DefaultCallBack<OnlineClient>() {
            @Override
            public void success(OnlineClient result) {
                result.getClientChannel().write(callPacket);
            }
        });
    }


    public void sendSystemNofify(final String from,final SystemNotifyPackage notifyPackage){
        onclientManager.getClient(from, new DefaultCallBack<OnlineClient>() {
            @Override
            public void success(OnlineClient result) {
                result.getClientChannel().write(notifyPackage);
            }
        });
    }


    public void sendGameCallReqPacket(final String from,final GameCallReqPacket reqPacket){
        onclientManager.getClient(from, new DefaultCallBack<OnlineClient>() {
            @Override
            public void success(OnlineClient result) {
                result.getClientChannel().write(reqPacket);
            }
        });
    }


    private void publishSendMsg(String from,String to,String content){
        if("yes".equalsIgnoreCase(enableStatisticMsgdelay)){
            eventPublisher.send(EventPath.USER_MSG_SEND,from,to,content,System.currentTimeMillis());
        }
    }









}
