package com.lilac.fifth;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Locale;

/**
 * Created by lilac on 2019-06-01.
 */
public class WebSocketSever {
    public static void main(String[] args) throws InterruptedException {

        /** 基于NIO创建事件循环组 */
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        EventLoopGroup workGroup = new NioEventLoopGroup();

        ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

        try {
            //创建服务器通道配置的辅助工具类
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)

                    .channel(NioServerSocketChannel.class)

                    .handler(new LoggingHandler(LogLevel.INFO))

                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new ChunkedWriteHandler());
                            pipeline.addLast(new HttpObjectAggregator(8192));
                            pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
                            pipeline.addLast(new SimpleChannelInboundHandler<WebSocketFrame>() {
                                /**
                                 * 用于处理文本帧数据
                                 * @param ctx
                                 * @param frame
                                 * @throws Exception
                                 */
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {

                                    if (frame instanceof TextWebSocketFrame) {
                                        // Send the uppercase string back.
                                        String request = ((TextWebSocketFrame) frame).text();

                                        System.out.println("收到消息：" + request);

                                        ctx.channel().writeAndFlush(new TextWebSocketFrame(request.toUpperCase(Locale.CHINA)));
                                        ctx.channel().writeAndFlush("服务器收到。。。。");
                                    } else {
                                        String message = "unsupported frame type: " + frame.getClass().getName();
                                        throw new UnsupportedOperationException(message);
                                    }
                                }

                                @Override
                                public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

                                    System.out.println("handler add -> " + ctx.channel().id().asLongText());
                                }

                                @Override
                                public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
                                    System.out.println("handler rm -> " + ctx.channel().id().asLongText());
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

                                    cause.printStackTrace();
                                    ctx.close();
                                }
                            });
                        }
                    });
            ChannelFuture cfuture = bootstrap.bind(8080).sync();//异步绑定端口
            cfuture.channel().closeFuture().sync();//阻塞程序,等待关闭
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }
}
