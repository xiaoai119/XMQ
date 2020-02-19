package consumer;

import common.IpNode;
import common.Message;
import common.PullMessage;
import common.RegisterMessage;
import model.Client;
import processor.ConsumerResponeProcessor;
import processor.RequestProcessor;
import model.Server;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConsumerFactory {
	//记录每个端口以及对应的消息队列
	private static ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Message>> map = new ConcurrentHashMap<Integer,ConcurrentLinkedQueue<Message>>();

	private static void waiting(int port) throws IOException {
		RequestProcessor requestProcessor = new RequestProcessor();
		ConsumerResponeProcessor consumerResponeProcessor = new ConsumerResponeProcessor();
		new Thread(){
            public void run() {
            	System.out.println("Consumer在本地端口"+port+"监听...");
            	try {
					new Server(port,requestProcessor,consumerResponeProcessor);
				} catch (IOException e) {
					System.out.println("端口已被占用");
					}
                }
		}.start();
	}

    //向Broker注册
	private static void register(IpNode ipNode1,IpNode ipNode2){
        System.out.println("正在注册Consumer...");
        Client client;
        try {
            //客户端注册的ip和端口
            client = new Client(ipNode1.getIp(), ipNode1.getPort());
            RegisterMessage msg = new RegisterMessage(ipNode2, "register", 1);
			byte[] bytes = client.SyscSendMessage(msg);
			System.out.println("test");
			if(client.SyscSendMessage(msg)!=null)
                System.out.println("注册成功...");
            else
                System.out.println("注册失败！");
        } catch (IOException e) {
            System.out.println("连接失败！");
        }
    }

	public static ConcurrentLinkedQueue<Message> getList(int port){
		return map.get(port);
	}

	public static Message getMessage(int port) {
		return ConsumerFactory.getList(port).poll();
	}

	public static void Pull(IpNode ipNode1,IpNode ipNode2) {
		System.out.println("正在拉取消息");
		Client client;
		try {
			client = new Client(ipNode1.getIp(), ipNode1.getPort());
			PullMessage msg = new PullMessage(ipNode2, "pull", 1);
			String ack = new String(client.SyscSendMessage(msg));
			if(ack!=null) {
				Message m = ConsumerFactory.getMessage(ipNode2.getPort());
        		if(m!=null) {
        			System.out.println("消息拉取成功");
        			System.out.println(m.getMessage());
        		}else 
        			System.out.println("消息拉取失败");
    				
			}
		} catch (IOException e) {
			System.out.println("Connection Refuse.");
		}
	}
	public static void createConsumer(IpNode ipNode1,IpNode ipNode2) throws IOException {
		if(map.containsKey(ipNode2.getPort())) {
			System.out.println("端口已被占用!");
			return;
		}
//		ConsumerFactory.register(ipNode1,ipNode2);
		ConsumerFactory.waiting(ipNode2.getPort());
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ConsumerFactory.register(ipNode1,ipNode2);
		map.put(ipNode2.getPort(), new ConcurrentLinkedQueue<Message>());
	}
}
