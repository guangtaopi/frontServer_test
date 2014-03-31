package com.v5.test.worker.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by piguangtao on 14-3-21.
 */
public class UserPool {
    private static Logger LOGGER = LoggerFactory.getLogger(UserPool.class);

    private static LinkedBlockingDeque<User> successLoginUserQueue = new LinkedBlockingDeque(20000);

    private static LinkedBlockingDeque<MessageInfo> toSendMsgQueue = new LinkedBlockingDeque(10000);


    public static void addSuccessLoginedUser(User user) {
        try {
            successLoginUserQueue.put(user);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static User taskLoginedUser() {
        User user = null;
        try {
            user = successLoginUserQueue.take();
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return user;
    }

    public static User resetUser() {
        User user = null;
        try {
            user = successLoginUserQueue.take();
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return user;
    }

     public static void addToSendMsg(MessageInfo messageInfo) {
        try {
            toSendMsgQueue.put(messageInfo);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static MessageInfo takeToSendMsg() {
        MessageInfo messageInfo = null;
        try {
            messageInfo = toSendMsgQueue.take();
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return messageInfo;
    }

}
