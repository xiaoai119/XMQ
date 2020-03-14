package processor;

import java.nio.channels.SelectionKey;

/**
 * Created By xfj on 2020/3/14
 */
public interface ResponseProcessor extends Processor{
    default void processResponse(){};
    default void processResponse(SelectionKey key){};
}
