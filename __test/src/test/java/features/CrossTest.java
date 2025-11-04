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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.solon.Solon;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.route.Routing;
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
        Collection<Routing<Handler>> routings1 = Solon.app().router().findBy("/demo2/cross1/hello");

        System.out.println(routings1);
        Assertions.assertEquals(1, routings1.size());
        assert routings1.stream().anyMatch(r -> r.method() == MethodType.ALL);

        Collection<Routing<Handler>> routings2 = Solon.app().router().findBy("/demo2/cross1/test");

        System.out.println(routings2);
        Assertions.assertEquals(2, routings2.size());
    }

    @Test
    public void cross13() throws IOException {
        Collection<Routing<Handler>> routings1 = Solon.app().router().findBy("/demo2/cross13/hello");

        System.out.println(routings1);
        Assertions.assertEquals(1, routings1.size());
        assert routings1.stream().anyMatch(r -> r.method() == MethodType.ALL);

        Collection<Routing<Handler>> routings2 = Solon.app().router().findBy("/demo2/cross13/test");

        System.out.println(routings2);
        Assertions.assertEquals(2, routings2.size());
    }

    @Test
    public void cross2() throws IOException {
        Collection<Routing<Handler>> routings1 = Solon.app().router().findBy("/demo2/cross2/hello");

        System.out.println(routings1);
        Assertions.assertEquals(2, routings1.size());
        assert routings1.stream().anyMatch(r -> r.method() == MethodType.SOCKET);

        Collection<Routing<Handler>> routings2 = Solon.app().router().findBy("/demo2/cross2/test");

        System.out.println(routings2);
        Assertions.assertEquals(2, routings2.size());
    }

    @Test
    public void cross22() throws IOException {
        Collection<Routing<Handler>> routings1 = Solon.app().router().findBy("/demo2/cross22/hello");

        System.out.println(routings1);
        Assertions.assertEquals(2, routings1.size());
        assert routings1.stream().anyMatch(r -> r.method() == MethodType.SOCKET);

        Collection<Routing<Handler>> routings2 = Solon.app().router().findBy("/demo2/cross22/test");

        System.out.println(routings2);
        Assertions.assertEquals(2, routings2.size());
    }

    @Test
    public void cross3() throws IOException {
        Collection<Routing<Handler>> routings1 = Solon.app().router().findBy("/demo2/cross3/hello");

        System.out.println(routings1);
        Assertions.assertEquals(2, routings1.size());
        assert routings1.stream().anyMatch(r -> r.method() == MethodType.SOCKET);

        Collection<Routing<Handler>> routings2 = Solon.app().router().findBy("/demo2/cross3/test");

        System.out.println(routings2);
        Assertions.assertEquals(3, routings2.size());
        assert routings2.stream().anyMatch(r -> r.method() == MethodType.SOCKET);
        assert routings2.stream().anyMatch(r -> r.method() == MethodType.OPTIONS);
    }

    @Test
    public void cross32() throws IOException {
        Collection<Routing<Handler>> routings1 = Solon.app().router().findBy("/demo2/cross32/hello");

        System.out.println(routings1);
        Assertions.assertEquals(2, routings1.size());
        assert routings1.stream().anyMatch(r -> r.method() == MethodType.SOCKET);

        Collection<Routing<Handler>> routings2 = Solon.app().router().findBy("/demo2/cross32/test");

        System.out.println(routings2);
        Assertions.assertEquals(3, routings2.size());
        assert routings2.stream().anyMatch(r -> r.method() == MethodType.SOCKET);
        assert routings2.stream().anyMatch(r -> r.method() == MethodType.OPTIONS);
    }
}