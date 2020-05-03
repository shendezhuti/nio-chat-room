package com.imooc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO服务器端
 * @author ZEXIN HUANG
 * @version 1.0
 * @date 2020-05-02 16:27
 */
public class NioServer {

    /**
     * 启动服务器端
     */
    public void start() throws IOException {
        /**
         * 1.创建selector
         */
        Selector selector = Selector.open();
        /**
         * 2.通过ServerSocketChannel创建channel通道
         */
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        /**
         * 3.为channel通道绑定监听端口
         */
        serverSocketChannel.bind(new InetSocketAddress((8000)));
        /**
         * 4.设置channel为非阻塞模式
         */
        serverSocketChannel.configureBlocking(false);

        /**
         * 5.将channel注册到selector上，监听连接事件
         */
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务器启动成功");
        /**
         * 6.循环等待新接入的连接
         */
        for(;;){ //while(true)
            /**
             * TODO 获取可用channel数量
             */
            int readyChannels = selector.select();

            if(readyChannels == 0) continue;
            /**
             * TODO 为什么要这样！？
             */

            /**
             * 获取可用channel集合
             */
            Set<SelectionKey> selectionKeySet =  selector.selectedKeys();

            Iterator iterator = selectionKeySet.iterator();

            while(iterator.hasNext()){
                /**
                 * selectionKey的实例
                 */
                SelectionKey selectionKey = (SelectionKey) iterator.next ();

                /**
                 * 移除set中的当前selectionKey
                 */
                iterator.remove();

                /**
                 * 7.根据就绪状态，调用对应方法，处理对应的业务逻辑
                 */

                /**
                 * 如果是 接入事件
                 */
                if(selectionKey.isAcceptable()){
                    acceptHandler(serverSocketChannel,selector);
                }
                // do somethings

                /**
                 * 如果是可读事件
                 */
                if(selectionKey.isReadable()){
                    readHandler(selectionKey,selector);
                }
                // do somethings
            }
        }


    }

    /**
     * 接入事件处理器
     */
    private void acceptHandler(ServerSocketChannel serverSocketChannel,Selector selector) throws IOException {
        /**
         * 如果是接入事件，创建socketchannel
         */
        SocketChannel socketChannel = serverSocketChannel.accept();

        /**
         * 将socketChannel 设置为非阻塞工作模式
         */
        socketChannel.configureBlocking(false);
        /**
         * 将channel注册到selector上，监听可读事件
         */
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);


        /**
         * 回复客户端提示信息
         */
        socketChannel.write(Charset.forName("UTF-8").encode("你与聊天室其他人都是不是朋友关系，请注意隐私安全"));

    }

    /**
     * 可读事件处理
     */
    private void readHandler(SelectionKey selectionKey,Selector selector) throws IOException {
        /**
         * 要从 selectionKey中获取到已经就绪的channel
         */

        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

        /**
         * 创建buffer
         */

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);


        /**
         * 循环读取客户端请求信息
         */
        String request = "";
        while(socketChannel.read(byteBuffer)>0){
            /**
             * 切换buffer为读模式
             */
            byteBuffer.flip();

            /**
             * 读取buffer中的内容
             */
            request += Charset.forName("UTF-8").decode(byteBuffer);
        }

        /**
         * 将channel再次注册到selector上，监听他的可读信息
         */
        socketChannel.register(selector,SelectionKey.OP_READ);

        /**
         * 将客户端发送的请求信息广播给其他客户端
         */
        if(request.length()>0){
            //广播给其他客户端
            System.out.println("::"+request);
        }

    }


    public static void main(String[] args) {
        NioServer nioServer = new NioServer();

    }
}
