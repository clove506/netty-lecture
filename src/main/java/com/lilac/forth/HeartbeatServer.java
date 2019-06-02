package com.lilac.forth;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by lilac on 2019-06-01.
 */
public class HeartbeatServer {

    public static void main(String[] args) throws InterruptedException {

        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new IdleStateHandler(4, 6, 8, TimeUnit.SECONDS));
                            pipeline.addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                    String eventType = null;
                                    if (evt instanceof IdleStateEvent) {
                                        IdleStateEvent event = (IdleStateEvent) evt;


                                        switch (event.state()) {
                                            case ALL_IDLE:
                                                eventType = "读写空闲";
                                                break;
                                            case READER_IDLE:
                                                eventType = "读空闲";
                                                break;
                                            case WRITER_IDLE:
                                                eventType = "写空闲";
                                                break;
                                            default:
                                                break;
                                        }
                                    }

                                    System.out.println(ctx.channel().remoteAddress() + " 超时事件-> " + eventType);

                                    ctx.channel().close();
                                }

                            });
                        }
                    });

            ChannelFuture channelFuture = bootstrap.bind(8080).sync();

            channelFuture.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
