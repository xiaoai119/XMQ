package processor;

import broker.Broker;

import java.nio.channels.SelectionKey;

/**
 * Created By xfj on 2020/3/14
 */
public interface ResponseProcessor extends Processor{
    default void processResponse(SelectionKey key){};
    default void processResponse(SelectionKey key, Broker broker){};
    default void processResponse(SelectionKey key, int port){};
}
