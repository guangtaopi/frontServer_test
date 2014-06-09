package com.v5.test.worker.client;

import com.v5.base.client.ClientChannel;
import com.v5.base.client.NoLoginedException;
import com.v5.base.event.EventPublisher;
import com.v5.base.handler.PacketHandlerManager;
import com.v5.base.message.command.HeartbeatPacket;
import com.v5.base.packet.BasePacket;
import com.v5.base.packet.PacketDecoderAndEncoderManager;
import com.v5.base.packet.PacketHead;
import com.v5.test.worker.constant.EventPath;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by piguangtao on 14-2-19.
 */
public class ClientPacketDecodeAndEncodeHandler extends MessageToMessageCodec<ByteBuf, BasePacket> {
    private static Logger LOGGER = LoggerFactory.getLogger(ClientPacketDecodeAndEncodeHandler.class);

    private PacketDecoderAndEncoderManager decoderAndEncoderManager;
    private PacketHandlerManager packetHandlerManager;
    private ClientOnclientManager onlineClientManager;
    private EventPublisher eventPublisher;

    ClientChannel clientChannel;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        clientChannel = new ClientChannelNettyImpl(ctx);

        eventPublisher.send(EventPath.USER_TCP_CONNECTED, ctx);

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        LOGGER.debug("user event trigger. channel:{}", ctx.channel(), evt);
        // 客户端心跳
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                 ctx.channel().writeAndFlush(new HeartbeatPacket());
            }
        }
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        eventPublisher.send(EventPath.USER_TCP_DISCONNECTED,onlineClientManager.getUserByChannel(ctx.channel()));
        super.channelInactive(ctx);
        clientChannel.destory();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, BasePacket msg, List out) throws Exception {
        LOGGER.debug("encode packet for type {}:{}", msg.getPacketType() % 256, msg.getPacketType() >> 8);
        ByteBuf buf = Unpooled.buffer();
        PacketHead head = msg.getPacketHead();
        if (head == null) {
            head = new PacketHead();
            msg.setPacketHead(head);
        }

        if (null != clientChannel.getOnlineClient()) {
            head.setAppId(clientChannel.getOnlineClient().getUserInfo().getAppId());
            head.setVersion(clientChannel.getOnlineClient().getUserInfo().getVersion());
        }

        decoderAndEncoderManager.encode(msg, buf);
        out.add(buf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List out) throws Exception {
        try {
            clientChannel.setAttribute("channel",ctx.channel());
            PacketHead head = decodePacketHead(buf);
            BasePacket packet = decoderAndEncoderManager.decode(buf, head);
            if (packet != null) {

                packetHandlerManager.processPacket(clientChannel, packet);
            } else {
                LOGGER.warn("not found decode for packet type {}", head.getPacketType());
            }
        } catch (IndexOutOfBoundsException e) {
            LOGGER.error("decode error: {}", e);
            ctx.close();
        } catch (NoLoginedException e) {
            LOGGER.error("no logined, close.",e);
            ctx.close();
        }
    }

    public static final PacketHead decodePacketHead(ByteBuf buf) {
        PacketHead packet = new PacketHead();

        packet.setHead(buf.readByte());
        packet.setVersion(buf.readByte());
        packet.setPacketType(buf.readByte());
        packet.setSecret(buf.readByte());
        long g = ((long)buf.readUnsignedShort()) << 32;
        packet.setTimestamp(buf.readInt() + g);
        packet.setTempId(buf.readUnsignedShort());
        packet.setPacketSize(buf.readUnsignedShort());
        packet.setAppId(buf.readShort());

        return packet;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//        eventPublisher.send(EventPath.USER_TCP_DISCONNECTED,onlineClientManager.getUserByChannel(ctx.channel()));
        LOGGER.warn("Unexpected exception from downstream.channel:{}",ctx.channel(), cause);
        ctx.close();
        clientChannel.destory();
    }

    public void setDecoderAndEncoderManager(PacketDecoderAndEncoderManager decoderAndEncoderManager) {
        this.decoderAndEncoderManager = decoderAndEncoderManager;
    }

    public void setPacketHandlerManager(PacketHandlerManager packetHandlerManager) {
        this.packetHandlerManager = packetHandlerManager;
    }

    public void setOnlineClientManager(ClientOnclientManager onlineClientManager) {
        this.onlineClientManager = onlineClientManager;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}
