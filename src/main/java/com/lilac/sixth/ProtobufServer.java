package com.lilac.sixth;

import com.lilac.object.Galaxy;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Created by lilac on 2019-06-02.
 */
public class ProtobufServer {

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

                            ChannelPipeline p = ch.pipeline();


                            p.addLast(new ProtobufVarint32FrameDecoder());

                            p.addLast(new ProtobufDecoder(Galaxy.Student.getDefaultInstance()));

                            p.addLast(new ProtobufVarint32LengthFieldPrepender());

                            p.addLast(new ProtobufEncoder());

                            p.addLast(new SimpleChannelInboundHandler<Galaxy.Student>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, Galaxy.Student msg) throws Exception {

                                    Channel channel = ctx.channel();

                                    System.out.println(msg.getName() + " " + msg.getAge() + " " + msg.getAddress());

                                    channel.writeAndFlush(Galaxy.Student.newBuilder().setAge(11).setAddress("上海").setName("卢江"));
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    cause.printStackTrace();
                                    ctx.close();
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
