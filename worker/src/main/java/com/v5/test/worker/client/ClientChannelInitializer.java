package com.v5.test.worker.client;

import com.v5.base.event.EventPublisher;
import com.v5.base.handler.PacketHandlerManager;
import com.v5.base.packet.PacketDecoderAndEncoderManager;
import com.v5.test.worker.ssl.SSLUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.net.ssl.SSLEngine;
import java.util.concurrent.TimeUnit;

/**
 * Created by piguangtao on 14-2-19.
 */
public class ClientChannelInitializer extends ChannelInitializer {

    @Autowired
    private PacketDecoderAndEncoderManager packetDecoderAndEncoderManager;

    @Autowired
    private PacketHandlerManager packetHandlerManager;

    @Autowired
    private ClientOnclientManager onlineClientManager;

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private SSLUtil sslUtil;

    @Value("${client.ssl.enable}")
    private String sslEnable;

    @Override
    protected void initChannel(Channel ch) throws Exception {
        SSLEngine sslEngine = sslUtil.newSSLContext().createSSLEngine();
        sslEngine.setUseClientMode(true);

        if ("yes".equalsIgnoreCase(sslEnable)) {
            ch.pipeline().addLast("ssl", new SslHandler(sslEngine));
        }

        ch.pipeline().addLast("logger", new LoggingHandler(LogLevel.DEBUG))
                .addLast("keep_alive", new IdleStateHandler(0, 60, 0, TimeUnit.SECONDS))
                .addLast("frame_decoder", new LengthFieldBasedFrameDecoder(65551, 12, 2, 2, 0, true))
                .addLast("base_decode_encoder", decodeEncoderHandler());
    }

    protected ClientPacketDecodeAndEncodeHandler decodeEncoderHandler() {
        ClientPacketDecodeAndEncodeHandler handler = new ClientPacketDecodeAndEncodeHandler();
        handler.setDecoderAndEncoderManager(packetDecoderAndEncoderManager);
        handler.setPacketHandlerManager(packetHandlerManager);
        handler.setOnlineClientManager(onlineClientManager);
        handler.setEventPublisher(eventPublisher);
        return handler;
    }
}
