package com.v5.test.worker.client;

import com.v5.base.client.ClientChannel;
import com.v5.base.event.EventPublisher;
import com.v5.base.handler.AbstractPacketHandler;
import com.v5.base.message.command.LoginResponsePacket;
import com.v5.test.worker.constant.EventPath;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by piguangtao on 14-2-19.
 */
public class LoginHandler extends AbstractPacketHandler<LoginResponsePacket> {

    private static Logger LOGGER = LoggerFactory.getLogger(LoginHandler.class);

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private ClientOnclientManager onclientManager;

    public LoginHandler() {
        this.setSupportPacketTypes(LoginResponsePacket.COMMAND_LOGIN_RESPONSE_PACKET_TYPE);
    }

    @Override
    public void handler(ClientChannel clientChannel, LoginResponsePacket packet) {
        LOGGER.debug("[login response].packet:{},clientChannel:{}",packet,clientChannel);
        switch (packet.getLoginStatus()) {
            case SUCCESS:
                Channel channel = (Channel) clientChannel.getAttribute("channel");
                eventPublisher.send(EventPath.USER_TCP_LOGIN_SUCCESS, onclientManager.getUserByChannel(channel),clientChannel);
                break;
            default: {
                LOGGER.error("[tcp-login] fail. channel:{}",clientChannel);
                clientChannel.close();
                break;
            }

        }
    }
}
