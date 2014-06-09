package com.v5.test.worker.packet;

import com.v5.base.message.text.MessageStatus;
import com.v5.base.message.text.SimpleMessagePacket;

/**
 * Created by piguangtao on 2014/6/5.
 */
public class StatusResponsePackage extends SimpleMessagePacket {
    public final static int STATUS_MESSAGE_RESPONSE_PACKET_TYPE = STATUS_RESPONSE_MESSAGE_TYPE*256 + SIMPLE_MESSAGE_PACKET_TYPE;

    public StatusResponsePackage(){
        this.setPacketType(STATUS_MESSAGE_RESPONSE_PACKET_TYPE);
    }

    private MessageStatus messageStatus;

    public MessageStatus getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(MessageStatus messageStatus) {
        this.messageStatus = messageStatus;
    }

}
