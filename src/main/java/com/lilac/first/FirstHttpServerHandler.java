package com.lilac.first;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * Created by lilac on 2019-06-01.
 */
public class FirstHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        System.out.println(msg.getClass());

        if (msg instanceof HttpRequest) {

            HttpRequest request = (HttpRequest) msg;

            String name = request.method().name();


            System.out.println("request name " + name + "request uri " + request.uri());

            if ("/favicon.ico".equals(request.uri())) {
                System.out.println("favicon.ico");
                return;
            }

            ByteBuf content = Unpooled.copiedBuffer("hello world", CharsetUtil.UTF_8);

            FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK, content);

            resp.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain");

            resp.headers().set(HttpHeaders.Names.CONTENT_LENGTH, content.readableBytes());

            ctx.writeAndFlush(resp);

            ctx.channel().close();
        }
    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel register");
        super.channelRegistered(ctx);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel added");
        super.handlerAdded(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel active");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel inactive");
        super.channelInactive(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel unregister");
        super.channelUnregistered(ctx);
    }
}
