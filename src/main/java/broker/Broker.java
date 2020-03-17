package broker;


import common.IpNode;
import common.Message;
import model.Client;
import model.Server;
import processor.BrokerResponeProcessor;
import processor.DefaultRequestProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created By xfj on 2020/3/14
 */
public class Broker {
    private volatile int count = 0;//记录队列编号
    private int pushTime = 1000;//push时间默认一秒一次
    private boolean hasQueueNum = false;//是否指定队列数
    private int retryTime = 16;//发送失败重试次数
    private ConcurrentHashMap<String,MyQueue> queueList;//队列列表
    private Filter filter;//过滤器
    List<IpNode> index;//消费者地址
    Map<IpNode, Client> clients;

    public Broker(int port) throws IOException {
        init(port);
    }
    public Broker(int port,int queueNum) throws IOException {
        hasQueueNum = true;
        init(port);
        createQueue(queueNum);
    }

    //broker init在本地创建server，监听
    private void init(int port) throws IOException {
        System.out.println("Broker已启动，在本地"+port+"端口监听。");
        //初始化索引
        index = new ArrayList<IpNode>();
        //创建客户端集合
        clients = new HashMap<IpNode,Client>();
        //创建队列库
        queueList = new ConcurrentHashMap<String,MyQueue>();
        //默认创建十个队列
        if(!hasQueueNum)
            createQueue(10);
        //监听生产者
        DefaultRequestProcessor defaultDefaultRequestProcessor = new DefaultRequestProcessor();
        BrokerResponeProcessor brokerResponeProcessor = new BrokerResponeProcessor();
        Broker broker = this;
        new Thread(){
            public void run() {
                try {
                    new Server(port, defaultDefaultRequestProcessor,brokerResponeProcessor,broker);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            };
        }.start();
        //slave同步

    }

    private synchronized void createQueue(int queueNum) {
        int k=0;
        for(int i=1;i<=queueNum;i++) {
            MyQueue queue = new MyQueue();
            queueList.put((count++)+"", queue);
        }
    }

    //将消息添加到某个队列中
    public synchronized void add(int queueNumber, Message value) {
        MyQueue queue = queueList.get(queueNumber+"");
        queue.putAtHeader(value);
    }

    //为Topic选择队列
    public List<Integer> choiceQueue(int queueNum) {
        if(queueNum>queueList.size())
            createQueue(queueNum-queueList.size());
        return LoadBalance.balance(queueList, queueNum);
    }

    public void addConsumer(IpNode ipNode) {
        index.add(ipNode);
        Client client;
        client = new Client(ipNode.getIp(),ipNode.getPort());
        clients.put(ipNode,client);
    }

    //pull模式
    public void pullMessage(IpNode ipNode) {
        List<Message> list = new ArrayList<Message>();
        //查找队列最外层消息，找到对应ipNode的消息
        for(MyQueue queue:queueList.values()) {
            if(queue.getTail()!=null) {
                List<IpNode> l = queue.getTail().getTopic().getConsumer();
                if(l.contains(ipNode)&&l.size()==1)//消息消费者只有一个，该消息出队
                    list.add(queue.getAndRemoveTail());
                else if(l.contains(ipNode)&&l.size()>1) {//消息消费者不止一个，则删除这个消费者，并将该消息推送给它
                    Message m = queue.getTail();
                    m.getTopic().deleteConsumer(ipNode);
                    list.add(m);
                }
            }
        }
        for(Message m:list) {
            Client client = new Client(ipNode.getIp(), ipNode.getPort());
            if(client!=null) {
                int i=0;
                for(i=0; i< retryTime; i++) {//失败重试reTry_Time次
                    String ack=null;
                    try {
                        ack = new String(client.SyscSendMessage(m));
                    } catch (IOException e) {
                        System.out.println("发送失败！正在重试第"+(i+1)+"次...");
                    }
                    if(ack!=null)
                        break;
                }
                if(i>= retryTime) {
                    //todo 进入死信队列
                }
            }
        }
    }

    //push模式
    public void push() {
        new Thread(){
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(pushTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pushMessage();
                }

            };
        }.start();
    }

    //为消费者推送消息
    private void pushMessage() {
        HashMap<IpNode, List<Message>> map = filter(index,poll(1));
        for(IpNode ip:map.keySet())
        {
            List<Message> message = map.get(ip);
            for(Message m:message) {
                Client client = clients.get(ip);
                if(client!=null) {
                    int i=0;
                    for(i=0; i< retryTime; i++) {//失败重试16次
                        String ack=null;
                        try {
                            ack = new String(client.SyscSendMessage(m));
                        } catch (IOException e) {
                            System.out.println("发送失败！正在重试...");
                        }
                        if(ack!=null)
                            break;
                    }
                    if(i>= retryTime) {
                        //todo 进入死信队列
                    }
                }else {
                    System.out.println("消费者不存在");
                    //todo 进入死信队列
                }
            }
        }
    }

    //所有队列均出队num个元素
    private synchronized List<Message> poll(int num) {
        ArrayList<Message> list = new ArrayList<Message>();
        for(int i=0;i<num;i++) {
            for(MyQueue queue:queueList.values()) {
                if(queue.getTail()!=null) {
                    List<IpNode> l = queue.getTail().getTopic().getConsumer();
                    for(IpNode j:l) {
                        if(!index.contains(j))
                            return list;
                    }
                }
                Message message = queue.getAndRemoveTail();
                if(message!=null)
                    list.add(message);
            }
        }
        return list;
    }

    private HashMap<IpNode, List<Message>> filter(List<IpNode> index,List<Message> list){
        filter = new Filter(index);
        return filter.filter(list);
    }

    public int getPushTime() {
        return pushTime;
    }

    public void setPushTime(int pushTime) {
        this.pushTime = pushTime;
    }

    public int getRetryTime() {
        return retryTime;
    }

    public void setRetryTime(int retryTime) {
        this.retryTime = retryTime;
    }

    public List<IpNode> getIndex() {
        return index;
    }

    public void setIndex(List<IpNode> index) {
        this.index = index;
    }
}
