package com.harry.netty.nio;



import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @Description TODO
 * @Author Harry
 * @Date 2020/8/13 17:38
 **/
public class NioServer {

    private static Map<String, SocketChannel> clientMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        ServerSocket serverSocket =   serverSocketChannel.socket();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(9999);
        serverSocket.bind(inetSocketAddress);

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true){
            try {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                selectionKeys.forEach(selectionKey -> {
                    final SocketChannel client;
                    try {
                        if(selectionKey.isAcceptable()) {
                            ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
                            client = server.accept();
                            client.configureBlocking(false);
                            client.register(selector,SelectionKey.OP_READ);
                            String key = "【"+ UUID.randomUUID().toString()+"】";
                            clientMap.put(key, client);
                        }else if(selectionKey.isReadable()){
                            client = (SocketChannel)selectionKey.channel();
                            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                            int count = client.read(readBuffer);
                            if(count > 0){
                                readBuffer.flip();
                                Charset character = Charset.forName("utf-8");
                                String recevidMessage = String.valueOf(character.decode(readBuffer).array());
                                System.out.println(client + ": "+recevidMessage);
                                String sendKey = null;
                                for(Map.Entry<String, SocketChannel> entry:clientMap.entrySet()){
                                    if(client == entry.getValue()){
                                        sendKey = entry.getKey();
                                    }
                                }
                                for (Map.Entry<String, SocketChannel> entry:clientMap.entrySet()){
                                    SocketChannel sc = entry.getValue();
                                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                                    byteBuffer.put((sendKey + ":" + recevidMessage).getBytes());
                                    byteBuffer.flip();
                                    sc.write(byteBuffer);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                selectionKeys.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
