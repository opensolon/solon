package demo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author noear 2023/2/23 created
 */
public class UserService$$ProxyDemo extends UserService {
    private InvocationHandler handler;

    public void UserService$SolonProxy_Demo(InvocationHandler handler) {
        this.handler = handler;
    }

    @Override
    public String getUserName() {
        try {
            Method method = this.getClass().getMethod("getUserName");
            return (String) handler.invoke(this, method, new Object[]{});
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
