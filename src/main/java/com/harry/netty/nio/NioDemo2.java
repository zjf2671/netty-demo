package com.harry.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @Description selector channel buffer
 * @Author Harry
 * @Date 2020/8/13 15:32
 **/
public class NioDemo2 {

    public static void main(String[] args) throws IOException {


        List<Integer> ports = Arrays.asList(5000, 5001, 5002, 5003, 5004);
        Selector selector = Selector.open();

        for(int i=0; i<ports.size(); i++){
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            ServerSocket socket = serverSocketChannel.socket();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(ports.get(i));
            socket.bind(inetSocketAddress);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("监听端口号："+ ports.get(i));
        }

        while (true){
            int numbers = selector.select();
            System.out.println("numbers: "+numbers);
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            System.out.println("selectionKeys: "+selectionKeys);
            Iterator<SelectionKey> iter = selectionKeys.iterator();
            while (iter.hasNext()){
                SelectionKey selectionKey = iter.next();
                if(selectionKey.isAcceptable()){
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel)selectionKey.channel();
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector,SelectionKey.OP_READ);
                    iter.remove();
                    System.out.println("获取客户端链接： " + socketChannel);
                }else if(selectionKey.isReadable()){

                    SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
                    int byteRead = 0;
                    while (true){
                        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
                        byteBuffer.clear();
                        int read = socketChannel.read(byteBuffer);
                        System.out.println("read: " + read);
                        if(read <= 0){
                            break;
                        }
                        byteBuffer.flip();
                        socketChannel.write(byteBuffer);
                        byteRead +=read;

                    }
                    System.out.println("读取： " + byteRead + "来自于：" + socketChannel);
                    iter.remove();

                }
            }

        }



    }

}
