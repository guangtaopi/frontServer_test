package com.v5.test.worker.client;

import com.v5.base.client.ClientChannel;
import com.v5.base.handler.AbstractPacketHandler;
import com.v5.base.message.command.HeartbeatResponsePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by piguangtao on 14-2-19.
 */
public class HeartbeatHandler extends AbstractPacketHandler<HeartbeatResponsePacket> {

    private static Logger LOGGER = LoggerFactory.getLogger(HeartbeatHandler.class);

    public HeartbeatHandler(){
        this.setSupportPacketTypes(HeartbeatResponsePacket.COMMAND_HEARTBEAT_RESPONSE_PACKET_TYPE);
    }


    @Override
    public void handler(ClientChannel clientChannel, HeartbeatResponsePacket packet) {
        LOGGER.debug("[receive-heatbeat].channel:{}",clientChannel);
    }
}
