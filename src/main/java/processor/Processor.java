package processor;

/**
 * Created By xfj on 2020/2/6
 */
public interface Processor {
    default void processRequest(){};
    default void processResponse(){};

}
