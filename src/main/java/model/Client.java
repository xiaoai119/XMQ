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
        socketChannel.connect(new InetSocketAddress(ip,port));
    }

    public void sendMessage(Message msg) throws IOException {
        byte[] bytes = SerializeUtil.Obj2Byte(msg);
        //写数据
        ByteBuffer buffer=ByteBuffer.allocate(1024*1024);
        buffer.clear();
        buffer.put(bytes);
        buffer.flip();
        socketChannel.write(buffer);
        socketChannel.close();
    }

    public byte[] receive(){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {
            while (socketChannel.isOpen() && socketChannel.read(buffer) != -1) {
                buffer.clear();
                int len = socketChannel.read(buffer);
                if (len == -1)
                    break;
                buffer.flip();
                while (buffer.hasRemaining()) {
                    bos.write(buffer.get());
                }
            }
        } catch (IOException e) {
            System.out.println("连接失败");
        }
        return bos.toByteArray();
    }
}
