package test;

/**
 * Created By xfj on 2020/2/21
 */
public class ClassLoaderTest {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        ClassLoader cl = TestClient.class.getClassLoader();

        System.out.println("ClassLoader is:"+cl.toString());

    }

}
