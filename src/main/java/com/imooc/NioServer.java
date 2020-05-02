package com.imooc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
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

                // do somethings

                /**
                 * 如果是可读事件
                 */
                // do somethings
            }
        }


    }

    public static void main(String[] args) {
        NioServer nioServer = new NioServer();

    }
}
