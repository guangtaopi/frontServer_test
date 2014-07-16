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
public class GameServerRespHandler extends AbstractPacketHandler<GameServerRespPacket> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameServerRespHandler.class);

    public GameServerRespHandler() {
        this.setSupportPacketTypes(GameServerRespPacket.COMMAND_GAME_SERVER_RESP_PACKET_TYPE);
    }

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private ClientOnclientManager clientOnclientManager;

    @Override
    public void handler(ClientChannel clientChannel, GameServerRespPacket respPacket) {
        LOGGER.debug("[game-call-handl].traceId:{},packet:{},channel:{}",respPacket.getTraceId(),respPacket,clientChannel);
        ClientChannelNettyImpl clientChannelNetty = (ClientChannelNettyImpl)clientChannel;
        String userId = clientOnclientManager.getUserByChannel(clientChannelNetty.getChannel());
        eventPublisher.send(EventPath.USER_GAME_SERVER_RECEIVE,userId,respPacket,System.currentTimeMillis());
    }
}
