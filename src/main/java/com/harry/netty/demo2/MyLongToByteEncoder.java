package com.harry.netty.demo2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Description 自定义出站编码器
 * @Author Harry
 * @Date 2020/9/15 11:51
 **/
public class MyLongToByteEncoder extends MessageToByteEncoder<Long> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Long msg, ByteBuf out) throws Exception {
        System.out.println("Encode invoked!");

        System.out.println(msg);

        out.writeLong(msg);
    }
}
