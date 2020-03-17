package test;

import common.IpNode;
import consumer.ConsumerFactory;

import java.io.IOException;

/**
 * Created By xfj on 2020/3/14
 */
public class ConsumerForPullTest {

    public static void main(String[] args) {
        IpNode ipNode3 = new IpNode("127.0.0.1", 81);
        IpNode ipNode4 = new IpNode("127.0.0.1", 8888);
        try {
            ConsumerFactory.createConsumer(ipNode3, ipNode4);
        } catch (IOException e) {
            System.out.println("Broker未上线！");
        }
        while(true) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ConsumerFactory.pull(ipNode3, ipNode4);
        }
    }
}
