import com.v5.base.message.command.CallPacket;
import com.v5.base.message.command.CallStatus;
import com.v5.base.message.notify.SystemNotifyPackage;
import com.v5.base.message.text.*;
import com.v5.base.packet.PacketHead;
import com.v5.test.worker.AutoTest;
import com.v5.test.worker.bean.TaskSnapshort;
import com.v5.test.worker.client.ClientOnclientManager;
import com.v5.test.worker.client.gameCall.GameCallHandupReqPacket;
import com.v5.test.worker.client.gameCall.GameCallPacket;
import com.v5.test.worker.client.gameCall.GameCallReqPacket;
import com.v5.test.worker.client.gameCall.GameCallRespPacket;
import com.v5.test.worker.service.TcpService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by piguangtao on 14-2-18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/conf/main_test.xml")
public class TestMain {

    @Autowired
    private ClientOnclientManager onclientManager;

    @Autowired
    private TcpService tcpService;

    @Autowired
    private AutoTest autoTest;

    private static String textMsg;

    private static String transMsg;

    private static String fromUserId;

    private static String toUserId;

    private static String imageContent;

    private static String voiceContent;

    private static String systemNotifyContent;

    private static AtomicBoolean isInit = new AtomicBoolean(false);

    private static AtomicInteger tempId = new AtomicInteger(1);

    private static int awaitTimeMilliSecond = 2000;

    @Before
    public void before() throws Exception {
        //tcp连接进行一次操作
        if (!isInit.getAndSet(true)) {
            //测试环境用户
            String userId1 = "6a6e0440d68b11e384f669f51b0e7dca";
            String sessionId1 = "0cd5c32ee50a4a35b068bd43e221138b";

            String userId2 = "19050f3005bc11e4a5a669f51b0e7dca";
            String sessionId2 = "21875f773b9a40b9845209e54b78a9ec";

//            String userId1 = "6a6e0440d68b11e384f669f51b0e7dca";
//            String sessionId1 = "6a6e0440d68b11e384f669f51b0e7dca";
//
//            String userId2 = "2ae70f20d68f11e384f669f51b0e7dca";
//            String sessionId2 = "2ae70f20d68f11e384f669f51b0e7dca";

//            String userId2 = userId1;
//            String sessionId2 = sessionId1;


//            String userId1 = "e763ac40d03511e3bbf113abbfd80b2e";
//            String sessionId1 = "e3e56ad9a1cb474288a1f4f620474dbd";
//
//            String userId2 = "0ac94f80e54b11e3bde571bafaff7945";
//            String sessionId2 = "d0b16b98a7314147acb78328ea6b065a";

            //现网环境
//            String userId1 = "e763ac40d03511e3bbf113abbfd80b2e";
//            String sessionId1 = "7a8eabe140bd45c2bb9b61a6b5a9fcbd";
//
//            String userId2 = "0ac94f80e54b11e3bde571bafaff7945";
//            String sessionId2 = "d0b16b98a7314147acb78328ea6b065a";



            List<String> userIds = new ArrayList<>();
            userIds.add(userId1);
            userIds.add(userId2);

            //初始化用户的nameMd5和sessionId
            onclientManager.saveSession(sessionId1, userId1);
            onclientManager.saveSession(sessionId2, userId2);

            for (int i = 0; i < userIds.size(); i++) {
                tcpService.connect(userIds.get(i));
            }
            fromUserId = TaskSnapshort.loginedUserSet.take();
            toUserId = TaskSnapshort.loginedUserSet.take();
            textMsg = "1-simple test";
            transMsg = "2-trans_test";
            imageContent = "http://192.168.1.231:9101/d/1.jpg";
            voiceContent = "{\"type\":\"voice\",\"url\":\"http://192.168.1.232:8080/1/1.mp3\"}";
            systemNotifyContent = "{\"type\":\"user_update\",\"info\":{\"user\":{\"id\":\"2dc363a0ec5211e387ca71bafaff7945\",\"nickname\":\"s44\",\"mobile\":\"18112957253\",\"sex\":0,\"avatar_url\":\"https://cn.image.chatgame.me/api/avatar/766090e7-3a01-469b-b12f-3056ae8accf0\",\"user_type\":0,\"mobile_verify\":1,\"public_key\":\"-----BEGIN RSA PUBLIC KEY-----\\nMIGHAoGBAL7UheDuzSltWjmmEgflT+z3VYzHIVnkoXwpcImjXf/bpZj29mTr3A0w\\nYusFClpbhuq6pt7D/hn1al0MzT1O19JnQ3udbadcL/sJG8QQBtRdG+J95d/rM5+J\\nrDxPJ9EKhZAmOs755I+TiHvbUdDahO1qdhG8Z4s2zKbI8U80/EJzAgEB\\n-----END RSA PUBLIC KEY-----\\n\",\"countrycode\":\"0086\",\"conversation\":null},\"group\":null,\"desc\":null,\"group_id\":null,\"user_id\":null,\"msg_type\":null,\"from\":null}}";
        }
    }

    @Test
    public void testSendSimpleTextMsg() throws InterruptedException, UnsupportedEncodingException {
        TextMessagePacket simpleMessagePacket = new TextMessagePacket();
        simpleMessagePacket.setFrom(fromUserId);
        simpleMessagePacket.setToUser(toUserId);
        simpleMessagePacket.setContent(textMsg.getBytes(Charset.forName("utf-8")));
        simpleMessagePacket.setMessageServiceType(SimpleMessagePacket.TO_USER);


        PacketHead head = new PacketHead();
        head.setTempId(tempId.getAndIncrement());
        simpleMessagePacket.setPacketHead(head);

        CountDownLatch latch = new CountDownLatch(1);
        autoTest.getIdStatusLatchMap().put(head.getTempId(), latch);

        CountDownLatch packetLatch = new CountDownLatch(1);
        autoTest.getPacketTypeCountDownLatch().put(TextMessagePacket.TEXT_MESSAGE_PACKET_TYPE, packetLatch);

        tcpService.sendSimpleMessagePacket(simpleMessagePacket);

        latch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);
        Assert.assertEquals(MessageStatus.SERVER_RECEIVED, autoTest.getIdStatusMap().get(head.getTempId()));

