package com.harry.netty.demo1;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.time.LocalDateTime;

/**
 * @Description TODO
 * @Author Harry
 * @Date 2020/7/29 10:28
 **/
public class TestWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>{
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        System.out.println("收到消息："+textWebSocketFrame.text());
        channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame("服务器时间："+ LocalDateTime.now()));

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerAdded:" + ctx.channel().id().asLongText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerRemoved:"+ctx.channel().id().asLongText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exceptionCaught异常发生");
        ctx.close();
    }
}
