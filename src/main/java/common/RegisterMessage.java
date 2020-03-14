package common;

import java.io.Serializable;

/**
 * Created By xfj on 2020/2/5
 */
public class RegisterMessage extends Message implements Serializable{
    private static final long serialVersionUID = 3431254115468157492L;
    private static final int type = MessageType.REGISTER;
    public RegisterMessage(IpNode node, String message, int num) {
        this.num = num;
        this.node = node;
        this.message = message;
    }
    public IpNode getIpNode() {
        return node;
    }
    public int getNum() {
        return num;
    }
    public int getType() {
        return type;
    }
    public String getMessage() {
        return message;
    }

}
