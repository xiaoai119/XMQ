package common;

import java.io.Serializable;

/**
 * Created By xfj on 2020/2/5
 */
public class PullMessage extends Message implements Serializable{
    private static final long serialVersionUID = -3037522746992937257L;
    private static final int type = MessageType.REGISTER;
    public PullMessage(ConsumerNode ipNode,String message,int num) {
        this.num = num;
        this.node = ipNode;
        this.message = message;
    }
}