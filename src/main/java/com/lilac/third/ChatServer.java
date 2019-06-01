package com.lilac.third;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;

import static io.netty.util.CharsetUtil.UTF_8;


/**
 * Created by lilac on 2019-06-01.
 */
public class ChatServer {

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

                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new DelimiterBasedFrameDecoder(4096, Delimiters.lineDelimiter()));

                            pipeline.addLast(new StringDecoder(UTF_8));

                            pipeline.addLast(new StringEncoder(UTF_8));

                            pipeline.addLast(new SimpleChannelInboundHandler<String>() {

                                @Override
                                public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

                                    // 新建立的连接
                                    Channel channel = ctx.channel();

                                    channelGroup.writeAndFlush("【服务器】--- " + channel.remoteAddress() + "  加入啦\n");

                                    channelGroup.add(channel);

                                }

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                    Channel channel = ctx.channel();

                                    channel.writeAndFlush("【我自己发消息】" + msg + "\n");

                                    channelGroup.stream().filter(ch -> ch != channel)
                                            .forEach(channel1 -> channel1.writeAndFlush(channel.remoteAddress() + " 发送消息：" + msg + "\n"));
                                }

                                @Override
                                public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

                                    Channel channel = ctx.channel();

                                    channelGroup.writeAndFlush("【服务器】--- " + channel.remoteAddress() + "  离开啦\n");

                                    channelGroup.remove(channel); // 这行代码非必须
                                }

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    Channel channel = ctx.channel();

                                    System.out.println(channel.remoteAddress() + " 上线啦");

                                }

                                @Override
                                public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                    Channel channel = ctx.channel();
                                    System.out.println(channel.remoteAddress() + " 下线啦");
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