        packetLatch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);
        TextMessagePacket responsePacket = autoTest.getReceiveTextMsg();
        Assert.assertNotNull(responsePacket);
        Assert.assertEquals(textMsg, new String(responsePacket.getContent(), "utf-8"));
        Assert.assertEquals(fromUserId, responsePacket.getFrom());
        Assert.assertTrue(responsePacket.getMessageId() > 0);
    }

    @Test
    public void testSendTransMsg() throws InterruptedException, UnsupportedEncodingException {
        ForwardMessagePacket packet = new ForwardMessagePacket();
        packet.setFrom(fromUserId);
        packet.setToUser(toUserId);
        packet.setData(transMsg.getBytes(Charset.forName("UTF-8")));
        packet.setContent(transMsg.getBytes(Charset.forName("UTF-8")));

        PacketHead head = new PacketHead();
        head.setTempId(tempId.getAndIncrement());
        packet.setPacketHead(head);

        CountDownLatch latch = new CountDownLatch(1);
        autoTest.getIdStatusLatchMap().put(head.getTempId(), latch);

        CountDownLatch packetLatch = new CountDownLatch(1);
        autoTest.getPacketTypeCountDownLatch().put(ForwardMessagePacket.FORWARD_MESSAGE_PACKET_TYPE, packetLatch);

        tcpService.sendSimpleMessagePacket(packet);

        latch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);
        //转发消息，服务端不发送状态响应
        Assert.assertNull(autoTest.getIdStatusMap().get(head.getTempId()));

        packetLatch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);
        ForwardMessagePacket responsePacket = autoTest.getReceiveForwardMsg();
        Assert.assertNotNull(responsePacket);
        Assert.assertEquals(transMsg, new String(responsePacket.getData(), "utf-8"));
        Assert.assertEquals(fromUserId, responsePacket.getFrom());
        Assert.assertNull(responsePacket.getMessageId());
    }

    @Test
    public void testSendImageMsg() throws InterruptedException, UnsupportedEncodingException {
        ImageMessagePacket imageMessagePacket = new ImageMessagePacket();
        imageMessagePacket.setToUser(toUserId);
        imageMessagePacket.setFrom(fromUserId);
        imageMessagePacket.setContent(imageContent.getBytes(Charset.forName("utf-8")));

        PacketHead head = new PacketHead();
        head.setTempId(tempId.getAndIncrement());
        imageMessagePacket.setPacketHead(head);

        CountDownLatch idStatusLatch = new CountDownLatch(1);
        autoTest.getIdStatusLatchMap().put(head.getTempId(), idStatusLatch);

        CountDownLatch packetLatch = new CountDownLatch(1);
        autoTest.getPacketTypeCountDownLatch().put(ImageMessagePacket.IMAGE_MESSAGE_PACKET_TYPE, packetLatch);


        tcpService.sendSimpleMessagePacket(imageMessagePacket);

        idStatusLatch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);
        Assert.assertEquals(MessageStatus.SERVER_RECEIVED, autoTest.getIdStatusMap().get(head.getTempId()));

        packetLatch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);
        ImageMessagePacket responsePacket = autoTest.getImageMessagePacket();
        Assert.assertNotNull(responsePacket);
        Assert.assertEquals(imageContent, new String(responsePacket.getContent(), "utf-8"));
        Assert.assertEquals(fromUserId, responsePacket.getFrom());
        Assert.assertTrue(responsePacket.getMessageId() > 0);
    }

    @Test
    public void testSendAudioMsg() throws InterruptedException, UnsupportedEncodingException {
        VoiceMessagePacket voiceMessagePacket = new VoiceMessagePacket();
        voiceMessagePacket.setToUser(toUserId);
        voiceMessagePacket.setFrom(fromUserId);
        voiceMessagePacket.setContent(voiceContent.getBytes(Charset.forName("utf-8")));

        PacketHead head = new PacketHead();
        head.setTempId(tempId.getAndIncrement());
        voiceMessagePacket.setPacketHead(head);

        CountDownLatch idStatusLatch = new CountDownLatch(1);
        autoTest.getIdStatusLatchMap().put(head.getTempId(), idStatusLatch);


        CountDownLatch packetLatch = new CountDownLatch(1);
        autoTest.getIdStatusLatchMap().put(VoiceMessagePacket.VOICE_MESSAGE_PACKET_TYPE, packetLatch);

        tcpService.sendSimpleMessagePacket(voiceMessagePacket);

        idStatusLatch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);

        //服务端返回消息已经接受到
        Assert.assertEquals(MessageStatus.SERVER_RECEIVED, autoTest.getIdStatusMap().get(head.getTempId()));

        //服务端给接受客户端转发消息
        packetLatch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);

        VoiceMessagePacket responsePacket = autoTest.getVoiceMessagePacket();
        Assert.assertNotNull(responsePacket);
        Assert.assertEquals(voiceContent, new String(responsePacket.getContent(), "utf-8"));
        Assert.assertEquals(fromUserId, responsePacket.getFrom());
        Assert.assertTrue(responsePacket.getMessageId() > 0);
    }


    @Test
    public void testSendVideoCall() throws InterruptedException {
        CallPacket videoCallPacket = new CallPacket();
        videoCallPacket.setPeerName(toUserId);
        videoCallPacket.setCallStatus(CallStatus.VIDEO_REQUEST);

        CountDownLatch latch = new CountDownLatch(1);
        autoTest.getCallStatusLatchMap().put(AutoTest.getCallStatusKey(fromUserId, toUserId, CallStatus.VIDEO_REQUEST), latch);

        tcpService.sendVideoCall(fromUserId, videoCallPacket);

        //验证结果
        //呼叫接受方收到服务端的呼叫转发
        latch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);
        Assert.assertTrue(autoTest.getCallStatusMap().get(AutoTest.getCallStatusKey(fromUserId, toUserId, CallStatus.VIDEO_REQUEST)));

        //发送辅助视频呼叫
        videoCallPacket = new CallPacket();
        videoCallPacket.setPeerName(toUserId);
        videoCallPacket.setCallStatus(CallStatus.VIDEO_REQUEST_AGAIN);

        latch = new CountDownLatch(1);
        autoTest.getCallStatusLatchMap().put(AutoTest.getCallStatusKey(fromUserId, toUserId, CallStatus.VIDEO_REQUEST_AGAIN), latch);

        tcpService.sendVideoCall(fromUserId, videoCallPacket);

        latch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);
        Assert.assertTrue(autoTest.getCallStatusMap().get(AutoTest.getCallStatusKey(fromUserId, toUserId, CallStatus.VIDEO_REQUEST_AGAIN)));

        //接受方发送已经收到
        videoCallPacket = new CallPacket();
        videoCallPacket.setPeerName(fromUserId);
        videoCallPacket.setCallStatus(CallStatus.RECEIVED);
        if (fromUserId.equals(toUserId)) {
            latch = new CountDownLatch(2);
            autoTest.getCallUDPServerPacketLatchMap().put(fromUserId, latch);
        } else {
            latch = new CountDownLatch(1);
            autoTest.getCallUDPServerPacketLatchMap().put(fromUserId, latch);
            latch = new CountDownLatch(1);
            autoTest.getCallUDPServerPacketLatchMap().put(toUserId, latch);
        }

        tcpService.sendVideoCall(toUserId, videoCallPacket);

        //发送方和接受方接受到UDP包
        autoTest.getCallUDPServerPacketLatchMap().get(fromUserId).await(1000, TimeUnit.MILLISECONDS);
        autoTest.getCallUDPServerPacketLatchMap().get(toUserId).await(1000, TimeUnit.MILLISECONDS);
        Assert.assertNotNull(autoTest.getCallUDPServerPacketMap().get(fromUserId));
        Assert.assertNotNull(autoTest.getCallUDPServerPacketMap().get(toUserId));

        //接受方发送接听
        videoCallPacket = new CallPacket();
        videoCallPacket.setPeerName(fromUserId);
        videoCallPacket.setCallStatus(CallStatus.VIDEO_ACCEPT);

        latch = new CountDownLatch(1);
        autoTest.getCallStatusLatchMap().put(AutoTest.getCallStatusKey(fromUserId, toUserId, CallStatus.VIDEO_ACCEPT), latch);

        tcpService.sendVideoCall(toUserId, videoCallPacket);

        latch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);
        Assert.assertTrue(autoTest.getCallStatusMap().get(AutoTest.getCallStatusKey(fromUserId, toUserId, CallStatus.VIDEO_ACCEPT)));

        //接受方发送挂断
        videoCallPacket = new CallPacket();
        videoCallPacket.setPeerName(fromUserId);
        videoCallPacket.setCallStatus(CallStatus.HANGUP);

        latch = new CountDownLatch(1);
        autoTest.getCallStatusLatchMap().put(AutoTest.getCallStatusKey(fromUserId, toUserId, CallStatus.HANGUP), latch);

        tcpService.sendVideoCall(toUserId, videoCallPacket);

        latch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);
        Assert.assertTrue(autoTest.getCallStatusMap().get(AutoTest.getCallStatusKey(fromUserId, toUserId, CallStatus.HANGUP)));

    }


    @Test
    /**
     * audio call 测试流程
     * 呼叫-->辅助呼叫-->已经接受-->udp包-->接听-->挂断
     */
    public void testSendAudioCall() throws InterruptedException {
        CallPacket audioCallPacket = new CallPacket();
        audioCallPacket.setPeerName(toUserId);
        audioCallPacket.setCallStatus(CallStatus.AUDIO_REQUEST);

        CountDownLatch latch = new CountDownLatch(1);
        autoTest.getCallStatusLatchMap().put(AutoTest.getCallStatusKey(fromUserId, toUserId, CallStatus.AUDIO_REQUEST), latch);

        tcpService.sendVideoCall(fromUserId, audioCallPacket);

        //验证结果
        //呼叫接受方收到服务端的呼叫转发
        latch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);
        Assert.assertTrue(autoTest.getCallStatusMap().get(AutoTest.getCallStatusKey(fromUserId, toUserId, CallStatus.AUDIO_REQUEST)));

        //发送辅助视频呼叫
        audioCallPacket = new CallPacket();
        audioCallPacket.setPeerName(toUserId);
        audioCallPacket.setCallStatus(CallStatus.AUDIO_REQUEST_AGAIN);

        latch = new CountDownLatch(1);
        autoTest.getCallStatusLatchMap().put(AutoTest.getCallStatusKey(fromUserId, toUserId, CallStatus.AUDIO_REQUEST_AGAIN), latch);

        tcpService.sendVideoCall(fromUserId, audioCallPacket);

        latch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);
        Assert.assertTrue(autoTest.getCallStatusMap().get(AutoTest.getCallStatusKey(fromUserId, toUserId, CallStatus.AUDIO_REQUEST_AGAIN)));

        //接受方发送已经收到
        audioCallPacket = new CallPacket();
        audioCallPacket.setPeerName(fromUserId);
        audioCallPacket.setCallStatus(CallStatus.RECEIVED);
        if (fromUserId.equals(toUserId)) {
            latch = new CountDownLatch(2);
            autoTest.getCallUDPServerPacketLatchMap().put(fromUserId, latch);
        } else {
            latch = new CountDownLatch(1);
            autoTest.getCallUDPServerPacketLatchMap().put(fromUserId, latch);
            latch = new CountDownLatch(1);
            autoTest.getCallUDPServerPacketLatchMap().put(toUserId, latch);
        }

        tcpService.sendVideoCall(toUserId, audioCallPacket);

        //发送方和接受方接受到UDP包
        autoTest.getCallUDPServerPacketLatchMap().get(fromUserId).await(1000, TimeUnit.MILLISECONDS);
        autoTest.getCallUDPServerPacketLatchMap().get(toUserId).await(1000, TimeUnit.MILLISECONDS);
        Assert.assertNotNull(autoTest.getCallUDPServerPacketMap().get(fromUserId));
        Assert.assertNotNull(autoTest.getCallUDPServerPacketMap().get(toUserId));

        //接受方发送接听
        audioCallPacket = new CallPacket();
        audioCallPacket.setPeerName(fromUserId);
        audioCallPacket.setCallStatus(CallStatus.AUDIO_ACCEPT);

        latch = new CountDownLatch(1);
        autoTest.getCallStatusLatchMap().put(AutoTest.getCallStatusKey(fromUserId, toUserId, CallStatus.AUDIO_ACCEPT), latch);

        tcpService.sendVideoCall(toUserId, audioCallPacket);

        latch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);
        Assert.assertTrue(autoTest.getCallStatusMap().get(AutoTest.getCallStatusKey(fromUserId, toUserId, CallStatus.AUDIO_ACCEPT)));

        //接受方发送挂断
        audioCallPacket = new CallPacket();
        audioCallPacket.setPeerName(fromUserId);
        audioCallPacket.setCallStatus(CallStatus.HANGUP);

        latch = new CountDownLatch(1);
        autoTest.getCallStatusLatchMap().put(AutoTest.getCallStatusKey(fromUserId, toUserId, CallStatus.HANGUP), latch);

        tcpService.sendVideoCall(toUserId, audioCallPacket);

        latch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);
        Assert.assertTrue(autoTest.getCallStatusMap().get(AutoTest.getCallStatusKey(fromUserId, toUserId, CallStatus.HANGUP)));

    }


    @Test
    /**
     * 视频呼叫拒绝
     */
    public void testSendVideoCallReject() throws InterruptedException {
        CallPacket videoCallPacket = new CallPacket();
        videoCallPacket.setPeerName(toUserId);
        videoCallPacket.setCallStatus(CallStatus.VIDEO_REQUEST);

        CountDownLatch latch = new CountDownLatch(1);
        autoTest.getCallStatusLatchMap().put(AutoTest.getCallStatusKey(fromUserId, toUserId, CallStatus.VIDEO_REQUEST), latch);

        tcpService.sendVideoCall(fromUserId, videoCallPacket);

        //验证结果
        //呼叫接受方收到服务端的呼叫转发
        latch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);
        Assert.assertTrue(autoTest.getCallStatusMap().get(AutoTest.getCallStatusKey(fromUserId, toUserId, CallStatus.VIDEO_REQUEST)));

        //发送辅助视频呼叫
        videoCallPacket = new CallPacket();
        videoCallPacket.setPeerName(toUserId);
        videoCallPacket.setCallStatus(CallStatus.VIDEO_REQUEST_AGAIN);

        latch = new CountDownLatch(1);
        autoTest.getCallStatusLatchMap().put(AutoTest.getCallStatusKey(fromUserId, toUserId, CallStatus.VIDEO_REQUEST_AGAIN), latch);

        tcpService.sendVideoCall(fromUserId, videoCallPacket);

        latch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);
        Assert.assertTrue(autoTest.getCallStatusMap().get(AutoTest.getCallStatusKey(fromUserId, toUserId, CallStatus.VIDEO_REQUEST_AGAIN)));

        //接受方发送已经收到
        videoCallPacket = new CallPacket();
        videoCallPacket.setPeerName(fromUserId);
        videoCallPacket.setCallStatus(CallStatus.RECEIVED);
        if (fromUserId.equals(toUserId)) {
            latch = new CountDownLatch(2);
            autoTest.getCallUDPServerPacketLatchMap().put(fromUserId, latch);
        } else {
            latch = new CountDownLatch(1);
            autoTest.getCallUDPServerPacketLatchMap().put(fromUserId, latch);
            CountDownLatch toLatch = new CountDownLatch(1);
            autoTest.getCallUDPServerPacketLatchMap().put(toUserId, toLatch);
        }

        tcpService.sendVideoCall(toUserId, videoCallPacket);

        //发送方和接受方接受到UDP包
        autoTest.getCallUDPServerPacketLatchMap().get(fromUserId).await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);
        autoTest.getCallUDPServerPacketLatchMap().get(toUserId).await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);
        Assert.assertNotNull(autoTest.getCallUDPServerPacketMap().get(fromUserId));
        Assert.assertNotNull(autoTest.getCallUDPServerPacketMap().get(toUserId));

        //接受方发送拒绝
        videoCallPacket = new CallPacket();
        videoCallPacket.setPeerName(fromUserId);
        videoCallPacket.setCallStatus(CallStatus.REJECT);

        latch = new CountDownLatch(1);
        autoTest.getCallStatusLatchMap().put(AutoTest.getCallStatusKey(fromUserId, toUserId, CallStatus.REJECT), latch);

        tcpService.sendVideoCall(toUserId, videoCallPacket);

        latch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);
        Assert.assertTrue(autoTest.getCallStatusMap().get(AutoTest.getCallStatusKey(fromUserId, toUserId, CallStatus.REJECT)));

    }


    @Test
    /**
     * audio call 正忙测试流程
     * 接受方回复正忙时，接受方不发送已收到指令
     * 呼叫-->辅助呼叫-->正忙
     */
    public void testSendAudioCallBeingBusy() throws InterruptedException {
        CallPacket audioCallPacket = new CallPacket();
        audioCallPacket.setPeerName(toUserId);
        audioCallPacket.setCallStatus(CallStatus.AUDIO_REQUEST);

        CountDownLatch latch = new CountDownLatch(1);
        autoTest.getCallStatusLatchMap().put(AutoTest.getCallStatusKey(fromUserId, toUserId, CallStatus.AUDIO_REQUEST), latch);

        tcpService.sendVideoCall(fromUserId, audioCallPacket);

        //验证结果
        //呼叫接受方收到服务端的呼叫转发
        latch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);
        Assert.assertTrue(autoTest.getCallStatusMap().get(AutoTest.getCallStatusKey(fromUserId, toUserId, CallStatus.AUDIO_REQUEST)));

        //发送辅助视频呼叫
        audioCallPacket = new CallPacket();
        audioCallPacket.setPeerName(toUserId);
        audioCallPacket.setCallStatus(CallStatus.AUDIO_REQUEST_AGAIN);

        latch = new CountDownLatch(1);
        autoTest.getCallStatusLatchMap().put(AutoTest.getCallStatusKey(fromUserId, toUserId, CallStatus.AUDIO_REQUEST_AGAIN), latch);

        tcpService.sendVideoCall(fromUserId, audioCallPacket);

        latch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);
        Assert.assertTrue(autoTest.getCallStatusMap().get(AutoTest.getCallStatusKey(fromUserId, toUserId, CallStatus.AUDIO_REQUEST_AGAIN)));

        //接受方发送正忙
        audioCallPacket = new CallPacket();
        audioCallPacket.setPeerName(fromUserId);
        audioCallPacket.setCallStatus(CallStatus.BUSY);

        latch = new CountDownLatch(1);
        autoTest.getCallStatusLatchMap().put(AutoTest.getCallStatusKey(fromUserId, toUserId, CallStatus.BUSY), latch);

        tcpService.sendVideoCall(toUserId, audioCallPacket);

        latch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);
        Assert.assertTrue(autoTest.getCallStatusMap().get(AutoTest.getCallStatusKey(toUserId, fromUserId, CallStatus.BUSY)));

    }

    @Test
    /**
     * A-->B发送系统通知
     * 需要接受端确认
     */
    public void sendSystemNotifyWithAck() throws InterruptedException {

        SystemNotifyPackage notifyPackage = new SystemNotifyPackage();

        PacketHead head = new PacketHead();
        head.setTempId(tempId.getAndIncrement());
        notifyPackage.setPacketHead(head);
        CountDownLatch idStatusLatch = new CountDownLatch(1);
        autoTest.getIdStatusLatchMap().put(head.getTempId(), idStatusLatch);

        notifyPackage.setMsgType((byte) 0x00);

        //需要确认
        notifyPackage.setServeType((byte) 0x01);

        notifyPackage.setFrom(fromUserId);
        notifyPackage.setTo(toUserId);
        notifyPackage.setExpired(0l);
        String pushContent = "push_test";
        notifyPackage.setPushContentLength((short) pushContent.length());
        notifyPackage.setPushContentBody(pushContent);
        notifyPackage.setMesssageLength((short) systemNotifyContent.length());
        notifyPackage.setMessageBody(systemNotifyContent);
        notifyPackage.setMsgId(0);

        CountDownLatch notifyLatch = new CountDownLatch(1);
        autoTest.getSystemNotifyCountDownLatch().put(toUserId, notifyLatch);

        tcpService.sendSystemNofify(fromUserId, notifyPackage);

        notifyLatch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);
        idStatusLatch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);

        //服务端返回消息已经接受到
        Assert.assertEquals(MessageStatus.SERVER_RECEIVED, autoTest.getIdStatusMap().get(head.getTempId()));
        SystemNotifyPackage receiveSystemNotify = autoTest.getSystemNotifyPackage();
        Assert.assertNotNull(receiveSystemNotify);
        Assert.assertEquals(fromUserId, receiveSystemNotify.getFrom());
        Assert.assertEquals(toUserId, receiveSystemNotify.getTo());
        Assert.assertEquals(notifyPackage.getExpired(), receiveSystemNotify.getExpired());
        Assert.assertEquals(notifyPackage.getPushContentLength(), receiveSystemNotify.getPushContentLength());
        Assert.assertEquals(notifyPackage.getPushContentBody(), receiveSystemNotify.getPushContentBody());
        Assert.assertEquals(notifyPackage.getMesssageLength(), receiveSystemNotify.getMesssageLength());
        Assert.assertEquals(notifyPackage.getMessageBody(), receiveSystemNotify.getMessageBody());
        Assert.assertTrue(receiveSystemNotify.getMsgId() > 0);

    }

    @Test
    /**
     * A-->B发送系统通知
     * 需要接受端确认
     */
    public void sendSystemNotifyWithoutAck() throws InterruptedException {

        SystemNotifyPackage notifyPackage = new SystemNotifyPackage();

        PacketHead head = new PacketHead();
        head.setTempId(tempId.getAndIncrement());
        notifyPackage.setPacketHead(head);
        CountDownLatch idStatusLatch = new CountDownLatch(1);
        autoTest.getIdStatusLatchMap().put(head.getTempId(), idStatusLatch);

        notifyPackage.setMsgType((byte) 0x00);

        //需要确认
        notifyPackage.setServeType((byte) 0x00);

        notifyPackage.setFrom(fromUserId);
        notifyPackage.setTo(toUserId);
        notifyPackage.setExpired(0l);
        notifyPackage.setPushContentLength((short) 0);
        notifyPackage.setMesssageLength((short) systemNotifyContent.length());
        notifyPackage.setMessageBody(systemNotifyContent);
        notifyPackage.setMsgId(0);

        CountDownLatch notifyLatch = new CountDownLatch(1);
        autoTest.getSystemNotifyCountDownLatch().put(toUserId, notifyLatch);

        tcpService.sendSystemNofify(fromUserId, notifyPackage);

        notifyLatch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);
        idStatusLatch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);

        //服务端返回消息已经接受到
        Assert.assertEquals(MessageStatus.SERVER_RECEIVED, autoTest.getIdStatusMap().get(head.getTempId()));
        SystemNotifyPackage receiveSystemNotify = autoTest.getSystemNotifyPackage();
        Assert.assertNotNull(receiveSystemNotify);
        Assert.assertEquals(fromUserId, receiveSystemNotify.getFrom());
        Assert.assertEquals(toUserId, receiveSystemNotify.getTo());
        Assert.assertEquals(notifyPackage.getExpired(), receiveSystemNotify.getExpired());
        Assert.assertEquals(notifyPackage.getPushContentLength(), receiveSystemNotify.getPushContentLength());
        Assert.assertEquals(notifyPackage.getPushContentBody(), receiveSystemNotify.getPushContentBody());
        Assert.assertEquals(notifyPackage.getMesssageLength(), receiveSystemNotify.getMesssageLength());
        Assert.assertEquals(notifyPackage.getMessageBody(), receiveSystemNotify.getMessageBody());
        Assert.assertEquals(0, receiveSystemNotify.getMsgId());
    }


    @Test
