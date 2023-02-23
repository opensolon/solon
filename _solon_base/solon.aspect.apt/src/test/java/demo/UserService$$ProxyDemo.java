package demo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author noear 2023/2/23 created
 */
public class UserService$$ProxyDemo extends UserService {

    private static Method method0;
    private static Method method1;

    static {
        try {
            method0 = UserService.class.getMethod("getUserName");
            method1 = UserService.class.getMethod("setUserName", String.class);
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }


    private InvocationHandler handler;

    public void UserService$SolonProxy_Demo(InvocationHandler handler) {
        this.handler = handler;
    }

    @Override
    public String getUserName() {
        try {
            return (String) handler.invoke(this, method0, new Object[]{});
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void setUserName(String name) throws RuntimeException {
        try {
            handler.invoke(this, method1, new Object[]{name});
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
