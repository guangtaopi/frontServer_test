package com.v5.test.worker.service;

import com.v5.base.client.ClientChannel;
import com.v5.test.worker.bean.TaskSnapshort;
import com.v5.test.worker.bean.User;
import com.v5.test.worker.bean.UserPool;
import com.v5.test.worker.client.ClientChannelNettyImpl;
import com.v5.test.worker.client.ClientOnclientManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Created by piguangtao on 14-2-19.
 */
public class UserService {

    @Autowired
    private ClientOnclientManager onlineClientManager;


    public void saveSession(String sessionId,String userMd5){
        onlineClientManager.saveSession(sessionId,userMd5);
    }

    public void saveUserChannel(ChannelHandlerContext ctx){

        String userMd5 = onlineClientManager.getUserByChannel(ctx.channel());

        ClientChannel clientChannel = new ClientChannelNettyImpl(ctx);

        String sessionId = onlineClientManager.getSessionByNameMd5(userMd5);
        Assert.notNull(sessionId);
        onlineClientManager.addOnlineClient(sessionId,userMd5,clientChannel);
    }

    public void loginSuccess(final String nameMd5, final ClientChannel clientChannel){
//        onlineClientManager.addOnlineClient(null, nameMd5, clientChannel);
        TaskSnapshort.loginedUserSet.add(nameMd5);
    }
}