//    @Ignore
    public void testSendGameCallReqPacket() throws InterruptedException {

        GameCallReqPacket gameCallReqPacket = new GameCallReqPacket();
        PacketHead packetHead = new PacketHead();
        packetHead.setVersion((byte) 0x04);
        packetHead.setPacketType(GameCallReqPacket.COMMAND_PACKET_TYPE);
        gameCallReqPacket.setPacketHead(packetHead);


        gameCallReqPacket.setCallType(GameCallPacket.CallType.CALL);
        gameCallReqPacket.setPeerName(toUserId);
        gameCallReqPacket.setSsrc(new byte[]{(byte) 0x01, (byte) 0x02});
        gameCallReqPacket.setSubCallTypes(new Integer[]{(int) GameCallPacket.SUB_CALL_TYPE_VIDEO, 1000});

        gameCallReqPacket.setExtraData(new byte[]{(byte) 0x01, (byte) 0x02, (byte) 0x03});


        CountDownLatch callLatch = new CountDownLatch(1);
        autoTest.getGameCallCountDownLatch().put(GameCallPacket.CallType.CALL, callLatch);

        //发送game呼叫
        tcpService.sendGameCallReqPacket(fromUserId, gameCallReqPacket);

        callLatch.await();
        GameCallRespPacket respPacket = autoTest.getGameCallRespPacket();
        Assert.assertNotNull(respPacket);

        //等待game呼叫的响应
        if (fromUserId.equals(toUserId)) {
            callLatch = new CountDownLatch(2);
            autoTest.getCallUDPServerPacketLatchMap().put(fromUserId, callLatch);
        } else {
            callLatch = new CountDownLatch(1);
            autoTest.getCallUDPServerPacketLatchMap().put(fromUserId, callLatch);
            CountDownLatch toCallLatch = new CountDownLatch(1);
            autoTest.getCallUDPServerPacketLatchMap().put(toUserId, toCallLatch);
        }

        //接受方发送已经接受到指令
        gameCallReqPacket.setPeerName(fromUserId);
        gameCallReqPacket.setCallType(GameCallPacket.CallType.RECEIVED);
        tcpService.sendGameCallReqPacket(toUserId, gameCallReqPacket);

        autoTest.getCallUDPServerPacketLatchMap().get(fromUserId).await();
        autoTest.getCallUDPServerPacketLatchMap().get(toUserId).await();
        Assert.assertNotNull(autoTest.getCallUDPServerPacketMap().get(fromUserId));
        Assert.assertNotNull(autoTest.getCallUDPServerPacketMap().get(toUserId));

        //发送辅助呼叫
        callLatch = new CountDownLatch(1);
        autoTest.getGameCallCountDownLatch().put(GameCallPacket.CallType.ASSIST_CALL, callLatch);
        gameCallReqPacket.setPeerName(toUserId);
        gameCallReqPacket.setCallType(GameCallPacket.CallType.ASSIST_CALL);
        tcpService.sendGameCallReqPacket(fromUserId, gameCallReqPacket);

        callLatch.await();
        respPacket = autoTest.getGameCallRespPacket();
        Assert.assertNotNull(respPacket);
        Assert.assertEquals(GameCallPacket.CallType.ASSIST_CALL, respPacket.getCallType());

        //发送接受指令
        callLatch = new CountDownLatch(1);
        autoTest.getGameCallCountDownLatch().put(GameCallPacket.CallType.ACCEPTE, callLatch);

        //等待game呼叫的响应
        if (fromUserId.equals(toUserId)) {
            CountDownLatch gameServerLatch = new CountDownLatch(2);
            autoTest.getUserGameServerCountDownlatch().put(fromUserId, gameServerLatch);
        } else {
            CountDownLatch gameServerLatch = new CountDownLatch(1);
            autoTest.getUserGameServerCountDownlatch().put(fromUserId, gameServerLatch);
            CountDownLatch toGameServerLatch = new CountDownLatch(1);
            autoTest.getUserGameServerCountDownlatch().put(toUserId, toGameServerLatch);
        }


        gameCallReqPacket.setPeerName(fromUserId);
        gameCallReqPacket.setCallType(GameCallPacket.CallType.ACCEPTE);
        tcpService.sendGameCallReqPacket(toUserId, gameCallReqPacket);

        callLatch.await();
        respPacket = autoTest.getGameCallRespPacket();
        Assert.assertNotNull(respPacket);
        Assert.assertEquals(GameCallPacket.CallType.ACCEPTE, respPacket.getCallType());


        //测试 game server包
        autoTest.getUserGameServerCountDownlatch().get(fromUserId).await();
        autoTest.getUserGameServerCountDownlatch().get(toUserId).await();
        Assert.assertNotNull(autoTest.getUserGameServerInfo().get(fromUserId));
        Assert.assertNotNull(autoTest.getUserGameServerInfo().get(toUserId));

        //测试 忙
        callLatch = new CountDownLatch(1);
        autoTest.getGameCallCountDownLatch().put(GameCallPacket.CallType.BUSY, callLatch);
        gameCallReqPacket.setPeerName(fromUserId);
        gameCallReqPacket.setCallType(GameCallPacket.CallType.BUSY);
        tcpService.sendGameCallReqPacket(toUserId, gameCallReqPacket);
        callLatch.await();
        respPacket = autoTest.getGameCallRespPacket();
        Assert.assertNotNull(respPacket);

        //测试 挂断
        callLatch = new CountDownLatch(1);
        autoTest.getGameCallCountDownLatch().put(GameCallPacket.CallType.HANGUP, callLatch);
        GameCallHandupReqPacket handupReqPacket = new GameCallHandupReqPacket();
        handupReqPacket.setPacketHead(packetHead);


        handupReqPacket.setCallType(GameCallPacket.CallType.CALL);
        handupReqPacket.setPeerName(toUserId);
        handupReqPacket.setSsrc(new byte[]{(byte) 0x01, (byte) 0x02});
        handupReqPacket.setSubCallTypes(new Integer[]{(int) GameCallPacket.SUB_CALL_TYPE_VIDEO, 0x03});

        handupReqPacket.setExtraData(new byte[]{(byte) 0x01, (byte) 0x02, (byte) 0x03});

        handupReqPacket.setPeerName(fromUserId);
        handupReqPacket.setCallType(GameCallPacket.CallType.HANGUP);
        handupReqPacket.setDesCode(GameCallHandupReqPacket.DesCode.UN_ACCEPT);

        tcpService.sendGameCallReqPacket(toUserId, handupReqPacket);
        callLatch.await();
        respPacket = autoTest.getGameCallRespPacket();
        Assert.assertNotNull(respPacket);

        //测试 拒绝
        callLatch = new CountDownLatch(1);
        autoTest.getGameCallCountDownLatch().put(GameCallPacket.CallType.REJECT, callLatch);
        gameCallReqPacket.setPeerName(fromUserId);
        gameCallReqPacket.setCallType(GameCallPacket.CallType.REJECT);
        tcpService.sendGameCallReqPacket(toUserId, gameCallReqPacket);
        callLatch.await();
        respPacket = autoTest.getGameCallRespPacket();
        Assert.assertNotNull(respPacket);

        System.out.println("over");
    }

