package com.lilac.sixth;

import com.lilac.object.Galaxy;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import java.time.LocalDateTime;

/**
 * Created by lilac on 2019-06-01.
 */
public class ProrobufClient {

    public static void main(String[] args) throws InterruptedException {

        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventExecutors).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
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

                                    System.out.println(ctx.channel().remoteAddress());

                                    System.out.println(msg.getName() + " " + msg.getAge() + " " + msg.getAddress() + "from server  !");

                                    ctx.writeAndFlush(msg);

                                }

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    System.out.println("client channelActive..");

                                    Galaxy.Student student = Galaxy.Student.newBuilder()
                                            .setName("丁香")
                                            .setAddress("北京")
                                            .setAge(23).build();

                                    // 必须有flush
                                    ctx.writeAndFlush(student);
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
