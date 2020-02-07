package test;
import common.Message;
import common.MessageType;
import model.Client;

import java.io.IOException;

/**
 * Created By xfj on 2020/2/6
 */
public class TestClient {
    public static final int OP_CONNECT = 1 >> 2;
    public static void main(String[] args) throws IOException, InterruptedException {
        Client client = new Client("127.0.0.1",8080);
        client.init();
        Thread.sleep(2000);
        byte[] receive = client.SyscSendMessage(new Message("test", MessageType.REGISTER, 1));
        System.out.println(new String(receive));
        if (receive.length!=0){
            System.out.println(new String(receive));
        }
//        byte[] receive = client.receive();
//        System.out.println(new String(receive));

    }
}
