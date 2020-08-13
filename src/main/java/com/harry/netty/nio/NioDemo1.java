package com.harry.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * @Description scattering and gathering
 * @Author Harry
 * @Date 2020/8/13 11:11
 **/
public class NioDemo1 {

    public static void main(String[] args) throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress(8888);
        serverSocketChannel.bind(address);
        int messageLength = 2+3+4;

        ByteBuffer[] byteBuffers = new ByteBuffer[3];
        byteBuffers[0] = ByteBuffer.allocate(2);
        byteBuffers[1] = ByteBuffer.allocate(3);
        byteBuffers[2] = ByteBuffer.allocate(4);

        SocketChannel accept = serverSocketChannel.accept();

        while (true){

            int byteRead = 0;
            while (byteRead < messageLength){
                long r = accept.read(byteBuffers);
                byteRead+=r;
                System.out.println("byteRead:"+byteRead);
                Arrays.asList(byteBuffers).stream().map(byteBuffer->
                        "position:"+byteBuffer.position()+" limit:"+byteBuffer.limit()).forEach(System.out::println);

            }

            for (ByteBuffer byteBuffer:byteBuffers) {
                byteBuffer.flip();
            }

            long bytesWritten = 0;
            while (bytesWritten < messageLength){
                long r = accept.write(byteBuffers);
                bytesWritten += r;
            }
            Arrays.asList(byteBuffers).forEach(byteBuffer -> byteBuffer.clear());
            System.out.println("byteRead:"+byteRead+" bytesWritten:"+bytesWritten);

        }

    }

}
