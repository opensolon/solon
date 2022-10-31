package features;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author noear 2022/1/21 created
 */
public class DemoHandler implements InvocationHandler {
    public static InvocationHandler global = new DemoHandler();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("log:bef: by DemoHandler");
        return method.invoke(proxy, args);
    }
}
