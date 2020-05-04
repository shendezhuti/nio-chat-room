# NIO简介

姓名：Non-blocking I/O 或者New I/O，前者是从NIO特性——非阻塞IO，后者是从年龄角度出发。

出生: JDK1.4

职务：高并发网络服务器支持岗



# BIO网络模型

首先回顾BIO网络编程，BIO是基于阻塞IO实现的，比如现在我们的程序要从网络上下载一段数据下来，程序需要read方法来发起一个读的操作来读取我们的数据，阻塞IO会让我们的程序线程卡在读的方法这里，程序既不会报错也不会返回，直到网络的数据被下载完毕，程序才会向下执行。我们假设当前网络状况条件不好，整个读的过程需要5秒钟，这个程序就会卡5秒。

接下来看一下BIO网络模型用图怎么表示

![image-20200503172057857](https://github.com/shendezhuti/nio-chat-room/blob/master/image/image-20200503172057857.png)

1.服务器端启动，监听客户端的连接请求

2.客户端启动，发起建立连接的请求

3.服务器端接收到客户端的连接请求后，会启动新线程

4.新创建的这个线程会和客户端创建socket连接，响应客户端，告诉客户端你可以发信息了

5.服务端等待客户端再次发来请求数据，在服务端线程等待发来数据的时候，线程是卡在读的方法上的

我们假设客户端和服务端建立好连接后，一直都没有发送请求信息，这样服务端的线程是不是就在一直等待？

如果上千万的客户端与服务器端建立了连接后，都没有发数据，这样服务器端会变成什么样？

![image-20200503174307563](https://github.com/shendezhuti/nio-chat-room/blob/master/image/image-20200503174307563.png)

我们从上图也可以看出，如果在高并发的情况下，客户端线程过多，就会对服务端的性能造成非常大的影响

总结一下BIO网络模型的缺点

- 阻塞式I/O导致服务端线程阻塞等待客户端发送信息

- 弹性伸缩能力差 1:1的客户端与服务器端线程

- 多线程耗资源



# NIO网络模型猜想

如果让读者基于非阻塞I/O，设计应对高并发场景编程模型，你会怎么设计？

我们不如YY一下。

首先有一个Acceptor去建立连接请求，当客户端1有连接请求后，创建socket，放到后台的set集合，然后遍历set集合，调用read方法。当read方法没有结果返回或者返回空，说明没有数据到达，不用处理。当read方法返回了数据，说明收到了客户端的信息，我们调用handler方法来处理业务逻辑。客户端2连接后，也是这样的步骤。这样后端的一个handler处理所有的业务请求。

![image-20200503183108323](https://github.com/shendezhuti/nio-chat-room/blob/master/image/image-20200503183108323.png)



# NIO网络模型

Selector组件: 循环检测注册事件就绪状态 （模型的核心），selector就是管理与客户端建立的多个连接，监听注册到它上面的事件，比如有新连接接入或者是某个连接上有可读、可写消息。一旦事件被selector监听到，它会调用相应的事件处理器。

整个请求过程

1.selector注册建立连接事件

2.客户端启动，发送建立连接请求

3.selctor检测到后，启动建立连接事件处理器

4.Acceptor Handler创建与客户端连接

5.Acceptor Handler响应客户端建立连接请求。

6.Acceptor Handler把创建的socket连接注册到selector上，并且注册这个事件的可读事件。然后这个Acceptor Handler方法就完成了使命

7.客户端发送请求到selector上

8.selector启动连接读写处理器

9.read&write handler处理与客户端读写业务

10.read&write handler响应客户端请求

11.read&write handler将socket可读事件注册到selector上

![image-20200503190754600](https://github.com/shendezhuti/nio-chat-room/blob/master/image/image-20200503190754600.png)



# NIO模型的改进

- 非阻塞式I/O模型
- 弹性伸缩能力强
- 单线程节省资源 （线程的上下文切换，线程的创建与销毁带来的负载都解决了）





# NIO网络编程详解

## NIO 核心

- **Channel**: 通道
- **Buffer**：缓冲区
- **Selector**:选择器或多路复用器



### channel简介

信息传输的通道，是jdk nio中对输入输出的抽象，类比BIO中流的概念。

- 双向性：一个channel既可读又可写

- 非阻塞性：
- 操作唯一性：只能由buffer（字节数组）操作

### channel实现

- 文件类：FileChannel
- UDP类：DatagramChannel
- TCP类：ServerSocketChannel/SocketChannel

### channel使用

![image-20200503193855872](https://github.com/shendezhuti/nio-chat-room/blob/master/image/image-20200503193855872.png)

### Buffer简介

- 作用：读写channel中的数据
- 本质：一块内存区域

### Buffer属性

- Capacity：容量
- position：位置
- Limit：上限
- Mark：标记

### Buffer的使用

![image-20200503205408272](https://github.com/shendezhuti/nio-chat-room/blob/master/image/image-20200503205408272.png)

![image-20200503205422395](https://github.com/shendezhuti/nio-chat-room/blob/master/image/image-20200503205422395.png)

![image-20200503205506242](https://github.com/shendezhuti/nio-chat-room/blob/master/image/image-20200503205506242.png)

![image-20200503205525271](https://github.com/shendezhuti/nio-chat-room/blob/master/image/image-20200503205525271.png)

![image-20200503205534576](https://github.com/shendezhuti/nio-chat-room/blob/master/image/image-20200503205534576.png)

![image-20200503205654982](https://github.com/shendezhuti/nio-chat-room/blob/master/image/image-20200503205654982.png)

![image-20200503205710603](https://github.com/shendezhuti/nio-chat-room/blob/master/image/image-20200503205710603.png)



### selector简介

- 作用：I/O就绪选择
- 地位：NIO网络编程基础

### selector使用

![image-20200503210450736](https://github.com/shendezhuti/nio-chat-room/blob/master/image/image-20200503210450736.png)



### Selectionkey简介

- 四种就绪状态常量
- 有价值的属性



### NIO编程实现步骤

- 第一步：创建selector
- 第二步：创建ServerSocketChannel，并且绑定监听端口
- 第三步：将Channel设置为非阻塞模式
- 第四步：将Channel注册到Selector上，监听连接事件
- 第五步：循环调用Selector的select方法，检测就绪情况
- 第六步：调用selectedKeys方法获取就绪channel集合
- 第七步：判断就绪事件种类，调用业务处理方法
- 第八步：根据业务需要决定是否再次注册监听事件，重复执行第三步操作



### 原生NIO缺陷分析

- 麻烦：NIO类库和API复杂，学习曲线比较抖
- 心累：可靠性能力补齐，工作量和难度都非常大，比如客户端的断连重连，网络闪断，失败缓存，网络堵塞，异常码流
- 有坑：epoll函数的bug，selector空轮询，导致CPU100%
