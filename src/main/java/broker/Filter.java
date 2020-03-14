package broker;

import common.IpNode;
import common.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created By xfj on 2020/3/14
 */
public class Filter {
    List<IpNode> index;//客户端索引

    public Filter(List<IpNode> index) {
        this.index = index;
    }

    public HashMap<IpNode, List<Message>> filter(List<Message> list) {
        //将Message按照分发地址分类
        HashMap<IpNode, List<Message>> map = new HashMap<IpNode, List<Message>>();
        //初始化
        for (IpNode address : index) {
            if (map.get(address) == null) {
                map.put(address, new ArrayList<Message>());
            }
        }
        //遍历消息，将每条message分发到对应的list
        Iterator<Message> iterator = list.iterator();
        while (iterator.hasNext()) {
            Message message = iterator.next();
            //根据消息的topic获取消费者的地址端口
            List<IpNode> consumerAddress = message.getTopic().getConsumer();
            Iterator<IpNode> it = consumerAddress.iterator();
            while (it.hasNext()) {
                IpNode address = it.next();
                if (map.containsKey(address)) {
                    List<Message> messages = map.get(address);
                    messages.add(message);
                }
            }
            return map;
        }
        return  map;
    }
}
