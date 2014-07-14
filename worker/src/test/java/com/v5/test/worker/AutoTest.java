package com.v5.test.worker;

import com.v5.base.message.command.CallStatus;
import com.v5.base.message.command.UDPServerPacket;
import com.v5.base.message.notify.SystemNotifyPackage;
import com.v5.base.message.text.*;
import com.v5.test.worker.client.gameCall.GameCallPacket;
import com.v5.test.worker.client.gameCall.GameCallRespPacket;
import com.v5.test.worker.client.gameCall.GameServerRespPacket;
import com.v5.test.worker.handler.ITestResultHandler;
import com.v5.test.worker.packet.StatusResponsePackage;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Created by piguangtao on 2014/6/5.
 * 在FrontServer不连接rpcServer测试时，用户的sessionId和userId相同。
 */
public class AutoTest implements ITestResultHandler {

    private TextMessagePacket receiveTextMsg;

    private ForwardMessagePacket receiveForwardMsg;

    private ImageMessagePacket imageMessagePacket;

    private VoiceMessagePacket voiceMessagePacket;
    private ConcurrentHashMap<Integer, CountDownLatch> packetTypeCountDownLatch = new ConcurrentHashMap();

    private ConcurrentHashMap<Integer, MessageStatus> idStatusMap = new ConcurrentHashMap();
    private ConcurrentHashMap<Integer, CountDownLatch> idStatusLatchMap = new ConcurrentHashMap();


    private SystemNotifyPackage systemNotifyPackage;
    private ConcurrentHashMap<String, CountDownLatch> systemNotifyCountDownLatch = new ConcurrentHashMap<>();





    /**
     * 呼叫状态映射
     * key:from+"_"+to+"_"+callstatus
     */
    private ConcurrentHashMap<String, Boolean> callStatusMap = new ConcurrentHashMap();
    private ConcurrentHashMap<String, CountDownLatch> callStatusLatchMap = new ConcurrentHashMap();


    private ConcurrentHashMap<String, UDPServerPacket> callUDPServerPacketMap = new ConcurrentHashMap();
    private ConcurrentHashMap<String, CountDownLatch> callUDPServerPacketLatchMap = new ConcurrentHashMap();


    private GameCallRespPacket gameCallRespPacket;
    private ConcurrentHashMap<GameCallPacket.CallType, CountDownLatch> gameCallCountDownLatch = new ConcurrentHashMap<>();


    private ConcurrentHashMap<String, GameServerRespPacket> userGameServerInfo = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, CountDownLatch> userGameServerCountDownlatch = new ConcurrentHashMap<>();

    @Override
    public void receiveSingleTextMsg(String from, String to, TextMessagePacket packet, long recTime) {
        receiveTextMsg = packet;
        CountDownLatch latch = packetTypeCountDownLatch.get(TextMessagePacket.TEXT_MESSAGE_PACKET_TYPE);
        if (null != latch) {
            latch.countDown();
        }
    }

    @Override
    public void receiveTransTextMsg(String from, String to, ForwardMessagePacket packet) {
        receiveForwardMsg = packet;
        CountDownLatch latch = packetTypeCountDownLatch.get(ForwardMessagePacket.FORWARD_MESSAGE_PACKET_TYPE);
        if (null != latch) {
            latch.countDown();
        }
    }

    @Override
    public void receiveImageMsg(String from, String to, ImageMessagePacket packet) {
        imageMessagePacket = packet;
        CountDownLatch latch = packetTypeCountDownLatch.get(ImageMessagePacket.IMAGE_MESSAGE_PACKET_TYPE);
        if (null != latch) {
            latch.countDown();
        }
    }

    @Override
    public void receiveMsgStatus(StatusResponsePackage statusResponsePackage) {
        idStatusMap.put(statusResponsePackage.getPacketHead().getTempId(), statusResponsePackage.getMessageStatus());
        CountDownLatch latch = packetTypeCountDownLatch.get(StatusResponsePackage.STATUS_MESSAGE_RESPONSE_PACKET_TYPE);
        if (null != latch) {
            latch.countDown();
        }
    }

