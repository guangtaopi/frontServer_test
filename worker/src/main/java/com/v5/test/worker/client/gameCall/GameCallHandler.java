package com.v5.test.worker.client.gameCall;

import com.v5.base.client.ClientChannel;
import com.v5.base.event.EventPublisher;
import com.v5.base.handler.AbstractPacketHandler;
import com.v5.base.handler.Logined;
import com.v5.test.worker.client.ClientChannelNettyImpl;
import com.v5.test.worker.client.ClientOnclientManager;
import com.v5.test.worker.constant.EventPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by piguangtao on 2014/6/25.
 */
@Logined(need = true)
public class GameCallHandler extends AbstractPacketHandler<GameCallRespPacket> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameCallHandler.class);

    public GameCallHandler() {
        this.setSupportPacketTypes(GameCallRespPacket.COMMAND_GAME_CALL_RESP_PACKET_TYPE);
    }

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private ClientOnclientManager clientOnclientManager;

    @Override
    public void handler(ClientChannel clientChannel, GameCallRespPacket respPacket) {
        LOGGER.debug("[game-call-handl].traceId:{},packet:{},channel:{}",respPacket.getTraceId(),respPacket,clientChannel);
        ClientChannelNettyImpl clientChannelNetty = (ClientChannelNettyImpl)clientChannel;
        String to = clientOnclientManager.getUserByChannel(clientChannelNetty.getChannel());
        String from = respPacket.getPeerName();
        switch (respPacket.getCallType()){
            case CALL:{
                eventPublisher.send(EventPath.USER_GAME_CALL_RECEIVE,from,to,respPacket,System.currentTimeMillis());
                break;
            }

            case ACCEPTE:{
                eventPublisher.send(EventPath.USER_GAME_CALL_RECEIVE,from,to,respPacket,System.currentTimeMillis());
                break;
            }

            case REJECT:{
                eventPublisher.send(EventPath.USER_GAME_CALL_RECEIVE,from,to,respPacket,System.currentTimeMillis());
                break;
            }

            case HANGUP:{
                eventPublisher.send(EventPath.USER_GAME_CALL_RECEIVE,from,to,respPacket,System.currentTimeMillis());
                break;
            }
            case RECEIVED:{
                eventPublisher.send(EventPath.USER_GAME_CALL_RECEIVE,from,to,respPacket,System.currentTimeMillis());
                break;
            }
            case BUSY:
            case ASSIST_CALL:{
                eventPublisher.send(EventPath.USER_GAME_CALL_RECEIVE,from,to,respPacket,System.currentTimeMillis());
                break;
            }
            default:{
                LOGGER.warn("[game call].calltype:{} not support.packet:{},clientChannel:{}",respPacket,clientChannel);
                break;
            }
        }
    }
}
