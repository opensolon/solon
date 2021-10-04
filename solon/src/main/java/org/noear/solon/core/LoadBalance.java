package org.noear.solon.core;

import org.noear.solon.Solon;

/**
 * 负载均衡器（为服务提供一个简单的负载接口；起到适配中介效果）
 *
 * <pre><code>
 * //定义 local LoadBalance
 * @Component("local")
 * public class TestLoadBalance implements LoadBalance {
 *     @Override
 *     public String getServer() {
 *         //可以有多个server地址，根据策略输出
 *         //
 *         return "http://localhost:8080";
 *     }
 * }
 *
 * //通过 NamiClient 使用，然后调用一个restful api（注：NamiClient 已与 LoadBalance 适配）
 * @Component
 * public class DemoBean{
 *     @NamiClient("local:/demo/hello/")    //此处的local，对上面的local
 *     HelloService demo;
 * }
 *
 * public interface HelloService{
 *     String hello();
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
@FunctionalInterface
public interface LoadBalance {
    static LoadBalance get(String service) {
        return get("", service);
    }

    static LoadBalance get(String group, String service) {
        return Bridge.upstreamFactory().create(group, service);
    }


    /**
     * 获取节点
     */
    String getServer();

    /**
     * 负载器工厂
     */
    interface Factory {
        LoadBalance create(String group, String service);
    }
}
