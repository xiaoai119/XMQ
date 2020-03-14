package util;

/**
 * Created By xfj on 2020/2/20
 */
public class SequenceUtil {

    private int count = 0;
    public synchronized int getSequence() {
        //唯一id生成，这里先简单count++
        return count++;
    }
}