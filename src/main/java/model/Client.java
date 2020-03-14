package model;

import common.Message;
import util.SerializeUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created By xfj on 2020/2/5
 */
public class Client {
    //client主要实现基本的链接，send，接收数据
    String ip;
    private int port;
    SocketChannel socketChannel;
    public Client(String ip,int port){
        this.ip = ip;
        this.port = port;
    }

    //初始化，创建链接
    public void init() throws IOException {
        socketChannel = SocketChannel.open();
//        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(ip,port));

    }

    public void sendMessage(Message msg) throws IOException {
        byte[] bytes = SerializeUtil.obj2Byte(msg);
        //写数据
        ByteBuffer buffer=ByteBuffer.allocate(1024*1024);
        buffer.clear();
        buffer.put(bytes);
        buffer.flip();

//        while (!socketChannel.finishConnect()) {
//            // 没连接上,则一直等待
//            Thread.yield();
//            System.out.println("connect refuse");
//        }
        socketChannel.write(buffer);
        //关闭输出，不关闭channel,关闭后服务端read返回-1
        socketChannel.shutdownOutput();
    }

    public byte[] SyscSendMessage(Message msg) throws IOException{
        init();
        sendMessage(msg);
        return receive();
    }

    public byte[] receive(){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {
            while (socketChannel.read(buffer) != -1) {
                //判断是否传输完成
                if (buffer.position() > 0) break;
            }

            buffer.flip();
            while (buffer.hasRemaining()) {
                bos.write(buffer.get());
            }
            buffer.clear();

        } catch (IOException e) {
            System.out.println("连接失败");
        }
        return bos.toByteArray();
    }
}
