package com.v5.test.worker.client;

import com.v5.base.client.ClientChannel;
import com.v5.base.handler.AbstractPacketHandler;
import com.v5.base.handler.Logined;
import com.v5.base.message.text.ForwardMessagePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Logined
public class ForwardMessageHandler extends AbstractPacketHandler<ForwardMessagePacket> {

    private  Logger log = LoggerFactory.getLogger(getClass());


    public ForwardMessageHandler() {
        this.setSupportPacketTypes(ForwardMessagePacket.FORWARD_MESSAGE_PACKET_TYPE);
    }

    @Override
    public void handler(final ClientChannel clientChannel, final ForwardMessagePacket packet) {

        log.debug("receive forward message.packet:"+packet);

    }

}
