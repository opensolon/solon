package test1;

import model.UserModel;
import org.junit.Test;
import org.noear.solon.core.wrap.ClassWrap;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class SpeetTest {
    @Test
    public void demo1() {
        long start = System.currentTimeMillis();

        for (int i = 0; i < 100000; i++) {
            for(Method m: UserModel.class.getDeclaredMethods()){
                for(Parameter p : m.getParameters()){

                }
            }
        }

        long times = System.currentTimeMillis() - start;

        System.out.println("用时：" + times);
    }

    @Test
    public void demo2() {
        long start = System.currentTimeMillis();

        for (int i = 0; i < 100000; i++) {
            for(Method m: ClassWrap.get(UserModel.class).getMethods()){
                for(Parameter p : m.getParameters()){

                }
            }
        }

        long times = System.currentTimeMillis() - start;

        System.out.println("用时：" + times);
    }
}
