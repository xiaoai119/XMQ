package common;

import java.io.Serializable;

/**
 * Created By xfj on 2020/2/5
 */
public class PushMessage extends Message implements Serializable{
    private static final long serialVersionUID = 3431254115468157492L;
    private static final int type = MessageType.REGISTER;
    public PushMessage(IpNode node, String message, int num) {
        this.num = num;
        this.node = node;
        this.message = message;
    }
}
