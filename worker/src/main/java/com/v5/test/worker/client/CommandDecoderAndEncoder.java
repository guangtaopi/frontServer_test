package com.v5.test.worker.client;

import cn.v5.common.utils.IPUtil;
import com.v5.base.message.command.*;
import com.v5.base.packet.PacketDecoder;
import com.v5.base.packet.PacketEncoder;
import com.v5.base.packet.PacketHead;
import com.v5.base.utils.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class CommandDecoderAndEncoder implements PacketDecoder<CommandPacket>, PacketEncoder<CommandPacket> {

    private static Logger log = LoggerFactory.getLogger(com.v5.base.message.command.CommandDecoderAndEncoder.class);

    @Override
    public boolean isSupport(ByteBuf buf, PacketHead head) {
        return CommandPacket.COMMAND_PACKET_TYPE == head.getPacketType();
    }

    @Override
    public int[] getSupportPacketType() {
        int[] types = new int[1];
        types[0] = CommandPacket.COMMAND_PACKET_TYPE;
        return types;
    }

    @Override
    /**
     * 需要考虑作为服务端和客户端的统一编解码
     */
    public void encode(CommandPacket msg, ByteBuf buf) {

        switch (msg.getPacketType()) {

            case LoginPacket.COMMAND_LOGIN_PACKET_TYPE: {
                log.debug("encode login packet.");
                LoginPacket loginPacket = (LoginPacket) msg;
                buf.writeByte(LoginPacket.COMMAND_LOGIN_TYPE);
                ByteBufUtils.writeUTF8String(buf, loginPacket.getSessionId());
                break;
            }

            case LoginResponsePacket.COMMAND_LOGIN_RESPONSE_PACKET_TYPE: {
                log.debug("encode login response packet.");
                LoginResponsePacket loginResponsePacket = (LoginResponsePacket) msg;
                buf.writeByte(LoginResponsePacket.COMMAND_LOGIN_RESPONSE_TYPE);
                buf.writeByte(loginResponsePacket.getLoginStatus().getStatus());
                break;
            }

            case HeartbeatPacket.COMMAND_HEARTBEAT_PACKET_TYPE: {
                buf.writeByte(HeartbeatPacket.COMMAND_HEARTBEAT_TYPE);
                break;
            }

            case HeartbeatResponsePacket.COMMAND_HEARTBEAT_RESPONSE_PACKET_TYPE: {
                buf.writeByte(HeartbeatResponsePacket.COMMAND_HEARTBEAT_RESPONSE_TYPE);
                HeartbeatResponsePacket responsePacket = (HeartbeatResponsePacket)msg;
                buf.writeLong(responsePacket.getHeartBeatSentTime());
                break;
            }

            case CallPacket.COMMAND_CALL_PACKET_TYPE: {
                CallPacket callPacket = (CallPacket) msg;
                buf.writeByte(CallPacket.COMMAND_CALL_TYPE);
                buf.writeByte(callPacket.getCallStatus().id());
                ByteBufUtils.writeUTF8String(buf, callPacket.getPeerName());
                if(msg instanceof CallAcceptPacket){
                    CallAcceptPacket callAcceptPacket = (CallAcceptPacket)msg;
                    buf.writeInt(callAcceptPacket.getUdpHost());
                    buf.writeShort(callAcceptPacket.getUdpPort());
                }
                break;
            }

            case CallResponsePacket.COMMAND_CALL_RESPONSE_PACKET_TYPE: {
                CallResponsePacket callResponsePacket = (CallResponsePacket)msg;
                buf.writeByte(CallPacket.COMMAND_CALL_RESPONSE_TYPE);
                buf.writeByte(callResponsePacket.getCallStatus().id());
                ByteBufUtils.writeUTF8String(buf, callResponsePacket.getPeerName());
                break;
            }

            case UDPServerPacket.COMMAND_UDP_PACKET_TYPE: {
                UDPServerPacket serverPacket = (UDPServerPacket) msg;
                buf.writeByte(UDPServerPacket.COMMAND_UDP_TYPE);
                buf.writeInt(IPUtil.ip2int(serverPacket.getIp()));
                buf.writeShort(serverPacket.getPort());
                buf.writeByte(serverPacket.getFlag());
                if (null != serverPacket.getRoomId()) {
                    buf.writeBytes(serverPacket.getRoomId().getBytes(Charset.forName("utf-8")));
                }
                break;
            }

            default: {
                log.debug("unknown command packte, cna't encode.");
                break;
            }
        }
    }

    @Override
    public CommandPacket decode(ByteBuf buf, PacketHead head) {
        CommandPacket result = null;
        byte commandType = buf.readByte();

        log.debug("command packet decode for type {}, packet size {}.commandType:{}",
                head.getPacketType(), head.getPacketSize(),commandType);

        switch (commandType) {

            case LoginPacket.COMMAND_LOGIN_TYPE: {
                LoginPacket loginPacket = new LoginPacket();
                loginPacket.setCommandType(commandType);
                loginPacket.setSessionId(ByteBufUtils.readUTF8String(buf, 32));
                loginPacket.setPacketHead(head);
                if(buf.isReadable()){
                    short languageLen = buf.readShort();
                    loginPacket.setLanguage(ByteBufUtils.readUTF8String(buf, languageLen));
                }
                if(buf.isReadable()){
                    short timeZoneLen = buf.readShort();
                    loginPacket.setTimeZone(ByteBufUtils.readUTF8String(buf, timeZoneLen));
                }

                if(buf.isReadable()){
                    short regionCodeLen = buf.readShort();
                    loginPacket.setRegionCode(ByteBufUtils.readUTF8String(buf, regionCodeLen));
                }
                log.debug("login session id {}, packet:{}", loginPacket.getSessionId(), loginPacket);
                result = loginPacket;
                break;
            }

            case LoginResponsePacket.COMMAND_LOGIN_RESPONSE_TYPE: {
                LoginResponsePacket loginResponsePacket = new LoginResponsePacket();
                loginResponsePacket.setCommandType(commandType);
                loginResponsePacket.setLoginStatus(LoginStatus.getInstance(buf.readByte()));
                result = loginResponsePacket;
                break;
            }

            case HeartbeatPacket.COMMAND_HEARTBEAT_TYPE: {
                HeartbeatPacket heartbeatPacket = new HeartbeatPacket();
                heartbeatPacket.setCommandType(commandType);
                result = heartbeatPacket;
                break;
            }

            case HeartbeatResponsePacket.COMMAND_HEARTBEAT_RESPONSE_TYPE: {
                HeartbeatResponsePacket heartbeatResponsePacket = new HeartbeatResponsePacket();
                heartbeatResponsePacket.setCommandType(commandType);
                if(buf.isReadable()){
                    heartbeatResponsePacket.setHeartBeatSentTime(buf.readLong());
                }
                result = heartbeatResponsePacket;
                break;
            }

            case LogoutPacket.COMMAND_LOGOUT_TYPE: {
                LogoutPacket logoutPacket = new LogoutPacket();
                logoutPacket.setCommandType(commandType);
                result = logoutPacket;
                break;
            }

            case CallPacket.COMMAND_CALL_TYPE: {
                CallPacket callPacket = new CallPacket();
                callPacket.setCommandType(commandType);
                callPacket.setCallStatus(CallStatus.getInstance(buf.readByte()));
                callPacket.setPeerName(ByteBufUtils.readUTF8String(buf, 32));

                //判断是否为电话接受包
                if(CallStatus.VIDEO_ACCEPT == callPacket.getCallStatus() || CallStatus.AUDIO_ACCEPT == callPacket.getCallStatus()){
                    CallAcceptPacket callAcceptPacket = new CallAcceptPacket();
                    callAcceptPacket.setCallStatus(callPacket.getCallStatus());
                    callAcceptPacket.setCommandType(callPacket.getCommandType());
                    callAcceptPacket.setPeerName(callPacket.getPeerName());
                    int readIndex = buf.readerIndex();
                    int writeIndex = buf.writerIndex();
                    if(writeIndex - readIndex >= 6){
                        callAcceptPacket.setUdpHost(buf.readInt());
                        callAcceptPacket.setUdpPort(buf.readShort());
                    }
                    callPacket = callAcceptPacket;
                }

                if(CallStatus.RECEIVED == callPacket.getCallStatus()){
                    CallReceivedPacket callReceivedPacket = new CallReceivedPacket();
                    callReceivedPacket.setCallStatus(callPacket.getCallStatus());
                    callReceivedPacket.setCommandType(callPacket.getCommandType());
                    callReceivedPacket.setPeerName(callPacket.getPeerName());
                    int readIndex = buf.readerIndex();
                    int writeIndex = buf.writerIndex();
                    if(writeIndex - readIndex >= 6){
                        callReceivedPacket.setUdpHost(buf.readInt());
                        callReceivedPacket.setUdpPort(buf.readShort());
                    }
                    callPacket = callReceivedPacket;
                }

                result = callPacket;
                break;
            }

            case CallResponsePacket.COMMAND_CALL_RESPONSE_TYPE: {
                CallResponsePacket callResponsePacket = new CallResponsePacket();
                callResponsePacket.setCommandType(commandType);
                callResponsePacket.setCallStatus(CallStatus.getInstance(buf.readByte()));
                callResponsePacket.setPeerName(ByteBufUtils.readUTF8String(buf, 32));
                result = callResponsePacket;
                break;
            }

            //服务端发送的UDP Server包
            case UDPServerPacket.COMMAND_UDP_TYPE:{
                UDPServerPacket udpServerPacket  = new UDPServerPacket();
                udpServerPacket.setIp(IPUtil.int2ip(buf.readInt()));
                udpServerPacket.setPort(buf.readShort());
                udpServerPacket.setFlag(buf.readByte());
                udpServerPacket.setRoomId(ByteBufUtils.readUTF8String(buf,64));
                result = udpServerPacket;
                break;
            }


            default: {
                log.error("unknown command packet, command type {}", commandType);
                break;
            }

        }
        return result;
    }
}
