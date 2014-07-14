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

    String TASK_SEND_OVER = "/task/send/over";

    String USER_TRANS_MSG_RECEIVE = "/test/user/trans/msg/receive";

    String USER_IMAGE_MSG_RECEIVE = "/test/user/image/msg/receive";

    String USER_AUDIO_MSG_RECEIVE = "/test/user/audio/msg/receive";

    String USER_MSG_STATUS = "/user/msg/status";

    String USER_CALL_RECEIVE_REQUEST = "/user/call/receive/reqeust";

    String USER_CALL_UDP_SERVER_RECEIVE = "/user/call/udpServer/receive";

    String USER_SYSTEM_NOTIFY_RECEIVE = "/user/systemNotify/receive";

    String USER_GAME_CALL_RECEIVE = "/user/gamecall/receive";

    String USER_GAME_SERVER_RECEIVE = "/user/gameserver/receive";

}
