package demo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author noear 2023/2/23 created
 */
public class UserService$$ProxyDemo extends UserService {

    private static Method method0;
    private InvocationHandler handler;

    public void UserService$SolonProxy_Demo(InvocationHandler handler) {
        this.handler = handler;
    }

    @Override
    public String getUserName() {
        try {
            return (String) handler.invoke(this, method0, new Object[]{});
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
