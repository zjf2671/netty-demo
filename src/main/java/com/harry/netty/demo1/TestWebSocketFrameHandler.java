package com.harry.netty.demo1;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.time.LocalDateTime;

/**
 * @Description TODO
 * @Author Harry
 * @Date 2020/7/29 10:28
 **/
public class TestWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>{

    public static ChannelGroup channelGroup;
    static {
        channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) throws Exception {
        System.out.println("收到消息："+textWebSocketFrame.text());
//        sendAllMessage(ctx);
        sendMessage(ctx);

    }
    //群发消息
    private void sendAllMessage(ChannelHandlerContext ctx){
        String message = "这是群发消息----服务器时间："+ LocalDateTime.now();
        for (Channel channel:channelGroup) {
            if(channel!=ctx.channel()){
                channel.writeAndFlush(new TextWebSocketFrame(message));
            }
        }
    }

    //给指定的人发消息
    private void sendMessage(ChannelHandlerContext ctx) {
        String message = "你好，"+ ctx.channel().localAddress() + " 给固定的人发消息！";
        ctx.channel().writeAndFlush(new TextWebSocketFrame(message));

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerAdded:" + ctx.channel().id().asLongText());
        channelGroup.add(ctx.channel());
        String message = ctx.channel().id().asLongText()+"加入，在线人数为：" + channelGroup.size();
        channelGroup.writeAndFlush(new TextWebSocketFrame(message));
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerRemoved:"+ctx.channel().id().asLongText());
        String message = ctx.channel().id().asLongText()+"离开，在线人数为：" + channelGroup.size();
        channelGroup.writeAndFlush(new TextWebSocketFrame(message));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exceptionCaught异常发生");
        ctx.close();
    }
}
