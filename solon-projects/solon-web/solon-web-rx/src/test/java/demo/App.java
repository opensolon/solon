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
package demo;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Produces;
import org.noear.solon.boot.web.MimeType;
import org.noear.solon.core.handle.Context;
import org.noear.solon.rx.Baba;
import org.noear.solon.rx.Mama;

import java.time.Duration;
import java.util.List;

/**
 * @author noear 2023/6/19 created
 */
@Controller
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args);
    }

    @Mapping("m1")
    public Baba<String> m1(String name) {
        return Baba.just("Hello " + name);
    }

    @Mapping("f1")
    public Mama<String> f1(String name) {
        return Mama.just("Hello " + name, "hello2 " + name);
    }

    @Produces(MimeType.APPLICATION_X_NDJSON_VALUE)
    @Mapping("f2")
    public Mama<String> f2(String name) {
        return Mama.just("Hello " + name, "hello2 " + name);
    }

    @Mapping("f3")
    public Baba<List<String>> f3(String name) {
        return Mama.just("Hello " + name, "hello2 " + name)
                .collectList();
    }

    @Mapping("t1")
    public Baba<Long> t1(Context ctx) {
        ctx.asyncStart(100L, null);
        return Baba.delay(Duration.ofMillis(500));
    }

    @Mapping("t2")
    public Baba<Long> t2(Context ctx) {
        ctx.asyncStart(500L, null);
        return Baba.delay(Duration.ofMillis(100));
    }
}
