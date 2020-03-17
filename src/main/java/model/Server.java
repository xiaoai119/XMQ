package model;

import broker.Broker;
import processor.DefaultRequestProcessor;
import processor.DefaultResponseProcessor;
import processor.RequestProcessor;
import processor.ResponseProcessor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created By xfj on 2020/2/5
 */
public class Server {
    ServerSocketChannel serverSocketChannel = null;
    //写任务队列
    private List<SelectionKey> writeQueen = new ArrayList<SelectionKey>();
    private Selector selector = null;
    private RequestProcessor requestProcessor;
    private ResponseProcessor responseProcessor;
    private Broker broker;

    public Server(int port, DefaultRequestProcessor defaultRequestProcessor, DefaultResponseProcessor responeProcessor) throws IOException {
        this.requestProcessor = defaultRequestProcessor;
        this.responseProcessor = responeProcessor;
        init(port);
    }

    public Server(int port, RequestProcessor requestProcessor, ResponseProcessor responeProcessor, Broker broker) throws IOException {
        this.requestProcessor = requestProcessor;
        this.responseProcessor = responeProcessor;
        this.broker = broker;
        init(port);
    }

    //添加SelectionKey到队列
    public void addWriteQueen(SelectionKey key) {
        synchronized (this) {
            writeQueen.add(key);
            //若select阻塞，则唤醒,以保证后续处理写事件
            selector.wakeup();
        }
    }

    void init(int port) throws IOException {
        // 1.创建ServerSocketChannel
        serverSocketChannel = ServerSocketChannel.open();
        // 2.绑定端口
        serverSocketChannel.bind(new InetSocketAddress(port));
        // 3.设置为非阻塞
        serverSocketChannel.configureBlocking(false);
        // 4.创建通道选择器
        selector = Selector.open();

        /*
         * 5.注册事件类型
         *
         *  sel:通道选择器
         *  ops:事件类型 ==>SelectionKey:包装类，包含事件类型和通道本身。四个常量类型表示四种事件类型
         *  SelectionKey.OP_ACCEPT 获取报文      SelectionKey.OP_CONNECT 连接
         *  SelectionKey.OP_READ 读           SelectionKey.OP_WRITE 写
         */
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        start(port);
    }

    void start(int port) throws IOException {
        while (true) {
            //System.out.println("服务器端：正在监听"+port+"端口");
            // 6.获取可用I/O通道,获得有多少可用的通道
            int num = selector.select();
            if (num > 0) { // 判断是否存在可用的通道
                // 获得所有的keys
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                // 使用iterator遍历所有的keys
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                // 迭代遍历当前I/O通道
                while (iterator.hasNext()) {
                    // 获得当前key
                    SelectionKey key = iterator.next();
                    // 调用iterator的remove()方法，并不是移除当前I/O通道，标识当前I/O通道已经处理。
                    iterator.remove();
                    // 判断事件类型，做对应的处理
                    if (key.isAcceptable()) {
                        ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
                        SocketChannel socketChannel = ssChannel.accept();
//                        System.out.println("处理请求："+ socketChannel.getRemoteAddress());
                        // 获取客户端的数据
                        // 设置非阻塞状态
                        socketChannel.configureBlocking(false);
                        // 注册到selector(通道选择器)
                        socketChannel.register(selector, SelectionKey.OP_READ);

                    } else if (key.isReadable()) {
                        //取消读事件的监控
                        key.cancel();
                        //调用读操作工具类
                        requestProcessor.processorRequest(key, this);
                    } else if (key.isWritable()) {
                        //取消写事件的监控
                        key.cancel();
                        //调用写操作工具类
                        if("processor.BrokerResponeProcessor".equals(responseProcessor.getClass().getName()))
                            responseProcessor.processResponse(key,broker);
                        else if("processor.ConsumerResponeProcessor".equals(responseProcessor.getClass().getName()))
                            responseProcessor.processResponse(key,port);
                        else
                            responseProcessor.processResponse(key);
                    }
                }
            } else {
                //若无可用通道，添加写事件
                synchronized (this) {
                    while (writeQueen.size() > 0) {
                        SelectionKey key = writeQueen.remove(0);
                        //注册写事件
                        SocketChannel channel = (SocketChannel) key.channel();
                        Object attachment = key.attachment();
                        channel.register(selector, SelectionKey.OP_WRITE, attachment);
                    }
                }
            }
        }
    }
}
