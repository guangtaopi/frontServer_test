package com.v5.test.worker.constant;

/**
 * Created with IntelliJ IDEA.
 * User: piguangtao
 * Date: 13-10-17
 * Time: 上午11:12
 */
public interface SystemConstant {

    int THRIFT_BUFFER_SIZE = 10 * 1024 * 1024;


    //连接指令常量
    //请求视频通话
    byte CONN_COMMAND_REQUIRE_VEDIO = 1;

    //请求语音通话
    byte CONN_COMMAND_REQUIRE_VOICE = 17;

    //接受连接
    byte CONN_COMMAND_ACCEPT = 2;

    //拒绝连接
    byte CONN_COMMAND_REFUSE = 3;

    //挂断
    byte CONN_COMMAND_HUNG_UP = 4;

    //对方不在线
    byte CONN_COMMAND_OPPOSITE_OFFLINE = 5;


    //正在通知对方
    byte CONN_COMMAND_BEING_NOTICE = 6;

    //对方正忙
    byte CONN_COMMAND_OPPOSITE_BUSY_NOTICE = 7;

    //UDP Server
    byte  CONN_COMMAND_UDP_SERVER = (byte)0x13;

    /**
     * 用户名称的前缀（昵称）
     */
    String USER_NAME_PREFIX = "test";

    String TEXT_SEQ_CONTEXT_SPLIT = "-";

}
