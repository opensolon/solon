package proxy2;

import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.proxy.asm.AsmProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author noear 2025/1/3 created
 */
public class Demo2 {
    public String test() {
        return "test";
    }

    /// ///////////////

    public static void main(String[] args) {
        Solon.start(Demo2.class, args);
        InvocationHandler invocationHandler = new InvocationHandlerImpl(new Demo2());

        Demo2 demo1Proxy = (Demo2) AsmProxy.newProxyInstance(Solon.context(), invocationHandler, Demo2.class);

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