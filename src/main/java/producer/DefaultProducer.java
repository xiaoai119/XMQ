package producer;

import common.IpNode;
import common.Message;
import common.MessageType;
import common.Topic;
import model.Client;
import util.SequenceUtil;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created By xfj on 2020/3/14
 */
public class DefaultProducer implements Producer{
    //该生产者是否已申请队列
    private static ConcurrentHashMap<IpNode, Boolean> requestMap= new ConcurrentHashMap<IpNode, Boolean>();
    //重试次数
    static int retryTime = 16;
    private static int delayTime = 2000;//延时时间默认2s
    public void setRetryTime(int retryTime) {
        this.retryTime = retryTime;
    }
    public void setRetryTime(int retryTime,int delayTime) {
        DefaultProducer.retryTime = retryTime;
        DefaultProducer.delayTime = delayTime;
    }
    //返回值为消息号+ACK
    //发送失败返回值为null
    public static String send(Message msg, String ip, int port) {//未申请队列返回null
        IpNode ipNode = new IpNode(ip, port);
        if(requestMap.get(ipNode)==null) {
            System.out.println("未向Broker申请队列！");
            return null;
        }
        Client client;
        try {
            Thread.sleep(delayTime);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
            return null;
        }
        //失败重复，reTry_Time次放弃
        for(int i = 0; i< retryTime; i++) {
            try {
                client = new Client(ip, port);
                String result = new String(client.SyscSendMessage(msg));
                if(result!=null)
                    return result;
                if("".equals(result))
                    return null;
            } catch (IOException e) {
                System.out.println("生产者消息发送失败，正在重试第"+(i+1)+"次...");
            }
        }
        return null;
    }
    private static String sendQueueRegister(Message msg, String ip, int port) {//未申请队列成功返回null
        Client client;
        try {
            client = new Client(ip, port);
            //失败重复，reTry_Time次放弃
            for(int i = 0; i< retryTime; i++) {
                String result = new String(client.SyscSendMessage(msg));
                if(result!=null) {
                    System.out.println("队列申请成功！");
                    return result;
                }
                if("".equals(result))
                    return null;
            }
        } catch (IOException e) {
            System.out.println("Broker未上线！");
        }
        return null;
    }

    public static Topic requestQueue(Topic topic, String ip, int port){//输入为一个topic，里面包含请求的队列个数
        System.out.println("请求向Broker申请队列...");
        Topic t = topic;
        Message m = new Message("requestQueue",MessageType.REQUEST_QUEUE,t, -1);
        String queue = DefaultProducer.sendQueueRegister(m, ip, port);
        if(queue==null) {
            System.out.println("申请队列失败！");
            return t;//返回原topic
        }
        String[] l = queue.substring(7).split(" ");
        for(String i:l)
            topic.addQueueId(Integer.parseInt(i));
        IpNode ipNode = new IpNode(ip, port);
        requestMap.put(ipNode, true);
        return t;
    }
    public static void main(String[] args) {
//    	Topic topic = SyscProducerFactory.requestQueue(new Topic("hh",10), "127.0.0.1", 81);
//		for(Integer i:topic.getQueue())
//			System.out.println(i+" ");
        SequenceUtil Sequence = new SequenceUtil();
        new Thread(){
            public void run() {
                Topic topic = DefaultProducer.requestQueue(new Topic("hh",1), "127.0.0.1", 81);
                topic.addConsumer(new IpNode("127.0.0.1", 8888));

//            	while(true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                for(int i=0;i<10;i++) {
                    int num = Sequence.getSequence();
                    Message msg = new Message("hh"+num,topic, num);
                    System.out.println(DefaultProducer.send(msg, "127.0.0.1", 81));
                }
//            	}

            }
        }.start();
    }
}