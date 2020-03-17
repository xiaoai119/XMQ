package test;

import broker.Broker;

import java.io.IOException;

/**
 * Created By xfj on 2020/3/14
 */
public class BrokerTest {
    public static void main(String[] args) {
        try {
            Broker broker = new Broker(81);
            broker.setRetryTime(1000);
            broker.setPushTime(1000);
            broker.push();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}