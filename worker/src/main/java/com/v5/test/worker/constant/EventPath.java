package com.v5.test.worker.constant;

/**
 * Created by piguangtao on 14-1-21.
 */
public interface EventPath {
    String TASK_RECEIVE = "/test/task/receive";

    String REGISTER_TO_CONTROLLER_SUCCESS = "/test/controller/register/success";

    String USER_HTTP_LOGIN_SUCCESS = "/test/user/http/login/success";

    String USER_TCP_CONNECTED = "/test/user/tcp/connected";

    String USER_TCP_DISCONNECTED = "/test/user/tcp/disconnected";

    String USER_TCP_LOGIN_SUCCESS = "/test/user/tcp/login/success";

    String USER_MSG_SEND = "/test/user/msg/send";

    String USER_MSG_RECEIVE = "/test/user/msg/receive";


}
