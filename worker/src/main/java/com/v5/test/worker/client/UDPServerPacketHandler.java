package com.v5.test.worker.client;

import com.v5.base.client.ClientChannel;
import com.v5.base.event.EventPublisher;
import com.v5.base.handler.AbstractPacketHandler;
import com.v5.base.handler.Logined;
import com.v5.base.message.command.UDPServerPacket;
import com.v5.test.worker.constant.EventPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Logined
public class UDPServerPacketHandler extends AbstractPacketHandler<UDPServerPacket> {
    private static Logger LOGGER = LoggerFactory.getLogger(UDPServerPacketHandler.class);

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private ClientOnclientManager clientOnclientManager;

    public UDPServerPacketHandler() {
        this.setSupportPacketTypes(UDPServerPacket.COMMAND_UDP_PACKET_TYPE);
    }

    @Override
    public void handler(final ClientChannel clientChannel, final UDPServerPacket packet) {
        LOGGER.debug("[UDP-Msg].traceId:{},packet:{},channel:{}.begin to handle.", packet.getTraceId(), packet, clientChannel);
        ClientChannelNettyImpl clientChannelNetty = (ClientChannelNettyImpl) clientChannel;
        String userId = clientOnclientManager.getUserByChannel(clientChannelNetty.getChannel());
        eventPublisher.send(EventPath.USER_CALL_UDP_SERVER_RECEIVE, userId, packet);
    }
}
