package com.harry.netty.demo2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @Description 自定义入站解码器
 * @Author Harry
 * @Date 2020/9/15 11:46
 **/
public class MyByteToLongDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("Decode invoked!");

        System.out.println(in.readableBytes());
        //限定大于等于8个字节才能正常传输,long占8个字节
        if(in.readableBytes() >= 8){
            out.add(in.readLong());
        }
    }
}
