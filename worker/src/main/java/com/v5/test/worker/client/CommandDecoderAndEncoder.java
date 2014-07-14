package com.v5.test.worker.client;

import cn.v5.common.utils.IPUtil;
import com.v5.base.message.command.*;
import com.v5.base.packet.PacketDecoder;
import com.v5.base.packet.PacketEncoder;
import com.v5.base.packet.PacketHead;
import com.v5.base.utils.ByteBufUtils;
import com.v5.test.worker.client.gameCall.*;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

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
                HeartbeatResponsePacket responsePacket = (HeartbeatResponsePacket) msg;
                buf.writeLong(responsePacket.getHeartBeatSentTime());
                break;
            }

            case CallPacket.COMMAND_CALL_PACKET_TYPE: {
                CallPacket callPacket = (CallPacket) msg;
                buf.writeByte(CallPacket.COMMAND_CALL_TYPE);
                buf.writeByte(callPacket.getCallStatus().id());
                ByteBufUtils.writeUTF8String(buf, callPacket.getPeerName());
                if (msg instanceof CallAcceptPacket) {
                    CallAcceptPacket callAcceptPacket = (CallAcceptPacket) msg;
                    buf.writeInt(callAcceptPacket.getUdpHost());
                    buf.writeShort(callAcceptPacket.getUdpPort());
                }
                break;
            }

            case CallResponsePacket.COMMAND_CALL_RESPONSE_PACKET_TYPE: {
                CallResponsePacket callResponsePacket = (CallResponsePacket) msg;
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

            case GameCallReqPacket.COMMAND_GAME_CALL_REQ_PACKET_TYPE: {
                GameCallReqPacket reqPacket = (GameCallReqPacket) msg;
                encodeGameCallReqPacket(buf, reqPacket);
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
                head.getPacketType(), head.getPacketSize(), commandType);

        switch (commandType) {

            case LoginPacket.COMMAND_LOGIN_TYPE: {
                LoginPacket loginPacket = new LoginPacket();
                loginPacket.setCommandType(commandType);
                loginPacket.setSessionId(ByteBufUtils.readUTF8String(buf, 32));
                loginPacket.setPacketHead(head);
                if (buf.isReadable()) {
                    short languageLen = buf.readShort();
                    loginPacket.setLanguage(ByteBufUtils.readUTF8String(buf, languageLen));
                }
                if (buf.isReadable()) {
                    short timeZoneLen = buf.readShort();
                    loginPacket.setTimeZone(ByteBufUtils.readUTF8String(buf, timeZoneLen));
                }

                if (buf.isReadable()) {
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
                if (buf.isReadable()) {
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
                if (CallStatus.VIDEO_ACCEPT == callPacket.getCallStatus() || CallStatus.AUDIO_ACCEPT == callPacket.getCallStatus()) {
                    CallAcceptPacket callAcceptPacket = new CallAcceptPacket();
                    callAcceptPacket.setCallStatus(callPacket.getCallStatus());
                    callAcceptPacket.setCommandType(callPacket.getCommandType());
                    callAcceptPacket.setPeerName(callPacket.getPeerName());
                    int readIndex = buf.readerIndex();
                    int writeIndex = buf.writerIndex();
                    if (writeIndex - readIndex >= 6) {
                        callAcceptPacket.setUdpHost(buf.readInt());
                        callAcceptPacket.setUdpPort(buf.readShort());
                    }
                    callPacket = callAcceptPacket;
                }

                if (CallStatus.RECEIVED == callPacket.getCallStatus()) {
                    CallReceivedPacket callReceivedPacket = new CallReceivedPacket();
                    callReceivedPacket.setCallStatus(callPacket.getCallStatus());
                    callReceivedPacket.setCommandType(callPacket.getCommandType());
                    callReceivedPacket.setPeerName(callPacket.getPeerName());
                    int readIndex = buf.readerIndex();
                    int writeIndex = buf.writerIndex();
                    if (writeIndex - readIndex >= 6) {
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
            case UDPServerPacket.COMMAND_UDP_TYPE: {
                UDPServerPacket udpServerPacket = new UDPServerPacket();
                udpServerPacket.setIp(IPUtil.int2ip(buf.readInt()));
                udpServerPacket.setPort(buf.readShort());
                udpServerPacket.setFlag(buf.readByte());
                udpServerPacket.setRoomId(ByteBufUtils.readUTF8String(buf, 64));
                result = udpServerPacket;
                break;
            }

            case GameCallRespPacket.COMMAND_GAME_CALL_RESPONSE_TYPE: {
                log.debug("begin to decode game call resp packet.");
                result = decodeGameCall(buf, head);
                break;
            }

            case GameServerRespPacket.COMMAND_GAME_SERVER_RESP_TYPE: {
                log.debug("begin to decode game server resp packet.");
                result = decodeGameServer(buf, head);
                break;
            }

            default: {
                log.error("unknown command packet, command type {}", commandType);
                break;
            }

        }
        return result;
    }


    private void encodeGameCallReqPacket(ByteBuf buf, GameCallReqPacket reqPacket) {
        buf.writeByte(GameCallReqPacket.COMMAND_GAME_CALL_TYPE);
        buf.writeByte(reqPacket.getCallType().getValue());
        buf.writeBytes(reqPacket.getPeerName().getBytes(Charset.forName("utf-8")));
        buf.writeBytes(reqPacket.getSsrc());
        if (null != reqPacket.getSubCallTypes()) {
            buf.writeShort(reqPacket.getSubCallTypes().length * 4);
            for (int i = 0; i < reqPacket.getSubCallTypes().length; i++) {
                buf.writeInt(reqPacket.getSubCallTypes()[i]);
            }
        } else {
            buf.writeShort(0);
        }
        GameCallPacket.GameRoom[] gameRooms = reqPacket.getGameRooms();

        short dataLength1 = null == gameRooms ? 0 : (short) (gameRooms.length * (2 + 64 + 4));
        short dataLength2 = 0;
        //huandup采用data length2
        GameCallHandupReqPacket.DesCode descCode = null;
        if (reqPacket instanceof GameCallHandupReqPacket) {
            GameCallHandupReqPacket gameCallHandupReqPacket = (GameCallHandupReqPacket) reqPacket;
            descCode = gameCallHandupReqPacket.getDesCode();
            if (null != descCode) {
                dataLength2 = 1;
            }
        }

        short dataLength;
        if (dataLength2 > 0) {
            dataLength = (short) (2 + dataLength1 + 2 + dataLength2);
        } else if (dataLength1 > 0) {
            dataLength = (short) (2 + dataLength1);
        } else {
            dataLength = 0;
        }

        if (dataLength > 0) {
            //data length
            buf.writeShort(dataLength);
            //data length1
            buf.writeShort(dataLength1);

            if(dataLength1 > 0){
                for (GameCallPacket.GameRoom gameRoom : gameRooms) {
                    buf.writeShort(64);
                    buf.writeInt(gameRoom.getGameId());
                    buf.writeBytes(gameRoom.getRoomId().getBytes(Charset.forName("utf-8")));
                }
            }
            if (dataLength2 > 0) {
                buf.writeShort(1);
                buf.writeByte(descCode.getValue());
            }
        } else {
            buf.writeShort(0);
        }
        byte[] extraData = reqPacket.getExtraData();
        if (null != extraData && extraData.length > 0) {
            buf.writeShort(extraData.length);
            buf.writeBytes(extraData);
        } else {
            buf.writeShort(0);
        }
    }


    private GameCallRespPacket decodeGameCall(ByteBuf buf, PacketHead head) {
        GameCallPacket.CallType callType = GameCallPacket.CallType.formCallType(buf.readByte());
        GameCallRespPacket respPacket = new GameCallRespPacket();
        respPacket.setPacketHead(head);
        respPacket.setCallType(callType);
        respPacket.setPeerName(ByteBufUtils.readUTF8String(buf, 32));
        byte[] ssrc = new byte[2];
        buf.readBytes(ssrc);
        respPacket.setSsrc(ssrc);
        short subCallTypeLenth = (short) buf.readUnsignedShort();

        if (subCallTypeLenth > 0) {
            Integer[] subCallTypes = new Integer[subCallTypeLenth / 4];
            for (int i = 0; i < subCallTypes.length; i++) {
                subCallTypes[i] = buf.readInt();
            }
            respPacket.setSubCallTypes(subCallTypes);
        }

        short dataLength = buf.readShort();
        if (dataLength > 0) {
            List<GameCallPacket.GameRoom> gameRoomList = new ArrayList();
            short dataLength1 = buf.readShort();
            int dataRemainLength = dataLength - 2;
            for (int readNum = 0; readNum < dataLength1; ) {
                short roomLength = buf.readShort();
                int subCallType = buf.readInt();
                String roomId = ByteBufUtils.readUTF8String(buf, roomLength);
                readNum += 2 + 4 + roomLength;
                GameCallPacket.GameRoom gameRoom = new GameCallPacket.GameRoom(subCallType, roomId);
                gameRoomList.add(gameRoom);
            }
            if (dataLength1 > 0) {
                respPacket.setGameRooms((GameCallPacket.GameRoom[]) gameRoomList.toArray());
                dataRemainLength = dataRemainLength - dataLength1;
            }

            short dataLength2 = 0;
            //还有dataLength2
            if (dataLength > 2 + dataLength1) {
                //忽略一个字节的长度字段
                dataLength2 = buf.readByte();
                dataRemainLength = dataRemainLength - 2;

                if (dataLength2 > 0) {
                    //忽略剩余的字段
                    buf.readBytes(dataLength2);
                    dataRemainLength = dataRemainLength - dataLength2;
                }
            }

            if (dataRemainLength > 0) {
                log.debug("game call.has remain data.length:{}", dataRemainLength);
                //忽略不支持的参数
                buf.readBytes(dataRemainLength);
            }
        }
        if (buf.isReadable()) {
            short extraLength = buf.readShort();
            if (extraLength > 0) {
                byte[] extraData = new byte[extraLength];
                buf.readBytes(extraData);
                respPacket.setExtraData(extraData);
            }
        }

        return respPacket;
    }


    private GameServerRespPacket decodeGameServer(ByteBuf buf, PacketHead head) {
        GameServerRespPacket respPacket = new GameServerRespPacket();
        respPacket.setPacketHead(head);
        List<GameServerRespPacket.GameServerInfo> gameServerInfos = new ArrayList<>();
        while (buf.isReadable()) {
            short subLength = buf.readShort();
            if (subLength > 0) {
                GameServerRespPacket.GameServerInfo gameServerInfo = new GameServerRespPacket.GameServerInfo();
                gameServerInfo.setGameId(buf.readInt());
                gameServerInfo.setIp(IPUtil.int2ip(buf.readInt()));
                gameServerInfo.setPort(buf.readShort());
                gameServerInfo.setFlag(buf.readByte());
                gameServerInfo.setRoomId(ByteBufUtils.readUTF8String(buf, 64));
                gameServerInfos.add(gameServerInfo);
            }
        }

        if (gameServerInfos.size() > 0) {
            respPacket.setGameServerInfos(gameServerInfos.toArray(new GameServerRespPacket.GameServerInfo[]{}));
        }
        return respPacket;
    }
}