//    @Test
//    public void testSendGameCallReqPacketWithoutGame() throws InterruptedException {
//
//        GameCallReqPacket gameCallReqPacket = new GameCallReqPacket();
//        PacketHead packetHead = new PacketHead();
//        packetHead.setVersion((byte) 0x04);
//        packetHead.setPacketType(GameCallReqPacket.COMMAND_PACKET_TYPE);
//        gameCallReqPacket.setPacketHead(packetHead);
//
//
//        gameCallReqPacket.setCallType(GameCallPacket.CallType.CALL);
//        gameCallReqPacket.setPeerName(toUserId);
//        gameCallReqPacket.setSsrc(new byte[]{(byte) 0x01, (byte) 0x02});
//        gameCallReqPacket.setSubCallTypes(new Integer[]{0x03});
//
//        gameCallReqPacket.setExtraData(new byte[]{(byte) 0x01, (byte) 0x02, (byte) 0x03});
//
//
//        CountDownLatch callLatch = new CountDownLatch(1);
//        autoTest.getGameCallCountDownLatch().put(GameCallPacket.CallType.CALL, callLatch);
//
//        //发送game呼叫
//        tcpService.sendGameCallReqPacket(fromUserId, gameCallReqPacket);
//
//        callLatch.await();
//        GameCallRespPacket respPacket = autoTest.getGameCallRespPacket();
//        Assert.assertNotNull(respPacket);
//
//        //等待game呼叫的响应
//        if (fromUserId.equals(toUserId)) {
//            callLatch = new CountDownLatch(2);
//            autoTest.getCallUDPServerPacketLatchMap().put(fromUserId, callLatch);
//        } else {
//            callLatch = new CountDownLatch(1);
//            autoTest.getCallUDPServerPacketLatchMap().put(fromUserId, callLatch);
//            CountDownLatch toCallLatch = new CountDownLatch(1);
//            autoTest.getCallUDPServerPacketLatchMap().put(toUserId, toCallLatch);
//        }
//
//        //接受方发送已经接受到指令
//        gameCallReqPacket.setPeerName(fromUserId);
//        gameCallReqPacket.setCallType(GameCallPacket.CallType.RECEIVED);
//        tcpService.sendGameCallReqPacket(toUserId, gameCallReqPacket);
//
//        autoTest.getCallUDPServerPacketLatchMap().get(fromUserId).await();
//        autoTest.getCallUDPServerPacketLatchMap().get(toUserId).await();
//        Assert.assertNotNull(autoTest.getCallUDPServerPacketMap().get(fromUserId));
//        Assert.assertNotNull(autoTest.getCallUDPServerPacketMap().get(toUserId));
//
//        //发送辅助呼叫
//        callLatch = new CountDownLatch(1);
//        autoTest.getGameCallCountDownLatch().put(GameCallPacket.CallType.ASSIST_CALL, callLatch);
//        gameCallReqPacket.setPeerName(toUserId);
//        gameCallReqPacket.setCallType(GameCallPacket.CallType.ASSIST_CALL);
//        tcpService.sendGameCallReqPacket(fromUserId, gameCallReqPacket);
//
//        callLatch.await();
//        respPacket = autoTest.getGameCallRespPacket();
//        Assert.assertNotNull(respPacket);
//        Assert.assertEquals(GameCallPacket.CallType.ASSIST_CALL, respPacket.getCallType());
//
//        //发送接受指令
//        callLatch = new CountDownLatch(1);
//        autoTest.getGameCallCountDownLatch().put(GameCallPacket.CallType.ACCEPTE, callLatch);
//
//        //等待game呼叫的响应
//        if (fromUserId.equals(toUserId)) {
//            CountDownLatch gameServerLatch = new CountDownLatch(2);
//            autoTest.getUserGameServerCountDownlatch().put(fromUserId, gameServerLatch);
//        } else {
//            CountDownLatch gameServerLatch = new CountDownLatch(1);
//            autoTest.getUserGameServerCountDownlatch().put(fromUserId, gameServerLatch);
//            CountDownLatch toGameServerLatch = new CountDownLatch(1);
//            autoTest.getUserGameServerCountDownlatch().put(toUserId, toGameServerLatch);
//        }
//
//
//        gameCallReqPacket.setPeerName(fromUserId);
//        gameCallReqPacket.setCallType(GameCallPacket.CallType.ACCEPTE);
//        tcpService.sendGameCallReqPacket(toUserId, gameCallReqPacket);
//
//        callLatch.await();
//        respPacket = autoTest.getGameCallRespPacket();
//        Assert.assertNotNull(respPacket);
//        Assert.assertEquals(GameCallPacket.CallType.ACCEPTE, respPacket.getCallType());
//
//
//        //测试 game server包
//        autoTest.getUserGameServerCountDownlatch().get(fromUserId).await();
//        autoTest.getUserGameServerCountDownlatch().get(toUserId).await();
//        Assert.assertNotNull(autoTest.getUserGameServerInfo().get(fromUserId));
//        Assert.assertNotNull(autoTest.getUserGameServerInfo().get(toUserId));
//
//        //测试 忙
//        callLatch = new CountDownLatch(1);
//        autoTest.getGameCallCountDownLatch().put(GameCallPacket.CallType.BUSY, callLatch);
//        gameCallReqPacket.setPeerName(fromUserId);
//        gameCallReqPacket.setCallType(GameCallPacket.CallType.BUSY);
//        tcpService.sendGameCallReqPacket(toUserId, gameCallReqPacket);
//        callLatch.await();
//        respPacket = autoTest.getGameCallRespPacket();
//        Assert.assertNotNull(respPacket);
//
//        //测试 挂断
//        callLatch = new CountDownLatch(1);
//        autoTest.getGameCallCountDownLatch().put(GameCallPacket.CallType.HANGUP, callLatch);
//        GameCallHandupReqPacket handupReqPacket = new GameCallHandupReqPacket();
//        handupReqPacket.setPacketHead(packetHead);
//
//
//        handupReqPacket.setCallType(GameCallPacket.CallType.CALL);
//        handupReqPacket.setPeerName(toUserId);
//        handupReqPacket.setSsrc(new byte[]{(byte) 0x01, (byte) 0x02});
//        handupReqPacket.setSubCallTypes(new Integer[]{(int) GameCallPacket.SUB_CALL_TYPE_VIDEO, 0x03});
//
//        handupReqPacket.setExtraData(new byte[]{(byte) 0x01, (byte) 0x02, (byte) 0x03});
//
//        handupReqPacket.setPeerName(fromUserId);
//        handupReqPacket.setCallType(GameCallPacket.CallType.HANGUP);
//        handupReqPacket.setDesCode(GameCallHandupReqPacket.DesCode.UN_ACCEPT);
//
//        tcpService.sendGameCallReqPacket(toUserId, handupReqPacket);
//        callLatch.await();
//        respPacket = autoTest.getGameCallRespPacket();
//        Assert.assertNotNull(respPacket);
//
//        //测试 拒绝
//        callLatch = new CountDownLatch(1);
//        autoTest.getGameCallCountDownLatch().put(GameCallPacket.CallType.REJECT, callLatch);
//        gameCallReqPacket.setPeerName(fromUserId);
//        gameCallReqPacket.setCallType(GameCallPacket.CallType.REJECT);
//        tcpService.sendGameCallReqPacket(toUserId, gameCallReqPacket);
//        callLatch.await();
//        respPacket = autoTest.getGameCallRespPacket();
//        Assert.assertNotNull(respPacket);
//
//        System.out.println("over");
//    }


    @Test
    public void testReSendSimpleTextMsgWithZeroFlag() throws InterruptedException, UnsupportedEncodingException {
        byte msgFlag = (byte) 0x00;
        String cmsgId = "10aa"+System.currentTimeMillis();

        TextMessagePacket simpleMessagePacket = new TextMessagePacket();
        simpleMessagePacket.setFrom(fromUserId);
        simpleMessagePacket.setToUser(toUserId);
        simpleMessagePacket.setContent(textMsg.getBytes(Charset.forName("utf-8")));
        simpleMessagePacket.setMessageServiceType(SimpleMessagePacket.TO_USER);
        simpleMessagePacket.setCmsgid(cmsgId);


        PacketHead head = new PacketHead();
        head.setTempId(tempId.getAndIncrement());
        simpleMessagePacket.setPacketHead(head);

        CountDownLatch latch = new CountDownLatch(1);
        autoTest.getIdStatusLatchMap().put(head.getTempId(), latch);

        CountDownLatch packetLatch = new CountDownLatch(1);
        autoTest.getPacketTypeCountDownLatch().put(TextMessagePacket.TEXT_MESSAGE_PACKET_TYPE, packetLatch);

        //第一次发送
        simpleMessagePacket.setMsgFlag(msgFlag);
        tcpService.sendSimpleMessagePacket(simpleMessagePacket);

        msgFlag = (byte)0x01;
        //第二次发送
        simpleMessagePacket.setMsgFlag(msgFlag);
        tcpService.sendSimpleMessagePacket(simpleMessagePacket);

        //第二次发送
        simpleMessagePacket.setMsgFlag(msgFlag);
        tcpService.sendSimpleMessagePacket(simpleMessagePacket);

        latch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);
        Assert.assertEquals(MessageStatus.SERVER_RECEIVED, autoTest.getIdStatusMap().get(head.getTempId()));

        packetLatch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);
        TextMessagePacket responsePacket = autoTest.getReceiveTextMsg();
        Assert.assertNotNull(responsePacket);
        Assert.assertEquals(textMsg, new String(responsePacket.getContent(), "utf-8"));
        Assert.assertEquals(fromUserId, responsePacket.getFrom());
        Assert.assertTrue(responsePacket.getMessageId() > 0);
    }

    @Test
    public void testReSendSimpleTextMsgWithoutZeroFlag() throws InterruptedException, UnsupportedEncodingException {
        byte msgFlag = (byte) 0x01;
        String cmsgId = "10aa"+System.currentTimeMillis();

        TextMessagePacket simpleMessagePacket = new TextMessagePacket();
        simpleMessagePacket.setFrom(fromUserId);
        simpleMessagePacket.setToUser(toUserId);
        simpleMessagePacket.setContent(textMsg.getBytes(Charset.forName("utf-8")));
        simpleMessagePacket.setMessageServiceType(SimpleMessagePacket.TO_USER);
        simpleMessagePacket.setCmsgid(cmsgId);


        PacketHead head = new PacketHead();
        head.setTempId(tempId.getAndIncrement());
        simpleMessagePacket.setPacketHead(head);

        CountDownLatch latch = new CountDownLatch(1);
        autoTest.getIdStatusLatchMap().put(head.getTempId(), latch);

        CountDownLatch packetLatch = new CountDownLatch(1);
        autoTest.getPacketTypeCountDownLatch().put(TextMessagePacket.TEXT_MESSAGE_PACKET_TYPE, packetLatch);

        //第一次发送
        simpleMessagePacket.setMsgFlag(msgFlag);
        tcpService.sendSimpleMessagePacket(simpleMessagePacket);

        msgFlag = (byte)0x01;
        //第二次发送
        simpleMessagePacket.setMsgFlag(msgFlag);
        tcpService.sendSimpleMessagePacket(simpleMessagePacket);

        //第二次发送
        simpleMessagePacket.setMsgFlag(msgFlag);
        tcpService.sendSimpleMessagePacket(simpleMessagePacket);

        latch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);
        Assert.assertEquals(MessageStatus.SERVER_RECEIVED, autoTest.getIdStatusMap().get(head.getTempId()));

        packetLatch.await(awaitTimeMilliSecond, TimeUnit.MILLISECONDS);
        TextMessagePacket responsePacket = autoTest.getReceiveTextMsg();
        Assert.assertNotNull(responsePacket);
        Assert.assertEquals(textMsg, new String(responsePacket.getContent(), "utf-8"));
        Assert.assertEquals(fromUserId, responsePacket.getFrom());
        Assert.assertTrue(responsePacket.getMessageId() > 0);
    }


}

