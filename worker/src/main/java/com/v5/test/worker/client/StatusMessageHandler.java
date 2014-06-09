package com.v5.test.worker.client;

import com.v5.base.client.ClientChannel;
import com.v5.base.event.EventPublisher;
import com.v5.base.handler.AbstractPacketHandler;
import com.v5.base.handler.Logined;
import com.v5.test.worker.constant.EventPath;
import com.v5.test.worker.packet.StatusResponsePackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Logined
public class StatusMessageHandler extends AbstractPacketHandler<StatusResponsePackage> {

    private static Logger log = LoggerFactory.getLogger(com.v5.base.message.text.StatusMessageHandler.class);


    @Autowired
    private EventPublisher eventPublisher;

    public StatusMessageHandler() {
        this.setSupportPacketTypes(StatusResponsePackage.STATUS_MESSAGE_RESPONSE_PACKET_TYPE);
    }

    @Override
    public void handler(ClientChannel clientChannel, StatusResponsePackage packet) {
        log.debug("[msg-status]traceId:{},packet:{},channel:{}",packet.getTraceId(),packet,clientChannel);

        eventPublisher.send(EventPath.USER_MSG_STATUS,packet);

    }

}
