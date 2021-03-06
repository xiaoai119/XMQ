package common;

import java.io.Serializable;

/**
 * Created By xfj on 2020/2/5
 */
public class Message implements Serializable {
    static final long serialVersionUID = 567109373083056917L;
    int num;//消息序号
    String message;//消息
    int type;//消息类型
    Topic topic;//消息主题


    IpNode node;

    private int MAX_LENGTH=99999;

    //构造函数
    public Message() {}
    public Message(String s,Topic topic,int num) {
        // 默认消息类型为1
        this.setType(MessageType.REPLY_EXPECTED);
        this.setNum(num);
        if(s.length()>MAX_LENGTH) {
            this.message = s.substring(0, MAX_LENGTH);
        }
        else{
            this.message = s;
        }
        this.topic = topic;
    }

    public Message(String s,int type,int num) {
        this.setType(type);
        this.setNum(num);
        if(s.length()>MAX_LENGTH) {
            this.message = s.substring(0, MAX_LENGTH);
        }
        else{
            this.message = s;
        }
    }
    public Message(String s,int type,Topic topic,int num) {
        this.setType(type);
        this.topic = topic;
        this.setNum(num);
        if(s.length()>MAX_LENGTH) {
            this.message = s.substring(0, MAX_LENGTH);
        }
        else{
            this.message = s;
        }

    }
    public String getMessage() {
        return message;
    }

	public String getJointMessage() {
		System.out.println(message.length());
		String str_len = String.format("%d",message.length());
		String str_type = String.valueOf(this.type);
		String JointMessage = str_len+str_type+message;
		//System.out.println(JointMessage);
		return JointMessage;
	}

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

}

