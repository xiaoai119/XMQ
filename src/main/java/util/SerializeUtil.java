package util;

import java.io.*;

/**
 * Created By xfj on 2020/2/5
 */
public class SerializeUtil {
    public static byte[] Obj2Byte(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.flush();
        baos.flush();
        byte[] bytes = baos.toByteArray();
        oos.close();
        baos.close();
        return bytes;
    }

    public static Object Byte2Obj(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object object = ois.readObject();
        ois.close();
        bais.close();
        return object;
    }
}
