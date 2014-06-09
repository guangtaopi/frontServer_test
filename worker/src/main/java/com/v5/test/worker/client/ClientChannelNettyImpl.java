package com.v5.test.worker.client;

import com.v5.base.client.ClientChannel;
import com.v5.base.client.ClientChannelNotConnectedExcpetion;
import com.v5.base.client.OnlineClient;
import com.v5.base.message.command.HeartbeatResponsePacket;
import com.v5.base.packet.BasePacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by piguangtao on 14-2-19.
 */
public class ClientChannelNettyImpl implements ClientChannel {

    private static Logger LOGGER = LoggerFactory.getLogger(ClientChannelNettyImpl.class);

    private ChannelHandlerContext channelHandlerContext;

    private OnlineClient onlineClient;

    private boolean connected;

    private ConcurrentHashMap<String,Object> attributes = new ConcurrentHashMap<String, Object>();

    public ClientChannelNettyImpl() {
        connected = false;
    }

    public ClientChannelNettyImpl(ChannelHandlerContext ctx) {
        this();
        this.channelHandlerContext = ctx;
        connected = true;
    }

    public void close() {
        connected = false;
        channelHandlerContext.close();
    }

    public void destory() {
        connected = false;
        this.channelHandlerContext = null;
        if (this.onlineClient != null){
            this.onlineClient.getOnlineClientManager().removeClient(this.getOnlineClient().getUserId(),this);
            this.onlineClient.setClientChannel(null);
            this.onlineClient.setUserInfo(null);
            this.onlineClient = null;
        }
    }

    public void setOnlineClient(OnlineClient onlineClient) {
        this.onlineClient = onlineClient;
    }

    public OnlineClient getOnlineClient() {
        return onlineClient;
    }

    public void write(BasePacket packet) {
        if (connected) {
            Channel channel = this.channelHandlerContext.channel();
            channel.writeAndFlush(packet);

            if(!(packet instanceof HeartbeatResponsePacket)){
                LOGGER.debug("[writeMsg].packet:{},chennel:{}",packet,channel);
            }
        } else {
            LOGGER.error("client channel not connected, can't write.");
            throw new ClientChannelNotConnectedExcpetion();
        }
    }

    public void writeThenClose(BasePacket packet) {
        if (connected) {
            final ChannelFuture f = this.channelHandlerContext.channel().write(packet);
            f.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    f.channel().close();
                }
            });
        } else {
            LOGGER.error("client channel not connected, can't write or close.");
            throw new ClientChannelNotConnectedExcpetion();
        }
    }

    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit timeUnit) {
        if (this.channelHandlerContext != null) {
            return this.channelHandlerContext.executor().schedule(command, delay, timeUnit);
        } else {
            LOGGER.error("channel hander context is null, can't add schedule.");
        }
        return null;
    }



    @Override
    public void setAttribute(String name, Object obj) {
        attributes.put(name,obj);
    }

    @Override
    public Object getAttribute(String name) {
        //TODO
        return attributes.get(name);
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public String getRemoteIp() {
        return channelHandlerContext.channel().remoteAddress().toString();
    }

    @Override
    public int getRemotePort() {
        InetSocketAddress socketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
        return socketAddress.getPort();
    }

    public Channel getChannel(){
        return channelHandlerContext.channel();
    }

    @Override
    public String toString() {
        if (null != channelHandlerContext && null != channelHandlerContext.channel()) {
            return channelHandlerContext.channel().toString();
        } else {
            return "channel_unknown.";
        }
    }
}
