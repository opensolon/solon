/*
 * Copyright 2017-2024 noear.org and authors
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.Solon;
import org.noear.solon.core.handle.Endpoint;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.route.Routing;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.io.IOException;
import java.util.Collection;

/**
 * @author noear
 * @since 2.6
 */
@SolonTest(App.class)
public class CrossTest {
    @Test
    public void cross1() throws IOException {
        Collection<Routing<Handler>> routings1 = Solon.app().router().getBy("/demo2/cross1/hello", Endpoint.main);

        System.out.println(routings1);
        assert routings1.size() == 1;
        assert routings1.stream().anyMatch(r -> r.method() == MethodType.ALL);

        Collection<Routing<Handler>> routings2 = Solon.app().router().getBy("/demo2/cross1/test", Endpoint.main);

        System.out.println(routings2);
        assert routings2.size() == 2;
    }

    @Test
    public void cross13() throws IOException {
        Collection<Routing<Handler>> routings1 = Solon.app().router().getBy("/demo2/cross13/hello", Endpoint.main);

        System.out.println(routings1);
        assert routings1.size() == 1;
        assert routings1.stream().anyMatch(r -> r.method() == MethodType.ALL);

        Collection<Routing<Handler>> routings2 = Solon.app().router().getBy("/demo2/cross13/test", Endpoint.main);

        System.out.println(routings2);
        assert routings2.size() == 2;
    }

    @Test
    public void cross2() throws IOException {
        Collection<Routing<Handler>> routings1 = Solon.app().router().getBy("/demo2/cross2/hello", Endpoint.main);

        System.out.println(routings1);
        assert routings1.size() == 2;
        assert routings1.stream().anyMatch(r -> r.method() == MethodType.SOCKET);

        Collection<Routing<Handler>> routings2 = Solon.app().router().getBy("/demo2/cross2/test", Endpoint.main);

        System.out.println(routings2);
        assert routings2.size() == 2;
    }

    @Test
    public void cross22() throws IOException {
        Collection<Routing<Handler>> routings1 = Solon.app().router().getBy("/demo2/cross22/hello", Endpoint.main);

        System.out.println(routings1);
        assert routings1.size() == 2;
        assert routings1.stream().anyMatch(r -> r.method() == MethodType.SOCKET);

        Collection<Routing<Handler>> routings2 = Solon.app().router().getBy("/demo2/cross22/test", Endpoint.main);

        System.out.println(routings2);
        assert routings2.size() == 2;
    }

    @Test
    public void cross3() throws IOException {
        Collection<Routing<Handler>> routings1 = Solon.app().router().getBy("/demo2/cross3/hello", Endpoint.main);

        System.out.println(routings1);
        assert routings1.size() == 2;
        assert routings1.stream().anyMatch(r -> r.method() == MethodType.SOCKET);

        Collection<Routing<Handler>> routings2 = Solon.app().router().getBy("/demo2/cross3/test", Endpoint.main);

        System.out.println(routings2);
        assert routings2.size() == 3;
        assert routings2.stream().anyMatch(r -> r.method() == MethodType.SOCKET);
        assert routings2.stream().anyMatch(r -> r.method() == MethodType.OPTIONS);
    }

    @Test
    public void cross32() throws IOException {
        Collection<Routing<Handler>> routings1 = Solon.app().router().getBy("/demo2/cross32/hello", Endpoint.main);

        System.out.println(routings1);
        assert routings1.size() == 2;
        assert routings1.stream().anyMatch(r -> r.method() == MethodType.SOCKET);

        Collection<Routing<Handler>> routings2 = Solon.app().router().getBy("/demo2/cross32/test", Endpoint.main);

        System.out.println(routings2);
        assert routings2.size() == 3;
        assert routings2.stream().anyMatch(r -> r.method() == MethodType.SOCKET);
        assert routings2.stream().anyMatch(r -> r.method() == MethodType.OPTIONS);
    }
}