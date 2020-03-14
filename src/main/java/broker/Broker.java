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
    private int push_Time = 1000;//push时间默认一秒一次
    private boolean hasSlave = false;//是否有备份节点
    private boolean hasQueueNum = false;//是否指定队列数
    private boolean startPersistence = false;//是否开启持久化
    private int sync_Time = 1000;//sync时间默认一秒一次
    private int reTry_Time = 16;//发送失败重试次数
    private int store_Time = 1000;//刷盘时间间隔
    private ConcurrentHashMap<String,MyQueue> queueList;//队列列表
    private Filter filter;//过滤器
    List<IpNode> index;//消费者地址
    Map<IpNode, Client> clients;
    List<IpNode> slave;

    public Broker(int port) throws IOException {
        init(port);
    }
    public Broker(int port,int queueNum) throws IOException {
        hasQueueNum = true;
        init(port);
        createQueue(queueNum);
    }
    public Broker(int port,List<IpNode> slave) throws IOException {
        this.slave = slave;
        hasSlave = true;
        init(port);
    }
    public Broker(int port,int queueNum,List<IpNode> slave) throws IOException {
        this.slave = slave;
        hasSlave = true;
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
                for(i=0;i<reTry_Time;i++) {//失败重试reTry_Time次
                    String ack=null;
                    try {
                        ack = new String(client.SyscSendMessage(m));
                    } catch (IOException e) {
                        System.out.println("发送失败！正在重试第"+(i+1)+"次...");
                    }
                    if(ack!=null)
                        break;
                }
                if(i>=reTry_Time) {
                    //todo 进入死信队列
                }
            }

        }
    }

}
