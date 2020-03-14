package processor;

import common.Message;
import common.MessageType;
import consumer.ConsumerFactory;
import util.SerializeUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConsumerResponeProcessorDefault extends DefaultResponseProcessor {

    private static ExecutorService executorService = Executors.newFixedThreadPool(10000);

    public void processResponse(final SelectionKey key,int port) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
            	SocketChannel writeChannel = null;
                try {
                    writeChannel = (SocketChannel) key.channel();
                    ByteArrayOutputStream attachment = (ByteArrayOutputStream)key.attachment();
                    Message msg = (Message)SerializeUtil.byte2Obj(attachment.toByteArray());
                    ConcurrentLinkedQueue<Message> list = ConsumerFactory.getList(port);
                    list.add(msg);
                    if(msg.getType()==MessageType.REPLY_EXPECTED) {
                        //回复消息
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        msg.setType(MessageType.ACK);
                    	buffer.put(SerializeUtil.obj2Byte(msg));
                        buffer.flip();
                        writeChannel.write(buffer);
                    }
                    
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }finally {
                	try {
						writeChannel.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
                }
            }
        });
    }
    
}