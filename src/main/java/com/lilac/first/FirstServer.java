package com.lilac.first;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;


/**
 * Created by lilac on 2019-06-01.
 */
public class FirstServer {

    public static void main(String[] args) throws InterruptedException {


        /** 基于NIO创建事件循环组 */
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();      //创建服务器通道配置的辅助工具类
            bootstrap.group(bossGroup,workGroup)                 //配置每个NioEventLoopGroup的用途
                    .channel(NioServerSocketChannel.class)          //指定Nio模式为Server模式
//                    .option(ChannelOption.SO_BACKLOG,1024)    //指定tcp缓冲区
//                    .option(ChannelOption.SO_SNDBUF,10*1024)  //指定发送缓冲区大小
//                    .option(ChannelOption.SO_RCVBUF,10*1024)  //指定接收缓冲区大小
//                    .option(ChannelOption.SO_KEEPALIVE,Boolean.TRUE)//是否保持连接,默认true
                    .childHandler(new FirstInitializer());
//                    .childHandler(new ChannelInitializer<SocketChannel>() {//具体的数据接收方法
//                        @Override
//                        protected void initChannel(SocketChannel ch) throws Exception {     //添加ChannelHandler,handler用来自定义消息处理逻辑
//                            // 管道 包含多个 handler
//                            ChannelPipeline pipeline = ch.pipeline();
//
//                            pipeline.addLast("httpServerCodec", new HttpServerCodec());
//
//                            pipeline.addLast("httpServerResp", new FirstHttpServerHandler());
//                        }
//                    });

            ChannelFuture cfuture = bootstrap.bind(8080).sync();//异步绑定端口
            cfuture.channel().closeFuture().sync();//阻塞程序,等待关闭
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
