package com.v5.test.worker.client.gameCall;

/**
 * Created by piguangtao on 2014/6/25.
 */
public class GameCallRespPacket extends GameCallPacket {

    public final static int COMMAND_GAME_CALL_RESP_PACKET_TYPE = COMMAND_GAME_CALL_RESPONSE_TYPE *256 + COMMAND_PACKET_TYPE;

    public GameCallRespPacket(){
        this.setPacketType(COMMAND_GAME_CALL_RESP_PACKET_TYPE);
    }
}
