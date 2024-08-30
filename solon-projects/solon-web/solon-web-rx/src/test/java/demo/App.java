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
package demo;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Produces;
import org.noear.solon.boot.web.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author noear 2023/6/19 created
 */
@Controller
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args);
    }

    @Mapping("t1")
    public Mono<String> t1(String name) {
        return Mono.just("Hello " + name);
    }

    @Mapping("t2")
    public Flux<String> t2(String name) {
        return Flux.just("Hello " + name, "hello2 " + name);
    }

    @Produces(MimeType.APPLICATION_X_NDJSON_VALUE)
    @Mapping("t3")
    public Flux<String> t3(String name) {
        return Flux.just("Hello " + name, "hello2 " + name);
    }
}