    @Override
    public void receiveAudioMsg(String from, String to, VoiceMessagePacket packet) {
        voiceMessagePacket = packet;

        CountDownLatch latch = packetTypeCountDownLatch.get(VoiceMessagePacket.VOICE_MESSAGE_PACKET_TYPE);
        if (null != latch) {
            latch.countDown();
        }
    }

    @Override
    public void receiveCallRequest(String from, String to, CallStatus callStatus) {
        callStatusMap.put(getCallStatusKey(from, to, callStatus), Boolean.TRUE);

        CountDownLatch latch = callStatusLatchMap.get(getCallStatusKey(from, to, callStatus));
        synchronized (latch) {
            if (null != latch) {
                latch.countDown();
            }
        }
    }

    @Override
    public void receiveUDPServerPacket(String userId, UDPServerPacket packet) {
        callUDPServerPacketMap.put(userId, packet);

        CountDownLatch latch = callUDPServerPacketLatchMap.get(userId);
        synchronized (latch) {
            if (null != latch) {
                latch.countDown();
            }
        }
    }

    @Override
    public void receiveSystemNotify(String receiver, SystemNotifyPackage notifyPackage) {
        systemNotifyPackage = notifyPackage;
        CountDownLatch latch = callUDPServerPacketLatchMap.get(receiver);
        synchronized (latch) {
            if (null != latch) {
                latch.countDown();
            }
        }
    }


    @Override
    public void receiveGameCallRespPacket(String from, String to, GameCallRespPacket packet, long recTime) {
        gameCallRespPacket = packet;
        CountDownLatch latch = gameCallCountDownLatch.get(packet.getCallType());
        if (null != latch) {
            latch.countDown();
        }
    }

    @Override
    public void receiveGameServerInfo(String userId, GameServerRespPacket respPacket, long currenTime) {
        userGameServerInfo.put(userId,respPacket);
        CountDownLatch latch = userGameServerCountDownlatch.get(userId);
        if (null != latch) {
            latch.countDown();
        }
    }


    public static String getCallStatusKey(String from, String to, CallStatus callStatus) {
        return from + "_" + to + "_" + callStatus.id();
    }

    public TextMessagePacket getReceiveTextMsg() {
        return receiveTextMsg;
    }

    public ForwardMessagePacket getReceiveForwardMsg() {
        return receiveForwardMsg;
    }

    public ImageMessagePacket getImageMessagePacket() {
        return imageMessagePacket;
    }

    public ConcurrentHashMap<Integer, MessageStatus> getIdStatusMap() {
        return idStatusMap;
    }

    public VoiceMessagePacket getVoiceMessagePacket() {
        return voiceMessagePacket;
    }

    public ConcurrentHashMap<String, Boolean> getCallStatusMap() {
        return callStatusMap;
    }

    public ConcurrentHashMap<String, UDPServerPacket> getCallUDPServerPacketMap() {
        return callUDPServerPacketMap;
    }

    public ConcurrentHashMap<String, CountDownLatch> getCallStatusLatchMap() {
        return callStatusLatchMap;
    }

    public ConcurrentHashMap<String, CountDownLatch> getCallUDPServerPacketLatchMap() {
        return callUDPServerPacketLatchMap;
    }

    public ConcurrentHashMap<Integer, CountDownLatch> getIdStatusLatchMap() {
        return idStatusLatchMap;
    }

    public ConcurrentHashMap<Integer, CountDownLatch> getPacketTypeCountDownLatch() {
        return packetTypeCountDownLatch;
    }

    public SystemNotifyPackage getSystemNotifyPackage() {
        return systemNotifyPackage;
    }

    public ConcurrentHashMap<String, CountDownLatch> getSystemNotifyCountDownLatch() {
        return systemNotifyCountDownLatch;
    }


    public GameCallRespPacket getGameCallRespPacket() {
        return gameCallRespPacket;
    }

    public ConcurrentHashMap<GameCallPacket.CallType, CountDownLatch> getGameCallCountDownLatch() {
        return gameCallCountDownLatch;
    }

    public ConcurrentHashMap<String, GameServerRespPacket> getUserGameServerInfo() {
        return userGameServerInfo;
    }

    public ConcurrentHashMap<String, CountDownLatch> getUserGameServerCountDownlatch() {
        return userGameServerCountDownlatch;
    }
}
