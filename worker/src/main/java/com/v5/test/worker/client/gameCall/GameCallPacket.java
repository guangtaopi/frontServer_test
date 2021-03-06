package com.v5.test.worker.client.gameCall;

import com.v5.base.message.command.CommandPacket;

import java.util.Arrays;

/**
 * Created by piguangtao on 2014/6/25.
 */
public class GameCallPacket extends CommandPacket {
    public final static int COMMAND_GAME_CALL_TYPE = 0x17;

    public final static int COMMAND_GAME_CALL_RESPONSE_TYPE = 0x18;

    public final static byte SUB_CALL_TYPE_VIDEO = 0x01;

    public final static byte SUB_CALL_TYPE_AUDIO = 0x02;

    /**
     * 呼叫类型
     */
    private CallType callType;

    private String peerName;

    /**
     * 客户端携带的唯一标示 2个字节，无符号整数
     */
    private byte[] ssrc;

    /**
     * 呼叫的子属性，
     */
    private Integer[] subCallTypes;

    /**
     * 客户端传递的需要透传的字节数组
     */
    private byte[] extraData;


    public CallType getCallType() {
        return callType;
    }

    public void setCallType(CallType callType) {
        this.callType = callType;
    }

    public byte[] getExtraData() {
        return extraData;
    }

    public void setExtraData(byte[] extraData) {
        this.extraData = extraData;
    }

    public String getPeerName() {
        return peerName;
    }

    public void setPeerName(String peerName) {
        this.peerName = peerName;
    }

    public byte[] getSsrc() {
        return ssrc;
    }

    public void setSsrc(byte[] ssrc) {
        this.ssrc = ssrc;
    }

    public Integer[] getSubCallTypes() {
        return subCallTypes;
    }

    public void setSubCallTypes(Integer[] subCallTypes) {
        this.subCallTypes = subCallTypes;
    }

    public GameRoom[] getGameRooms() {
        return gameRooms;
    }

    public void setGameRooms(GameRoom[] gameRooms) {
        this.gameRooms = gameRooms;
    }



    GameRoom[] gameRooms;

    public static class GameRoom {
        Integer gameId;
        String roomId;

        public GameRoom(Integer gameId, String roomId) {
            this.gameId = gameId;
            this.roomId = roomId;
        }

        public Integer getGameId() {
            return gameId;
        }

        public String getRoomId() {
            return roomId;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("GameRoom{");
            sb.append("gameId=").append(gameId);
            sb.append(", roomId='").append(roomId).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GameCallPacket{");
        sb.append("callType=").append(callType);
        sb.append(", peerName='").append(peerName).append('\'');
        sb.append(", ssrc=").append(Arrays.toString(ssrc));
        sb.append(", subCallTypes=").append(Arrays.toString(subCallTypes));
        sb.append(", extraData=").append(Arrays.toString(extraData));
        sb.append(", gameRooms=").append(Arrays.toString(gameRooms));
        sb.append('}');
        return sb.toString();
    }


    public enum CallType{
        /**
         * 呼叫（包括视频呼叫或者语音呼叫）
         */
        CALL((byte)0x01),

        /**
         * 接受呼叫
         */
        ACCEPTE((byte)0x02),

        REJECT((byte)0x03),

        HANGUP((byte)0x04),

        RECEIVED((byte)0x06),

        BUSY((byte)0x07),

        ASSIST_CALL((byte)0x08),

        PEER_NOT_SUPPORT((byte)0x15);


        private byte value;

        CallType(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }

        public static CallType formCallType(byte value){
             switch (value){
                 case 0x01:return CALL;
                 case 0x02:return ACCEPTE;
                 case 0x03:return REJECT;
                 case 0x04:return HANGUP;
                 case 0x06:return RECEIVED;
                 case 0x07:return BUSY;
                 case 0x08:return ASSIST_CALL;
                 case 0x15:return PEER_NOT_SUPPORT;
                 default:return null;
             }
        }
    }
}
