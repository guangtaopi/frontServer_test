package com.v5.test.worker.service;

import com.v5.test.worker.bean.MessageInfo;
import com.v5.test.worker.bean.TaskSnapshort;
import com.v5.test.worker.client.ClientOnclientManager;
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
        List<String> userIds = new ArrayList<>();
        userIds.add("c4ca4238a0b923820dcc509a6f75849b");

        //初始化用户的nameMd5和sessionId
        onclientManager.saveSession("cfcd208495d565ef66e7dff9f98764da", "c4ca4238a0b923820dcc509a6f75849b");

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
