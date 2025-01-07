package proxy2;

import org.noear.solon.core.AppContext;
import org.noear.solon.proxy.ProxyUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author noear 2025/1/3 created
 */
public class Demo1 {
    public String test() {
        return "test";
    }

    /// ///////////////

    public static void main(String[] args) {
        AppContext appContext = new AppContext();
        InvocationHandler invocationHandler = new InvocationHandlerImpl(new Demo1());

        Demo1 demo1Proxy = ProxyUtil.newProxyInstance(appContext, Demo1.class, invocationHandler);

        System.out.println(demo1Proxy.test());
    }


    public static class InvocationHandlerImpl implements InvocationHandler {
        private Object target;

        public InvocationHandlerImpl(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("bef...");
            Object rst = method.invoke(target, args);
            System.out.println("aft...");

            return rst;
        }
    }
}