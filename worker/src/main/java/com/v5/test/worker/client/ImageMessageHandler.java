package com.v5.test.worker.client;

import cn.v5.proto.Service;
import com.v5.base.client.ClientChannel;
import com.v5.base.client.OnlineClient;
import com.v5.base.client.OnlineClientManager;
import com.v5.base.constant.EventPath;
import com.v5.base.event.EventPublisher;
import com.v5.base.handler.AbstractPacketHandler;
import com.v5.base.handler.Logined;
import com.v5.base.message.text.ImageMessagePacket;
import com.v5.base.message.text.MessageHelper;
import com.v5.base.message.text.MessageStatus;
import com.v5.base.message.text.SimpleMessagePacket;
import com.v5.base.packet.PacketHead;
import com.v5.base.service.MessageService;
import com.v5.base.service.UserService;
import com.v5.base.utils.AsyncInvokeException;
import com.v5.base.utils.DefaultCallBack;
import com.v5.base.utils.SimpleCallback;
import com.v5.base.utils.UserUtils;
import com.v5.test.worker.service.TcpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Logined
public class ImageMessageHandler extends AbstractPacketHandler<ImageMessagePacket> {
    private static Logger LOGGER = LoggerFactory.getLogger(com.v5.base.message.text.ImageMessageHandler.class);

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private ClientOnclientManager clientOnclientManager;

    @Autowired
    private TcpService tcpService;


    public ImageMessageHandler() {
        this.setSupportPacketTypes(ImageMessagePacket.IMAGE_MESSAGE_PACKET_TYPE);
    }

    @Override
    public void handler(final ClientChannel clientChannel, final ImageMessagePacket packet) {
        LOGGER.debug("[Image-Msg].traceId:{},channel:{},pakcet:{}",packet.getTraceId(),clientChannel,packet);
        ClientChannelNettyImpl clientChannelNetty = (ClientChannelNettyImpl)clientChannel;
        String receive = clientOnclientManager.getUserByChannel(clientChannelNetty.getChannel());
        String from = packet.getFrom();
        eventPublisher.send(com.v5.test.worker.constant.EventPath.USER_IMAGE_MSG_RECEIVE, from, receive, packet, System.currentTimeMillis());
        tcpService.sendAckMessage(receive, packet.getMessageId());
    }

}
