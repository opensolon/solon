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
package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.AppContext;
import org.noear.solon.proxy.ProxyUtil;
import thirdparty.Demo;
import thirdparty.DemoApi;

/**
 * @author noear 2022/1/21 created
 */
public class ProxyUtilTest {
    @Test
    public void case1() {
        AppContext appContext = new AppContext();

        Demo demo_raw = new Demo();
        Demo demoProxy = ProxyUtil.newProxyInstance(appContext, Demo.class, (proxy, method, args1) -> {
            System.out.println(proxy.getClass().getName());
            System.out.println(method.getName());
            return method.invoke(demo_raw, args1) + ":1";
        });

        assert demoProxy.hello().endsWith(":1");

        DemoApi demoApiProxy = ProxyUtil.newProxyInstance(appContext, DemoApi.class, (proxy, method, args) -> {
            System.out.println(proxy.getClass().getName());
            System.out.println(method.getName());
            return ":1";
        });

        assert demoApiProxy.hello().endsWith(":1");
    }
}