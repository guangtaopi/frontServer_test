package com.v5.test.worker.task;

import com.v5.base.event.EventPublisher;
import com.v5.base.utils.AsyncInvokeExceptoin;
import com.v5.base.utils.DefaultCallBack;
import com.v5.base.utils.SimpleCallback;
import com.v5.test.worker.bean.User;
import com.v5.test.worker.constant.EventPath;
import com.v5.test.worker.service.HttpService;
import com.v5.test.worker.service.TcpService;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import sun.java2d.pipe.SpanShapeRenderer;

import java.io.IOException;

/**
 * Created by piguangtao on 14-3-21.
 */
public class UserLoginTask extends Thread implements InitializingBean {

    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    private String phonePrefix = "100000000";

    @Value("${text.main.content.seq.start}")
    private int startSeq;

    @Value("${text.main.content.seq.end}")
    private int endSeq;

    @Autowired
    private HttpService httpService;

    @Autowired
    private EventPublisher eventPublisher;

    private boolean loginFromHttpServer;

    @Override
    public void afterPropertiesSet() throws Exception {
        loginFromHttpServer = false;
        start();
    }

    public void run() {
        LOGGER.debug("start to bind device.");
        for (int i = startSeq; i <= endSeq; i++) {
            if(loginFromHttpServer){
                loginFromHttpServer(i);
            }
            else{
                loginFromTcpServer(i);
            }
        }
        LOGGER.debug("end to bind device");
    }

    private void loginFromTcpServer(int num) {
        User user = new User();
        user.setId(DigestUtils.md5Hex(String.valueOf(num+1)));
        user.setSessionId(DigestUtils.md5Hex(String.valueOf(num)));
        eventPublisher.send(EventPath.USER_HTTP_LOGIN_SUCCESS,user);
    }

    private void loginFromHttpServer(int num) {
        final String mobile = phonePrefix + num;
        try {
            bindDevice(mobile, false, new SimpleCallback<Boolean, AsyncInvokeExceptoin>() {
                @Override
                public void success(Boolean aBoolean) {
                    if (!aBoolean) {
                        reBindDeviceWithAuthcode(mobile);
                    }
                }

                @Override
                public void failure(AsyncInvokeExceptoin exceptoin) {
                    reBindDeviceWithAuthcode(mobile);
                }
            });
        } catch (Exception e) {
            reBindDeviceWithAuthcode(mobile);
        }
    }

    private void bindDevice(final String mobile, boolean isAuth, final SimpleCallback<Boolean, AsyncInvokeExceptoin> callback) throws Exception {
        httpService.bindDevice(mobile, isAuth, new SimpleCallback<User, AsyncInvokeExceptoin>() {
            @Override
            public void success(User user) {
                callback.success(true);
            }

            @Override
            public void failure(AsyncInvokeExceptoin exceptoin) {
                callback.failure(exceptoin);
            }
        });
    }

    private void reBindDeviceWithAuthcode(final String mobile) {
        try {
            httpService.bindDevice(mobile, true, new SimpleCallback<User, AsyncInvokeExceptoin>() {
                @Override
                public void success(User user) {
                }

                @Override
                public void failure(AsyncInvokeExceptoin exceptoin) {
                    LOGGER.error("mobile:{} fails to bind device with authcode.", mobile, exceptoin);
                }
            });
        } catch (IOException e) {
            LOGGER.error("mobile:{} fails to bind device with authcode.", mobile, e);
        }
    }
}
