package processor;

import common.Message;
import util.SerializeUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created By xfj on 2020/2/5
 *
 */
public class DefaultResponseProcessor implements ResponseProcessor{
    private static ExecutorService executorService = Executors.newFixedThreadPool(100);
    public void processResponse(final SelectionKey key) {
        //从线程池拿到线程并执行
        executorService.submit(() -> {
            SocketChannel writeChannel = null;
            try {
                // 写操作
                writeChannel = (SocketChannel) key.channel();
                //拿到客户端传递的数据
                ByteArrayOutputStream attachment = (ByteArrayOutputStream)key.attachment();
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                Message m = (Message)SerializeUtil.byte2Obj(attachment.toByteArray());
                System.out.println("服务器收到："+new String(attachment.toByteArray()));
                String message = new String(attachment.toByteArray())+"ACK";
                buffer.put(message.getBytes());
                buffer.flip();
                writeChannel.write(buffer);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    writeChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
