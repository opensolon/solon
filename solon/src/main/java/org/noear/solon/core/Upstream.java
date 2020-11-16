package org.noear.solon.core;

/**
 * 负载器（为服务提供一个简单的负载接口）
 *
 * <pre><code>
 * //定义 local upstream
 * @XBean("local")
 * public class TestUpstream implements XUpstream {
 *     @Override
 *     public String getServer() {
 *         //可以有多个server地址，根据策略输出
 *         //
 *         return "http://localhost:8080";
 *     }
 * }
 *
 * //通过 FairyClient 使用，然后调用一个restful api（注：FairyClient 已与 XUpstream 适配）
 * @XBean
 * public class DemoBean{
 *     @FairyClient("local:/demo/hello/")    //此处的local，对上面的local
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
public interface Upstream {

    /**
     * 获取节点
     * */
    String getServer();

    /**
     * 负载器工厂
     * */
    interface Factory{
        Upstream create(String service);
    }
}
