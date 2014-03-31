package com.v5.test.worker.client;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.v5.base.client.ClientChannel;
import com.v5.base.client.OnlineClient;
import com.v5.base.client.UserInfo;
import com.v5.base.utils.AsyncInvokeExceptoin;
import com.v5.base.utils.SimpleCallback;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Created by piguangtao on 14-2-19.
 */
public class ClientOnclientManager implements InitializingBean{

    @Autowired
    private MetricRegistry metrics;

    private Counter clientCount;

    private static Logger LOGGER = LoggerFactory.getLogger(ClientOnclientManager.class);

    private ConcurrentHashMap<String, String> userSession = new ConcurrentHashMap<String, String>();

    private ConcurrentHashMap<Channel, String> channelUser = new ConcurrentHashMap<Channel, String>();

    private Map<Serializable, OnlineClient> onlineClientMap = new ConcurrentHashMap<Serializable, OnlineClient>();

    @Override
    public void afterPropertiesSet() throws Exception {
        clientCount = metrics.counter("tcp-client-num");
    }


    public void saveSession(String sessionId, String userMd5) {
        Assert.notNull(sessionId);
        Assert.notNull(userMd5);
        userSession.put(userMd5, sessionId);
    }

    public String getSessionByNameMd5(String userMd5) {
        return userSession.get(userMd5);
    }

    public void setChannnelUser(Channel channnel, String userMd5) {
        channelUser.put(channnel, userMd5);
    }


    public String getUserByChannel(Channel channel) {
        return channelUser.get(channel);
    }

    public void addClient(OnlineClient client) {
        LOGGER.debug("add online client, id={}", client.getUserId());
        onlineClientMap.put(client.getUserId(), client);
        clientCount.inc();
    }

    public void removeClient(Serializable id, ClientChannel clientChannel) {
        LOGGER.debug("remove online client, id={}", id);
        onlineClientMap.remove(id);
        clientCount.dec();
    }

    public void getClient(final Serializable id, final SimpleCallback<OnlineClient, AsyncInvokeExceptoin> callback) {

        if (callback == null) {
            return;
        }

        if (onlineClientMap.containsKey(id)) {
            callback.success(onlineClientMap.get(id));
        }

    }


    public OnlineClient addOnlineClient(String sessionId,String nameMd5, ClientChannel clientChannel) {
        LOGGER.debug("[save-on-client].nameMd5:{},channel:{}", nameMd5,clientChannel);
        UserInfo userInfo = new UserInfo(sessionId, nameMd5);
        OnlineClient client = new OnlineClient(clientChannel, userInfo,null);
        clientChannel.setOnlineClient(client);
        addClient(client);
        return client;
    }

}
