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
package labs.test2.genericity2;

import org.noear.solon.Solon;
import org.noear.solon.annotation.*;

/**
 * 测试范围注入
 * */
@Configuration
public class DemoApp {

    public static void main(String[] args) throws Exception {
        Solon.start(DemoApp.class, args, app->app.enableHttp(false));
    }

    public static class Bean1 {}
    public static class Bean2 {}
    public static class Entity {}

    public interface Base<T, K> {}

    @Component
    public static class CommonBaseImpl implements Base<Bean1, Integer> {}

    @Configuration
    public static class DefaultBaseImpl implements Base<Entity, Integer> {
        @Inject
        private DefaultBaseImpl defaultBase2;

        @Inject
        private Base<Entity, Integer> defaultBase3;

        @Bean
        public Base<Bean1, Long> base1() { return new Base<Bean1, Long>() {}; }

        @Bean
        public Base<Bean2, Long> base2() { return new Base<Bean2, Long>() {}; }
    }

    public static abstract class BaseController<T, K> {
        @Inject
        protected Base<T, K> service;
    }

    public static abstract class IntBaseController<T> extends BaseController<T, Integer> {}

    @Component
    public static class DefaultController extends IntBaseController<Entity> {
        @Inject
        private DefaultBaseImpl defaultBase;
        @Inject
        private Base<Bean1, Long> bean1Base;
        @Inject
        private Base<Bean2, Long> bean2Base;

        @Init
        public void afterPropertiesSet() {
            if (this.defaultBase != this.service) {
                throw new IllegalStateException();
            }else{
                System.out.println("ok");
            }
        }
    }
}