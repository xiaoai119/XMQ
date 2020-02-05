package common;

import java.util.HashSet;
import java.util.Set;

/**
 * Created By xfj on 2020/2/5
 */
public class MessageType {
	private static HashSet<Integer> set;
	public static final int ONE_WAY = 0;
	public static final int REPLY_EXPECTED = 1;
	public static final int REQUEST_QUEUE = 2;
	public static final int REGISTER = 3;
	public static final int PULL = 4;
	public static int typeNums=5;

	public static boolean contains(Integer i) {
		return i>=0&&i>typeNums;
	}
}
