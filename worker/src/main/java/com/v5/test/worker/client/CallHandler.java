package com.v5.test.worker.client;

import com.v5.base.client.ClientChannel;
import com.v5.base.event.EventPublisher;
import com.v5.base.handler.AbstractPacketHandler;
import com.v5.base.handler.Logined;
import com.v5.base.message.command.CallPacket;
import com.v5.test.worker.constant.EventPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: piguangtao
 * Date: 14-1-6
 * To change this template use File | Settings | File Templates.
 */
@Logined(need = true)
public class CallHandler extends AbstractPacketHandler<CallPacket> {
    private static final Logger LOGGER = LoggerFactory.getLogger(com.v5.base.message.command.CallHandler.class);

    public CallHandler() {
        this.setSupportPacketTypes(CallPacket.COMMAND_CALL_RESPONSE_TYPE*256+CallPacket.COMMAND_PACKET_TYPE);
    }

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private ClientOnclientManager clientOnclientManager;

    @Override
    public void handler(ClientChannel clientChannel, CallPacket packet) {
        ClientChannelNettyImpl clientChannelNetty = (ClientChannelNettyImpl)clientChannel;
        String to = clientOnclientManager.getUserByChannel(clientChannelNetty.getChannel());
        String from = packet.getPeerName();

        LOGGER.debug("[Conn-Call].Thead:{},traceId:{},sender:{},receiver:{},code:{}.channel:{}.start handle.",
                Thread.currentThread(), packet.getTraceId(),
                from, to, packet.getCallStatus(), clientChannel);


        switch (packet.getCallStatus()) {
            //视频、语音呼叫
            case VIDEO_REQUEST:
            case AUDIO_REQUEST: {
                callRequest(clientChannel, packet);
                break;
            }
            //接受
            case VIDEO_ACCEPT:
            case AUDIO_ACCEPT: {
                accept(clientChannel, packet);
                break;
            }
            //拒绝
            case REJECT: {
                reject(clientChannel, packet);
                break;
            }
            //挂断
            case HANGUP: {
                hangup(clientChannel, packet);
                break;
            }

            //持续辅助呼叫/正在呼叫中/对方正忙 直接转发
            case BUSY:
            case VIDEO_REQUEST_AGAIN:
            case AUDIO_REQUEST_AGAIN: {
                transCommand(clientChannel, packet);
                break;
            }

            //被呼叫方收到"对方已经收到呼叫"，发送配对信息
            case RECEIVED: {
                LOGGER.info("no handle");
                break;
            }
            default: {
                LOGGER.warn("[Conn-Call].traceId:{},channel:{},sender:{},receiver:{},code:{}.code not supported.",
                        packet.getTraceId(),
                        clientChannel.getOnlineClient(), from, to, packet.getCallStatus());
                break;
            }
        }

        LOGGER.info("[Conn-Call].traceId:{},channel:{},sender:{},receiver:{},code:{}. exit handle.",
                packet.getTraceId(),
                clientChannel, from, to, packet.getCallStatus());

    }


    /**
     * 呼叫接受方从服务端收到
     * @param clientChannel
     * @param packet
     */
    private void callRequest(ClientChannel clientChannel, CallPacket packet) {
        ClientChannelNettyImpl clientChannelNetty = (ClientChannelNettyImpl) clientChannel;
        String receive = clientOnclientManager.getUserByChannel(clientChannelNetty.getChannel());
        String from = packet.getPeerName();

        //收到服务端转发的呼叫
        eventPublisher.send(EventPath.USER_CALL_RECEIVE_REQUEST,from,receive,packet.getCallStatus());
    }

    /**
     * 发送方从服务端收到
     * @param clientChannel
     * @param packet
     */
    private void accept(ClientChannel clientChannel, CallPacket packet){
        ClientChannelNettyImpl clientChannelNetty = (ClientChannelNettyImpl) clientChannel;
        String from = clientOnclientManager.getUserByChannel(clientChannelNetty.getChannel());
        String receiver = packet.getPeerName();

        //收到服务端转发的呼叫
        eventPublisher.send(EventPath.USER_CALL_RECEIVE_REQUEST,from,receiver,packet.getCallStatus());
    }

    /**
     * 测试时，发送方从服务端接受到
     * @param clientChannel
     * @param packet
     */
    private void reject(ClientChannel clientChannel, CallPacket packet){
        ClientChannelNettyImpl clientChannelNetty = (ClientChannelNettyImpl) clientChannel;
        String from = clientOnclientManager.getUserByChannel(clientChannelNetty.getChannel());
        String receive = packet.getPeerName();

        //收到服务端转发的呼叫
        eventPublisher.send(EventPath.USER_CALL_RECEIVE_REQUEST,from,receive,packet.getCallStatus());
    }

    /**
     * 测试时，发送方从服务端接受到呼叫接受方的挂断
     * @param clientChannel
     * @param packet
     */
    private void hangup(ClientChannel clientChannel, CallPacket packet){
        ClientChannelNettyImpl clientChannelNetty = (ClientChannelNettyImpl) clientChannel;

        //呼叫的发起方
        String from = clientOnclientManager.getUserByChannel(clientChannelNetty.getChannel());

        //呼叫的接受方
        String to = packet.getPeerName();

        //收到服务端转发的呼叫
        eventPublisher.send(EventPath.USER_CALL_RECEIVE_REQUEST,from,to,packet.getCallStatus());
    }

    /**
     * 接受方从服务端接受到
     * @param clientChannel
     * @param packet
     */
    private void transCommand(ClientChannel clientChannel, CallPacket packet){
        ClientChannelNettyImpl clientChannelNetty = (ClientChannelNettyImpl) clientChannel;
        String receive = clientOnclientManager.getUserByChannel(clientChannelNetty.getChannel());
        String from = packet.getPeerName();

        //收到服务端转发的呼叫
        eventPublisher.send(EventPath.USER_CALL_RECEIVE_REQUEST,from,receive,packet.getCallStatus());
    }
}
