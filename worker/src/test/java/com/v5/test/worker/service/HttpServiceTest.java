package com.v5.test.worker.service;

import com.v5.base.utils.AsyncInvokeExceptoin;
import com.v5.base.utils.SimpleCallback;
import com.v5.test.worker.bean.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.CountDownLatch;

/**
 * Created by piguangtao on 14-3-18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/conf/main.xml")
public class HttpServiceTest {

    @Autowired
    private HttpService httpService;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testBindDevice() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        String mobile = "13770508309";
        httpService.bindDevice(mobile, true, new SimpleCallback<User, AsyncInvokeExceptoin>() {
            @Override
            public void success(User user) {
                System.out.format("user: %s",user);
                latch.countDown();
            }

            @Override
            public void failure(AsyncInvokeExceptoin exceptoin) {
                System.out.format("user: %s",exceptoin);
                latch.countDown();
            }
        });

        latch.await();
    }


}
