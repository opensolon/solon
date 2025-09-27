/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.core;

import org.noear.solon.Solon;

import java.net.URI;

/**
 * 负载均衡器（为服务提供一个简单的负载接口；起到适配中介效果）
 *
 * <pre>{@code
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
 *     @NamiClient(name="local", path="/demo/hello/")    //此处的local，对上面的local
 *     HelloService demo;
 * }
 *
 * public interface HelloService{
 *     String hello();
 * }
 * }</pre>
 *
 * @author noear
 * @since 1.0
 * */
@FunctionalInterface
public interface LoadBalance {
    static String URI_SCHEME = "lb";

    /**
     * 解析并生成负载均衡器
     */
    static LoadBalance parse(String uriStr) {
        if (uriStr.contains("://")) {
            return parse(URI.create(uriStr));
        } else {
            //as service
            return get(uriStr);
        }
    }

    /**
     * 解析并生成负载均衡器
     */
    static LoadBalance parse(URI uri) {
        if (URI_SCHEME.equals(uri.getScheme())) {
            //"lb://"
            return new LoadBalanceWrap(null, get(uri.getHost()), uri.getPort());
        } else {
            //"http://","ftp://"...
            return new LoadBalanceWrap(uri.getScheme() + "://" + uri.getAuthority(), null, -1);
        }
    }

    /**
     * 获取负载均衡器
     *
     * @param service 服务名
     */
    static LoadBalance get(String service) {
        return get("", service);
    }

    /**
     * 获取负载均衡器
     *
     * @param service 服务名
     * @param group   服务分组
     */
    static LoadBalance get(String group, String service) {
        return FactoryManager.getGlobal().newLoadBalance(group, service);
    }


    /**
     * 获取节点
     */
    default String getServer() {
        return getServer(0);
    }

    /**
     * 获取节点
     *
     * @param port 指定端口
     */
    String getServer(int port);

    /**
     * 负载器工厂
     */
    interface Factory {
        LoadBalance create(String group, String service);
    }

    /**
     * 负载均衡器包装器
     */
    static class LoadBalanceWrap implements LoadBalance {
        private String server0;
        private LoadBalance lb0;
        private int port0;

        public LoadBalanceWrap(String server, LoadBalance lb, int port) {
            this.server0 = server;
            this.lb0 = lb;
            this.port0 = port;
        }

        @Override
        public String getServer(int port) {
            if (server0 == null) {
                if (lb0 == null) {
                    return null;
                } else {
                    if (port0 > 0) {
                        return lb0.getServer(port0);
                    } else {
                        return lb0.getServer(port);
                    }
                }
            } else {
                return server0;
            }
        }
    }
}