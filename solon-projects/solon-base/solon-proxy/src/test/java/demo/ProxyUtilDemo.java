package demo;

import org.noear.solon.Solon;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.proxy.BeanProxy;
import org.noear.solon.proxy.ProxyUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *
 * @author noear 2025/10/27 created
 *
 */
public class ProxyUtilDemo {
    //代理
    public static Object doProxy(Object bean) {
        Object tmp = ProxyUtil.newProxyInstance(bean.getClass(), (proxy, method, args) -> {
            //执行之前
            try {
                return method.invoke(bean, args);
            } finally {
                //执行之后
            }
        });
        return tmp;
    }

    public static void main(String[] args) {
        Solon.start(ProxyUtilDemo.class, args, app->{
            app.context().getWrapAsync(ProxyUtilDemo.class, bw->{
                bw.proxySet((bw1, bean) -> doProxy(bean));
            });
        });
    }
}
