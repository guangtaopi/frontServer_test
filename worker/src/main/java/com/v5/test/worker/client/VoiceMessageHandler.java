package com.v5.test.worker.client;

import com.v5.base.client.ClientChannel;
import com.v5.base.event.EventPublisher;
import com.v5.base.handler.AbstractPacketHandler;
import com.v5.base.handler.Logined;
import com.v5.base.message.text.TextMessageHandler;
import com.v5.base.message.text.VoiceMessagePacket;
import com.v5.test.worker.service.TcpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: piguangtao
 * Date: 14-1-13
 * Time: 下午6:37
 * To change this template use File | Settings | File Templates.
 */
@Logined
public class VoiceMessageHandler extends AbstractPacketHandler<VoiceMessagePacket> {

    private static Logger LOGGER = LoggerFactory.getLogger(TextMessageHandler.class);

    public VoiceMessageHandler() {
        this.setSupportPacketTypes(VoiceMessagePacket.VOICE_MESSAGE_PACKET_TYPE);
    }

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private TcpService tcpService;

    @Autowired
    private ClientOnclientManager clientOnclientManager;

    @Override
    public void handler(final ClientChannel clientChannel, final VoiceMessagePacket packet) {
        LOGGER.debug("[Text-Msg].traceId:{},packet:{},channel:{}.begin to handle.", packet.getTraceId(), packet, clientChannel);
        ClientChannelNettyImpl clientChannelNetty = (ClientChannelNettyImpl)clientChannel;
        String receive = clientOnclientManager.getUserByChannel(clientChannelNetty.getChannel());
        String from = packet.getFrom();
        eventPublisher.send(com.v5.test.worker.constant.EventPath.USER_AUDIO_MSG_RECEIVE,from,receive,packet,System.currentTimeMillis());
        tcpService.sendAckMessage(receive, packet.getMessageId());
    }
}
