package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By xfj on 2020/2/5
 */
public class Topic implements Serializable{
	private static final long serialVersionUID = -3115497946567476212L;

	private ArrayList<Integer> queueIdList;
	private ArrayList<IpNode> ipNodeList;
	String topic_name;
	int queueNum;

	public Topic(String s, int queueNum) {
		topic_name = s;
		this.queueNum = queueNum;
		queueIdList = new ArrayList<Integer>();
		ipNodeList = new ArrayList<IpNode>();
	}
	public Topic(String s, ArrayList<Integer> queueIdList, ArrayList<IpNode> ipNodeList) {
		topic_name = s;
		this.queueIdList = queueIdList;
		this.ipNodeList = ipNodeList;
	}

	public String getTopicName() {
		return topic_name;
	}
	public List<Integer> getQueue() {
		return queueIdList;
	}
	public List<IpNode> getConsumer(){
		return ipNodeList;
	}
	public void addConsumer(IpNode node) {
		ipNodeList.add(node);
	}
	public void deleteConsumer(IpNode ipnode) {
		ipNodeList.remove(ipnode);
	}
	public void addQueueId(int i) {
		queueIdList.add(i);
	}
	public int getQueueNum() {
		return queueNum; 
	}
}
