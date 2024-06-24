package labs;

import org.noear.solon.core.wrap.MethodKey;

import java.lang.reflect.Method;

/**
 * @author noear 2024/6/24 created
 */
public class MethodTest {
    public static void main(String[] args) {
        Method main0 = MethodTest.class.getDeclaredMethods()[0];
        MethodKey methodKey = new MethodKey(main0, MethodTest.class);

        System.out.println(methodKey.hashCode());
        System.out.println(methodKey);

        MethodKey methodKey2 = new MethodKey(main0, MethodTest.class);
        System.out.println(methodKey2.hashCode());
        System.out.println(methodKey2);
    }
}
