package processor;

import model.Server;

import java.nio.channels.SelectionKey;

/**
 * Created By xfj on 2020/3/14
 */
public interface RequestProcessor extends Processor {
    default void processorRequest(SelectionKey key, Server server){};
}
