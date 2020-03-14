package test;

import model.Server;
import processor.DefaultRequestProcessor;
import processor.DefaultResponseProcessor;

import java.io.IOException;

/**
 * Created By xfj on 2020/2/6
 */
public class TestServer {
    public static void main(String[] args) throws IOException {
        Server server = new Server(8080, new DefaultRequestProcessor(), new DefaultResponseProcessor());

    }
}
