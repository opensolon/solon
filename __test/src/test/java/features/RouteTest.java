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
import org.noear.solon.Solon;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.Gateway;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.route.PathRule;
import org.noear.solon.core.route.RoutingDefault;
import org.noear.solon.test.SolonTest;
import webapp.App;

/**
 * @author noear 2024/1/2 created
 */
@SolonTest(App.class)
public class RouteTest {
    @Test
    public void routingDefault() {
        //Mvc 里用的 Mapping 路由记录
        RoutingDefault routingDefault = new RoutingDefault("/captchaImage", MethodType.GET, null);
        assert routingDefault.matches(MethodType.GET, "/captchaImage");
        assert routingDefault.matches(MethodType.GET, "/captchaimage") == false;

        routingDefault = new RoutingDefault("/captchaimage", MethodType.GET, null);
        assert routingDefault.matches(MethodType.GET, "/captchaImage") == false;
        assert routingDefault.matches(MethodType.GET, "/captchaimage");
    }

    @Test
    public void authRuleImpl() {
        //AuthRuleImpl 内用的路由规则
        PathRule pathRule = new PathRule();
        pathRule.exclude("/captchaImage");
        assert pathRule.test("/captchaImage") == false;
        assert pathRule.test("/captchaimage");

        pathRule = new PathRule();
        pathRule.exclude("/captchaimage");
        assert pathRule.test("/captchaImage");
        assert pathRule.test("/captchaimage") == false;

        pathRule = new PathRule();
        pathRule.include("/captchaImage");
        assert pathRule.test("/captchaImage");
        assert pathRule.test("/captchaimage") == false;

        pathRule = new PathRule();
        pathRule.include("/captchaimage");
        assert pathRule.test("/captchaImage") == false;
        assert pathRule.test("/captchaimage");
    }

    @Test
    public void gatewayTest() {
        String path = "/demo1/run2/send";
        Handler mainHandler = Solon.app().router().matchMain(new ContextEmpty() {
            @Override
            public String path() {
                return path;
            }

            @Override
            public String method() {
                return "GET";
            }
        });

        assert (mainHandler instanceof Gateway);

        Gateway gateway = (Gateway) mainHandler;
        Handler handler = gateway.getMainRouting().matchOne(path, MethodType.GET);

        assert handler instanceof Handler;
    }
}
