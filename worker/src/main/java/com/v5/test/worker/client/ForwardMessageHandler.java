package com.v5.test.worker.client;

import com.v5.base.client.ClientChannel;
import com.v5.base.event.EventPublisher;
import com.v5.base.handler.AbstractPacketHandler;
import com.v5.base.handler.Logined;
import com.v5.base.message.text.ForwardMessagePacket;
import com.v5.test.worker.constant.EventPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Logined
public class ForwardMessageHandler extends AbstractPacketHandler<ForwardMessagePacket> {

    private  Logger log = LoggerFactory.getLogger(getClass());


    public ForwardMessageHandler() {
        this.setSupportPacketTypes(ForwardMessagePacket.FORWARD_MESSAGE_PACKET_TYPE);
    }

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private ClientOnclientManager clientOnclientManager;


    @Override
    public void handler(final ClientChannel clientChannel, final ForwardMessagePacket packet) {

        log.debug("receive forward message.packet:"+packet);
        ClientChannelNettyImpl clientChannelNetty = (ClientChannelNettyImpl)clientChannel;
        String receive = clientOnclientManager.getUserByChannel(clientChannelNetty.getChannel());
        String from = packet.getFrom();

        eventPublisher.send(EventPath.USER_TRANS_MSG_RECEIVE,from,receive,packet);


    }

}
