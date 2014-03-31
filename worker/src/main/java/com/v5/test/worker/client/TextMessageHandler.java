package com.v5.test.worker.client;

import com.v5.base.client.ClientChannel;
import com.v5.base.event.EventPublisher;
import com.v5.base.handler.AbstractPacketHandler;
import com.v5.base.handler.Logined;
import com.v5.base.message.text.TextMessagePacket;
import com.v5.test.worker.constant.EventPath;
import com.v5.test.worker.service.TcpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.Charset;

/**
 * 记录消息的延迟
 */
@Logined
public class TextMessageHandler extends AbstractPacketHandler<TextMessagePacket> {

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private TcpService tcpService;

    @Autowired
    private ClientOnclientManager clientOnclientManager;

    private static Logger LOGGER = LoggerFactory.getLogger(com.v5.base.message.text.TextMessageHandler.class);

    public TextMessageHandler() {
        this.setSupportPacketTypes(TextMessagePacket.TEXT_MESSAGE_PACKET_TYPE);
    }

    @Override
    public void handler(final ClientChannel clientChannel, final TextMessagePacket packet) {
        LOGGER.debug("[Text-Msg].traceId:{},packet:{},channel:{}.begin to handle.", packet.getTraceId(), packet, clientChannel);
        ClientChannelNettyImpl clientChannelNetty = (ClientChannelNettyImpl)clientChannel;
        String receive = clientOnclientManager.getUserByChannel(clientChannelNetty.getChannel());
        String from = packet.getFrom();
        String content = new String(packet.getContent(), Charset.forName("utf-8"));
        eventPublisher.send(EventPath.USER_MSG_RECEIVE,from,receive,content,System.currentTimeMillis());
        tcpService.sendAckMessage(from, packet.getMessageId());
    }
}
