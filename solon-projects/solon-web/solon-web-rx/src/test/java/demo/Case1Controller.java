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

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Produces;
import org.noear.solon.web.util.MimeType;
import org.noear.solon.core.handle.Context;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

/**
 * @author noear 2023/6/19 created
 */
@Mapping("case1")
@Controller
public class Case1Controller {
    @Mapping("m1")
    public Mono<String> m1(String name) {
        return Mono.just("Hello " + name);
    }

    @Mapping("f1")
    public Flux<String> f1(String name) {
        return Flux.just("Hello " + name, "hello2 " + name);
    }

    @Produces(MimeType.APPLICATION_X_NDJSON_VALUE)
    @Mapping("f2")
    public Flux<String> f2(String name) {
        return Flux.just("Hello " + name, "hello2 " + name);
    }

    @Mapping("f3")
    public Mono<List<String>> f3(String name) {
        return Flux.just("Hello " + name, "hello2 " + name)
                .collectList();
    }

    @Mapping("t1")
    public Mono<Long> t1(Context ctx) {
        ctx.asyncStart(100L, null);
        return Mono.delay(Duration.ofMillis(500));
    }

    @Mapping("t2")
    public Mono<Long> t2(Context ctx) {
        ctx.asyncStart(500L, null);
        return Mono.delay(Duration.ofMillis(100));
    }
}
