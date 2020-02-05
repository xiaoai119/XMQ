package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created By xfj on 2020/2/5
 */
public class Topic implements Serializable{
	private static final long serialVersionUID = -3115497946567476212L;

	private ArrayList<Integer> queueIdList;
	private ArrayList<ConsumerNode> consumerNodeList;
	String topic_name;
	int queueNum;

	public Topic(String s, int queueNum) {
		topic_name = s;
		this.queueNum = queueNum;
		queueIdList = new ArrayList<Integer>();
		consumerNodeList = new ArrayList<ConsumerNode>();
	}
	public Topic(String s, ArrayList<Integer> queueIdList, ArrayList<ConsumerNode> consumerNodeList) {
		topic_name = s;
		this.queueIdList = queueIdList;
		this.consumerNodeList = consumerNodeList;
	}

	public String getTopicName() {
		return topic_name;
	}
	public List<Integer> getQueue() {
		return queueIdList;
	}
	public List<ConsumerNode> getConsumer(){
		return consumerNodeList;
	}
	public void addConsumer(ConsumerNode node) {
		consumerNodeList.add(node);
	}
	public void deleteConsumer(ConsumerNode ipnode) {
		consumerNodeList.remove(ipnode);
	}
	public void addQueueId(int i) {
		queueIdList.add(i);
	}
	public int getQueueNum() {
		return queueNum; 
	}
}
