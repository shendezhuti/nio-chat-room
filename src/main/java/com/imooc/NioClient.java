package com.imooc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * Nio客户端
 * @author ZEXIN HUANG
 * @version 1.0
 * @date 2020-05-02 16:27
 */
public class NioClient {


    /**
     * 启动
     */
    public void start(String nickname) throws IOException {

        /**
         * 连接服务器端
         */
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",8000));


        /**
         * 接受服务器响应
         */
        //新开线程，专门负责来接受服务器端的相应数据

        Selector selector = Selector.open();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        new Thread(new NioClientHandler(selector)).start();

        /**
         * 向服务器端发送数据
         */
        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNextLine()){
            String request = scanner.nextLine();
            if(request!= null && request.length()>0){
                socketChannel.write(Charset.forName("UTF-8").encode(nickname+":"+request));
            }
        }


    }

    public static void main(String[] args) throws IOException {
      //  new NioClient().start();

    }
}
