package labs;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * @author noear 2024/7/3 created
 */
public class TypeTest {
    public static void main(String[] args) {
        for(Method method : Model.class.getDeclaredMethods()) {
            System.out.println(method.getName());
            System.out.println(method.getReturnType());
            System.out.println(method.getGenericReturnType());
            System.out.println(method.getGenericReturnType().getClass());
            System.out.println(method.getAnnotatedReturnType());
            for(Type type : method.getGenericParameterTypes()) {
                System.out.println(">>" + type);
            }

            System.out.println("----------");
        }
    }

    public static class Model {
        public String fun1() {
            return "";
        }

        public List<String> fun2() {
            return Collections.emptyList();
        }

        public <T> T fun3(T v) {
            return null;
        }
    }
}
