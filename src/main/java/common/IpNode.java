package common;

import java.io.Serializable;

/**
 * Created By xfj on 2020/2/5
 */
public class IpNode implements Serializable {
    private static final long serialVersionUID = 4568160852837573117L;
    private String ip;
    private int port;

    public IpNode(String ip,int port){
        this.port=port;
        this.ip=ip;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
