package test;

import common.IpNode;
import consumer.ConsumerFactory;

import java.io.IOException;

/**
 * Created By xfj on 2020/2/19
 */
public class ConsumerFactoryTest {
    public static void main(String[] args) throws IOException, InterruptedException {
//		IpNode ipNode1 = new IpNode("127.0.0.1", 8888);
        IpNode ipNode1 = new IpNode("127.0.0.1", 8888);
        IpNode ipNode2 = new IpNode("127.0.0.1", 8888);
        ConsumerFactory.createConsumer(ipNode1, ipNode2);
        new Thread(){
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    ConsumerFactory.Pull(ipNode1,ipNode2);
                }
            };
        }.start();
    }
}
