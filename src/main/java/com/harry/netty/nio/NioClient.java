package com.harry.netty.nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description TODO
 * @Author Harry
 * @Date 2020/8/14 13:38
 **/
public class NioClient {
    public static void main(String[] args) throws IOException {

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1",9999);
        socketChannel.connect(inetSocketAddress);

        while (true){
            selector.select();
            Set<SelectionKey> selectionKeySet = selector.selectedKeys();

            for(SelectionKey selectionKey:selectionKeySet){
                if(selectionKey.isConnectable()){
                    SocketChannel client = (SocketChannel) selectionKey.channel();
                    if(client.isConnectionPending()){
                        client.finishConnect();
                        ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
                        writeBuffer.put((LocalDateTime.now()+"连接成功").getBytes());
                        writeBuffer.flip();
                        client.write(writeBuffer);

                        ExecutorService executorService = Executors.newSingleThreadExecutor(Executors.defaultThreadFactory());
                        executorService.submit(()->{
                            while (true){
                                try{
                                    writeBuffer.clear();
                                    InputStreamReader input = new InputStreamReader(System.in);
                                    BufferedReader br = new BufferedReader(input);
                                    String sendMessage = br.readLine();
                                    writeBuffer.put(sendMessage.getBytes());
                                    writeBuffer.flip();
                                    client.write(writeBuffer);
                                }catch(Exception ex){
                                    ex.printStackTrace();
                                }
                            }
                        });
                    }
                    client.register(selector,SelectionKey.OP_READ);
                }else if(selectionKey.isReadable()){
                    SocketChannel client = (SocketChannel) selectionKey.channel();
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    int count = client.read(readBuffer);
                    if(count > 0){
                        String receviedMeassage = new String(readBuffer.array(), 0, count);
                        System.out.println("receviedMeassage: "+ receviedMeassage);
                    }
                }
            }
            selectionKeySet.clear();
        }

    }
}
