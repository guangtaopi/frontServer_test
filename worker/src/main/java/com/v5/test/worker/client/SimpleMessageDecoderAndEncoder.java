package com.v5.test.worker.client;

import cn.v5.common.utils.MD5;
import com.v5.base.message.text.*;
import com.v5.base.packet.PacketDecoder;
import com.v5.base.packet.PacketEncoder;
import com.v5.base.packet.PacketHead;
import com.v5.base.utils.ByteBufUtils;
import com.v5.test.worker.packet.StatusResponsePackage;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleMessageDecoderAndEncoder implements PacketDecoder<SimpleMessagePacket>, PacketEncoder<SimpleMessagePacket> {

    private static Logger log = LoggerFactory.getLogger(com.v5.base.message.text.SimpleMessageDecoderAndEncoder.class);

    @Override
    public boolean isSupport(ByteBuf buf, PacketHead head) {
        return SimpleMessagePacket.SIMPLE_MESSAGE_PACKET_TYPE == head.getPacketType();
    }

    @Override
    public int[] getSupportPacketType() {
        int[] types = new int[1];
        types[0] = SimpleMessagePacket.SIMPLE_MESSAGE_PACKET_TYPE;
        return types;
    }

    @Override
    public void encode(SimpleMessagePacket msg, ByteBuf buf) {
        switch (msg.getPacketType()) {
            case TextMessagePacket.TEXT_MESSAGE_PACKET_TYPE: {
                buf.writeByte(TextMessagePacket.TEXT_MESSAGE_TYPE);
                //发给个人消息
                if (msg.getFromGroup() == null) {
                    buf.writeByte(TextMessagePacket.TO_USER);
                    ByteBufUtils.writeUTF8String(buf, msg.getToUser());
                } else {
                    //发送群组消息
                    buf.writeByte(TextMessagePacket.TO_GROUP);
                    ByteBufUtils.writeUTF8String(buf, msg.getFromGroup());
                    ByteBufUtils.writeUTF8String(buf, msg.getFrom());
                }
                ByteBufUtils.writeByteArrayPrefix2ByteLength(buf, msg.getContent());
                buf.writeByte(0x00);
                ByteBufUtils.writeByteArrayPrefix2ByteLength(buf, MD5.getMD5(String.valueOf(System.currentTimeMillis())).getBytes());
                break;
            }

            case ImageMessagePacket.IMAGE_MESSAGE_PACKET_TYPE: {
                buf.writeByte(ImageMessagePacket.IMAGE_URL_MESSAGE_TYPE);
                if (msg.getFromGroup() == null) {
                    buf.writeByte(ImageMessagePacket.TO_USER);
                    ByteBufUtils.writeUTF8String(buf, msg.getToUser());
                } else {
                    buf.writeByte(ImageMessagePacket.TO_GROUP);
                    ByteBufUtils.writeUTF8String(buf, msg.getFromGroup());
                    ByteBufUtils.writeUTF8String(buf, msg.getFrom());
                }
                ByteBufUtils.writeByteArrayPrefix2ByteLength(buf, msg.getContent());
                buf.writeByte(0x00);
                ByteBufUtils.writeByteArrayPrefix2ByteLength(buf, MD5.getMD5(String.valueOf(System.currentTimeMillis())).getBytes());
                break;
            }

            case VoiceMessagePacket.VOICE_MESSAGE_PACKET_TYPE:{
                buf.writeByte(VoiceMessagePacket.VOICE_MESSAGE_TYPE);
                if (msg.getFromGroup() == null) {
                    buf.writeByte(VoiceMessagePacket.TO_USER);
                    ByteBufUtils.writeUTF8String(buf, msg.getFrom());
                } else {
                    buf.writeByte(VoiceMessagePacket.TO_GROUP);
                    ByteBufUtils.writeUTF8String(buf, msg.getFromGroup());
                    ByteBufUtils.writeUTF8String(buf, msg.getFrom());
                }
                ByteBufUtils.writeByteArrayPrefix2ByteLength(buf, msg.getContent());
                buf.writeByte(0x00);
                ByteBufUtils.writeByteArrayPrefix2ByteLength(buf, MD5.getMD5(String.valueOf(System.currentTimeMillis())).getBytes());
                break;
            }

            case VideoMessagePacket.VIDEO_MESSAGE_PACKET_TYPE:{
                buf.writeByte(VideoMessagePacket.VIDEO_MESSAGE_TYPE);
                if (msg.getFromGroup() == null) {
                    buf.writeByte(VideoMessagePacket.TO_USER);
                    ByteBufUtils.writeUTF8String(buf, msg.getFrom());
                } else {
                    buf.writeByte(VideoMessagePacket.TO_GROUP);
                    ByteBufUtils.writeUTF8String(buf, msg.getFromGroup());
                    ByteBufUtils.writeUTF8String(buf, msg.getFrom());
                }
//                ByteBufUtils.writeUTF8StringPrefix2ByteLength(buf, msg.getContent());
                ByteBufUtils.writeByteArrayPrefix2ByteLength(buf, msg.getContent());
                if(null != msg.getMessageId()){
                    buf.writeLong(msg.getMessageId());
                }
                break;
            }

            case MessageResponsePacket.MESSAGE_RESPONSE_PACKET_TYPE: {
                MessageResponsePacket messageResponsePacket = (MessageResponsePacket) msg;
                PacketHead head = new PacketHead();
                head.setTempId(messageResponsePacket.getTempId());
                messageResponsePacket.setPacketHead(head);
                buf.writeByte(MessageResponsePacket.STATUS_RESPONSE_MESSAGE_TYPE);
                buf.writeByte(messageResponsePacket.getMessageStatus().id());
                buf.writeLong(messageResponsePacket.getMessageId());
                break;
            }

            case ForwardMessagePacket.FORWARD_MESSAGE_PACKET_TYPE: {
                ForwardMessagePacket forwardMessagePacket = (ForwardMessagePacket) msg;
                buf.writeByte(ForwardMessagePacket.FORWARD_MESSAGE_TYPE);
                buf.writeByte(forwardMessagePacket.isBoth() ? 0x02 : 0x01);
                ByteBufUtils.writeUTF8String(buf, forwardMessagePacket.getFrom());
                ByteBufUtils.writeByteArrayPrefix2ByteLength(buf, forwardMessagePacket.getData());
                break;
            }
            case StatusMessagePacket.STATUS_MESSAGE_PACKET_TYPE: {
                StatusMessagePacket statusMessagePacket = (StatusMessagePacket)msg;
                buf.writeByte(0x10);
                buf.writeByte(statusMessagePacket.getMessageStatus().id());
                buf.writeLong(statusMessagePacket.getMessageId());
                break;
            }

            default: {
                log.debug("unknown simple message packet type {}", msg.getPacketType());
                break;
            }
        }
    }

    @Override
    public SimpleMessagePacket decode(ByteBuf buf, PacketHead head) {
        log.debug("simple message packet decode.");

        SimpleMessagePacket result = null;

        byte messageType = buf.readByte();
        byte messageServiceType = buf.readByte();
        log.debug("message type {}, message service type {}", messageType, messageServiceType);
        switch (messageType) {
            case TextMessagePacket.TEXT_MESSAGE_TYPE: {
                log.debug("decode text message.");
                TextMessagePacket textMessagePacket = new TextMessagePacket();
                textMessagePacket.setMessageType(messageType);
                textMessagePacket.setMessageServiceType(messageServiceType);
                if (TextMessagePacket.TO_USER == messageServiceType) {
                    String fromUser = ByteBufUtils.readUTF8String(buf, 32);
                    int len = buf.readUnsignedShort();
                    byte[] content = ByteBufUtils.readByteArray(buf, len);
                    textMessagePacket.setFrom(fromUser);
                    textMessagePacket.setContent(content);
                    textMessagePacket.setMessageId(buf.readLong());

                    result = textMessagePacket;
                } else if (TextMessagePacket.TO_GROUP == messageServiceType) {
                    String fromGroup = ByteBufUtils.readUTF8String(buf, 32);
                    String fromUser = ByteBufUtils.readUTF8String(buf, 32);
                    int len = buf.readUnsignedShort();
                    byte[] content = ByteBufUtils.readByteArray(buf, len);
                    textMessagePacket.setFrom(fromUser);
                    textMessagePacket.setFromGroup(fromGroup);
                    textMessagePacket.setToGroup(fromGroup);
                    textMessagePacket.setContent(content);
                    textMessagePacket.setMessageId(buf.readLong());
                    result = textMessagePacket;
                } else {
                    log.error("unknown message service type {}", messageServiceType);
                }
                result.setPacketHead(head);
                result.setTempId(head.getTempId());
                break;
            }

            case ImageMessagePacket.IMAGE_URL_MESSAGE_TYPE: {
                log.debug("decode image url message.");
                ImageMessagePacket imageMessagePacket = new ImageMessagePacket();
                imageMessagePacket.setMessageType(messageType);
                imageMessagePacket.setMessageServiceType(messageServiceType);
                if (ImageMessagePacket.TO_USER == messageServiceType) {
                    String fromUser = ByteBufUtils.readUTF8String(buf, 32);
                    int len = buf.readUnsignedShort();
                    byte[] content = ByteBufUtils.readByteArray(buf, len);
                    imageMessagePacket.setFrom(fromUser);
                    imageMessagePacket.setContent(content);
                    imageMessagePacket.setMessageId(buf.readLong());
                    result = imageMessagePacket;
                } else if (ImageMessagePacket.TO_GROUP == messageServiceType) {
                    String fromGroup = ByteBufUtils.readUTF8String(buf, 32);
                    String fromUser = ByteBufUtils.readUTF8String(buf, 32);
                    int len = buf.readUnsignedShort();
                    byte[] content = ByteBufUtils.readByteArray(buf, len);
                    imageMessagePacket.setFromGroup(fromGroup);
                    imageMessagePacket.setToGroup(fromGroup);
                    imageMessagePacket.setFrom(fromUser);
                    imageMessagePacket.setContent(content);
                    imageMessagePacket.setMessageId(buf.readLong());
                    result = imageMessagePacket;
                } else {
                    log.error("unknown image url message service type {}", messageServiceType);
                }
                result.setPacketHead(head);
                result.setTempId(head.getTempId());
                break;
            }

            case VoiceMessagePacket.VOICE_MESSAGE_TYPE:{
               log.debug("decode voice message");
                VoiceMessagePacket voicePacket = new VoiceMessagePacket();
                voicePacket.setMessageType(messageType);
                voicePacket.setMessageServiceType(messageServiceType);
                if (VoiceMessagePacket.TO_USER == messageServiceType) {
                    String fromUser = ByteBufUtils.readUTF8String(buf, 32);
                    int len = buf.readUnsignedShort();
                    byte[] content = ByteBufUtils.readByteArray(buf, len);
                    voicePacket.setFrom(fromUser);
                    voicePacket.setContent(content);
                    voicePacket.setMessageId(buf.readLong());
                    result = voicePacket;
                } else if (VoiceMessagePacket.TO_GROUP == messageServiceType) {
                    String fromGroup = ByteBufUtils.readUTF8String(buf, 32);
                    String fromUser = ByteBufUtils.readUTF8String(buf, 32);
                    int len = buf.readUnsignedShort();
                    byte[] content = ByteBufUtils.readByteArray(buf, len);
                    voicePacket.setFromGroup(fromGroup);
                    voicePacket.setToGroup(fromGroup);
                    voicePacket.setContent(content);
                    voicePacket.setFrom(fromUser);
                    voicePacket.setMessageId(buf.readLong());
                    result = voicePacket;
                } else {
                    log.error("unknown voice message service type {}", messageServiceType);
                }
                result.setPacketHead(head);
                result.setTempId(head.getTempId());
                break;
            }

            case VideoMessagePacket.VIDEO_MESSAGE_TYPE:{
                log.debug("decode voice message");
                VideoMessagePacket videoPacket = new VideoMessagePacket();
                videoPacket.setMessageType(messageType);
                videoPacket.setMessageServiceType(messageServiceType);
                if (VoiceMessagePacket.TO_USER == messageServiceType) {
                    String fromUser = ByteBufUtils.readUTF8String(buf, 32);
                    int len = buf.readUnsignedShort();
                    byte[] content = ByteBufUtils.readByteArray(buf, len);
                    videoPacket.setToUser(fromUser);
                    videoPacket.setContent(content);
                    videoPacket.setMessageId(buf.readLong());
                    result = videoPacket;
                } else if (VoiceMessagePacket.TO_GROUP == messageServiceType) {
                    String fromGroup = ByteBufUtils.readUTF8String(buf, 32);
                    String fromUser = ByteBufUtils.readUTF8String(buf, 32);
                    int len = buf.readUnsignedShort();
                    byte[] content = ByteBufUtils.readByteArray(buf, len);
                    videoPacket.setFromGroup(fromGroup);
                    videoPacket.setToGroup(fromGroup);
                    videoPacket.setContent(content);
                    videoPacket.setFrom(fromUser);
                    videoPacket.setMessageId(buf.readLong());
                    result = videoPacket;
                } else {
                    log.error("unknown video url message service type {}", messageServiceType);
                }
                result.setPacketHead(head);
                result.setTempId(head.getTempId());
                break;
            }

            case ForwardMessagePacket.FORWARD_MESSAGE_TYPE: {
                ForwardMessagePacket forwardMessagePacket = new ForwardMessagePacket();
                forwardMessagePacket.setMessageType(messageType);
                forwardMessagePacket.setMessageServiceType(messageServiceType);

                String fromUser = ByteBufUtils.readUTF8String(buf, 32);
                int len = buf.readUnsignedShort();
                byte[] data = ByteBufUtils.readByteArray(buf, len);

                forwardMessagePacket.setFrom(fromUser);
                forwardMessagePacket.setData(data);
                forwardMessagePacket.setBoth(messageServiceType == 0x02);
                result = forwardMessagePacket;
                result.setPacketHead(head);
                result.setTempId(head.getTempId());
                break;
            }

            case StatusMessagePacket.STATUS_RESPONSE_MESSAGE_TYPE: {
                StatusResponsePackage statusMessagePacket = new StatusResponsePackage();
                statusMessagePacket.setMessageType(messageType);
                statusMessagePacket.setMessageServiceType(messageServiceType);

                statusMessagePacket.setMessageStatus(MessageStatus.getInstance(messageServiceType));
                statusMessagePacket.setMessageId(buf.readLong());
                result = statusMessagePacket;
                result.setPacketHead(head);
                result.setTempId(head.getTempId());
                break;
            }

            default: {
                log.error("unknown simple message packet, message type {},", messageType);
                break;
            }
        }
        return result;
    }
}
