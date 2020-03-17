package test;

import common.IpNode;
import common.Message;
import common.Topic;
import producer.DefaultProducer;
import util.SequenceUtil;

/**
 * Created By xfj on 2020/3/14
 */
public class ProducerTest {
    public static void main(String[] args) throws InterruptedException {
        //创建Producer
        SequenceUtil Sequence = new SequenceUtil();

        //同步生产者工厂
        Topic topic = DefaultProducer.requestQueue(new Topic("topic",1), "127.0.0.1", 81);
        topic.addConsumer(new IpNode("127.0.0.1", 8888));
//		        topic.addConsumer(new IpNode("127.0.0.1", 8889));
        int num = Sequence.getSequence();
        Message msg = new Message("message"+num,topic, num);
        String string = DefaultProducer.send(msg, "127.0.0.1", 81);//同步发送
        System.out.println(string);
    }
}
