package com.lilac.second;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.time.LocalDateTime;

/**
 * Created by lilac on 2019-06-01.
 */
public class SocketClient {

    public static void main(String[] args) throws InterruptedException {

        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventExecutors).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));

                            pipeline.addLast(new LengthFieldPrepender(4));

                            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));

                            pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));


                            pipeline.addLast(new SimpleChannelInboundHandler<String>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

                                    System.out.println(ctx.channel().remoteAddress());

                                    System.out.println(msg + "from server  !");

                                    ctx.writeAndFlush("client-> " + LocalDateTime.now());
                                }

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    System.out.println("client channelActive..");

                                    // 必须有flush
                                    ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
                                }
                            });
                        }
                    });

            ChannelFuture localhost = bootstrap.connect("localhost", 8080).sync();

            localhost.channel().closeFuture().sync();

        } finally {
            eventExecutors.shutdownGracefully();
        }
    }

}
