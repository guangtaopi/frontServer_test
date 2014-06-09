package com.v5.test.worker.client;

import com.v5.base.client.ClientChannel;
import com.v5.base.event.EventPublisher;
import com.v5.base.handler.AbstractPacketHandler;
import com.v5.base.message.notify.SystemNotifyPackage;
import com.v5.test.worker.constant.EventPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by piguangtao on 14-4-8.
 */
public class SystemNotifyHandler extends AbstractPacketHandler<SystemNotifyPackage> {

    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    public SystemNotifyHandler() {
        this.setSupportPacketTypes(SystemNotifyPackage.SYSTEM_NOTIFY_PACKAGE_TYPE);
    }
    @Autowired
    private ClientOnclientManager clientOnclientManager;

    @Autowired
    private EventPublisher eventPublisher;


    @Override
    public void handler(final ClientChannel clientChannel, final SystemNotifyPackage packet) {
        LOGGER.debug("[Text-Msg].traceId:{},packet:{},channel:{}.begin to handle.", packet.getTraceId(), packet, clientChannel);
        ClientChannelNettyImpl clientChannelNetty = (ClientChannelNettyImpl)clientChannel;
        String receive = clientOnclientManager.getUserByChannel(clientChannelNetty.getChannel());
        eventPublisher.send(EventPath.USER_SYSTEM_NOTIFY_RECEIVE,receive,packet);
    }
}
