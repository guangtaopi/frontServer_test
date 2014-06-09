package com.v5.test.worker.handler;

import com.v5.base.message.command.CallStatus;
import com.v5.base.message.command.UDPServerPacket;
import com.v5.base.message.notify.SystemNotifyPackage;
import com.v5.base.message.text.ForwardMessagePacket;
import com.v5.base.message.text.ImageMessagePacket;
import com.v5.base.message.text.TextMessagePacket;
import com.v5.base.message.text.VoiceMessagePacket;
import com.v5.test.worker.packet.StatusResponsePackage;

/**
 * Created by piguangtao on 2014/6/5.
 */
public interface ITestResultHandler {

    void receiveSingleTextMsg(String from, String to, TextMessagePacket content, long recTime);

    void receiveTransTextMsg(String from, String to, ForwardMessagePacket content);

    void receiveImageMsg(String from, String to, ImageMessagePacket packet);

    void receiveMsgStatus(StatusResponsePackage statusResponsePackage);

    void receiveAudioMsg(String from, String to, VoiceMessagePacket packet);

    void receiveCallRequest(String from,String to,CallStatus callStatus);

    void receiveUDPServerPacket(String userId,UDPServerPacket packet);

    void receiveSystemNotify(String receiver,SystemNotifyPackage notifyPackage);
}
