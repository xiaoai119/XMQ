package processor;

import model.Server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created By xfj on 2020/2/5
 * 处理请求，并根据请求添加response任务到队列
 */
public class RequestProcessor implements Processor{
    //构造线程池
    private static ExecutorService executorService = Executors.newFixedThreadPool(100);

    public void processorRequest(final SelectionKey key, Server server){
        //获得线程并执行
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    SocketChannel readChannel = (SocketChannel) key.channel();
                    // I/O读数据操作
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int len = 0;
                    while (true) {
                        buffer.clear();
                        len = readChannel.read(buffer);
                        if (len == -1) break;
                        buffer.flip();
                        while (buffer.hasRemaining()) {
                            baos.write(buffer.get());
                        }
                    }
                    //System.out.println("服务器端接收到的数据："+ new String(baos.toByteArray()));
                    //将数据添加到key中
                    key.attach(baos);
                    //将注册写操作添加到队列中
                    server.addWriteQueen(key);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
