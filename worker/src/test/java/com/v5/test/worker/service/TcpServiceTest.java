package com.v5.test.worker.service;

import com.v5.base.utils.AsyncInvokeExceptoin;
import com.v5.base.utils.SimpleCallback;
import com.v5.test.worker.bean.MessageInfo;
import com.v5.test.worker.bean.TaskSnapshort;
import com.v5.test.worker.bean.User;
import com.v5.test.worker.client.ClientOnclientManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.TypeReference;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.CountDownLatch;

/**
 * Created by piguangtao on 14-3-18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/conf/main_test.xml")
public class TcpServiceTest {
    String mobile = "13770508309";
    @Autowired
    private HttpService httpService;

    @Autowired
    private TcpService tcpService;

    @Autowired
    private ClientOnclientManager onclientManager;

    @Before
    public void setUp() throws Exception {
        //http登录
        final CountDownLatch latch = new CountDownLatch(1);
        final User finalUser = new User();
        for(int i = 0;i<20;i++){
            String mobile = "138705083010"+i;
            httpService.bindDevice(mobile, true, new SimpleCallback<User, AsyncInvokeExceptoin>() {
                @Override
                public void success(User user) {
                    System.out.format("user: %s", user);
                    finalUser.setId(user.getId());
                    finalUser.setSessionId(user.getSessionId());
                    finalUser.setAppId(user.getAppId());
                    latch.countDown();
                }

                @Override
                public void failure(AsyncInvokeExceptoin exceptoin) {
                    System.out.format("user: %s", exceptoin);
                    latch.countDown();
                    return;
                }
            });
        }


        latch.await();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testConnect() throws Exception {

    }

    @Test
    public void testLogin() throws Exception {

    }

    @Test
    public void testSendSimpleMessagePacket() throws Exception {
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
//        noExit();
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
