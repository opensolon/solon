package features;

/**
 * @author noear 2023/5/4 created
 */
public class TypeTest {
    public static void main(String[] args) throws Exception{
        Class<?> clz = Class.forName("java.lang.String[]"); //"Ljava.lang.String"
        System.out.println(clz.getName());
    }
}
