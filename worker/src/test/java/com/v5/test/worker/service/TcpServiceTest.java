package com.v5.test.worker.service;

import com.v5.base.packet.PacketHead;
import com.v5.test.worker.bean.MessageInfo;
import com.v5.test.worker.bean.TaskSnapshort;
import com.v5.test.worker.client.ClientOnclientManager;
import com.v5.test.worker.client.gameCall.GameCallPacket;
import com.v5.test.worker.client.gameCall.GameCallReqPacket;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by piguangtao on 14-3-18.
 * 前置条件:
 * 用户连接准备：
 *      用户连接tcpServer-->ClientPacketDecodeAndEncodeHandler#channelActive-->EventHandler#tcpConnectSuccess-->tcpService.login(ctx.channel());
 * 用户业务测试:
 *      获取成功登录的用户:
 *       TaskSnapshort.loginedUserSet.take();
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/conf/main_test.xml")
public class TcpServiceTest {
    @Autowired
    private HttpService httpService;

    @Autowired
    private TcpService tcpService;

    @Autowired
    private ClientOnclientManager onclientManager;

    @Before
    public void setUp() throws Exception {
        //支持进行tcp登录，在数据库中需要有用户的记录，包括
        //insert into user_session_indexes(session_id,user_id,app_id) values('cfcd208495d565ef66e7dff9f98764da','c4ca4238a0b923820dcc509a6f75849b',0);
        //insert into user_sessions(session_id,user_id,app_id) values('cfcd208495d565ef66e7dff9f98764da','c4ca4238a0b923820dcc509a6f75849b',0);
        //insert into users(id,nickname,user_type,mobile_verify,app_id,create_time) values('c4ca4238a0b923820dcc509a6f75849b','test',0,0,0,'2014-03-11 13:40:14+0800');
//        String userId = "6a6e0440d68b11e384f669f51b0e7dca";
//        String sessionId = "025f0c1ffa11474a8efc5eb0b1d9c6de";

        String userId = "6a6e0440d68b11e384f669f51b0e7dca";
        String sessionId = userId;

        List<String> userIds = new ArrayList<>();
        userIds.add(userId);

        //初始化用户的nameMd5和sessionId
        onclientManager.saveSession(sessionId, userId);

        for(int i = 0;i<userIds.size();i++){
            tcpService.connect(userIds.get(i));
        }
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testHeatBeat() throws InterruptedException {
        CountDownLatch latch =new CountDownLatch(1);
        latch.await();
    }

    @Test
    public void testLogin() throws Exception {

    }

    @Test
    public void testSendSimpleMessagePacket() throws Exception {
        String to = "88888888888888888888888888888888";
        //http 登录成功后，会自动tcp连接和登录
        while (true){
            String userId = TaskSnapshort.loginedUserSet.take();
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setContent("simple test");
            messageInfo.setFrom(userId);
            messageInfo.setTo(userId);
            for(int i = 0;i<10;i++){
                tcpService.sendSimpleMessagePacket(tcpService.formTextSimpleMessagePacket(messageInfo));
            }
        }
    }

    @Test
    public void testSendTransMsg() throws InterruptedException {
        String userId = TaskSnapshort.loginedUserSet.take();
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setContent("simple test");
        messageInfo.setFrom(userId);
        messageInfo.setTo(userId);
        for(int i = 0;i<10;i++){
            tcpService.sendTransMessagePacket(messageInfo);
        }
        System.out.println("over");

    }

    @Test
    public void testSendGameCallReqPacket() throws InterruptedException {
        String userId = TaskSnapshort.loginedUserSet.take();
        GameCallReqPacket gameCallReqPacket = new GameCallReqPacket();
        PacketHead packetHead = new PacketHead();
        packetHead.setVersion((byte)0x04);
        packetHead.setPacketType(GameCallReqPacket.COMMAND_PACKET_TYPE);
        gameCallReqPacket.setPacketHead(packetHead);


        gameCallReqPacket.setCallType(GameCallPacket.CallType.CALL);
        gameCallReqPacket.setPeerName(userId);
        gameCallReqPacket.setSsrc(new byte[]{(byte)0x01,(byte)0x02});
        gameCallReqPacket.setSubCallTypes(new Integer[]{(int)GameCallPacket.SUB_CALL_TYPE_VIDEO,0x03});

        gameCallReqPacket.setExtraData(new byte[]{(byte)0x01,(byte)0x02,(byte)0x03});


        //发送game呼叫
        tcpService.sendGameCallReqPacket(userId,gameCallReqPacket);

        //等待game呼叫的响应


        //发送已经接受

        System.out.println("over");
    }

    private void noExit() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
