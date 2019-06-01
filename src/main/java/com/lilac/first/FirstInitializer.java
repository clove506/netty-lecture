package com.lilac.first;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * Created by lilac on 2019-06-01.
 */
public class FirstInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {


        // 管道 包含多个 handler
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast("httpServerCodec", new HttpServerCodec());

        pipeline.addLast("httpServerResp", new FirstHttpServerHandler());

    }
}