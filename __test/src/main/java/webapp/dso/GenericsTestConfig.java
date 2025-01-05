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
package webapp.dso;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class GenericsTestConfig {

    @Inject
    public static WxCallbackContext wxCallbackContext;

    @Inject
    public static FsCallbackContext fsCallbackContext;

    public interface SocialEventAware<E extends SocialEventAware<E>> {
    }

    public interface SocialEventCallback<E extends SocialEventAware<E>, S> {
    }

    public static class WxEvent implements SocialEventAware<WxEvent> {
    }

    public static class FsEvent implements SocialEventAware<FsEvent> {
    }

    @Component
    public static class WxUserCallback implements SocialEventCallback<WxEvent, String> {
    }

    @Component
    public static class WxDeptCallback implements SocialEventCallback<WxEvent, String> {
    }

    @Component
    public static class FsUserCallback implements SocialEventCallback<FsEvent, Integer> {
    }

    @Component
    public static class FsDeptCallback implements SocialEventCallback<FsEvent, Integer> {
    }

    public abstract static class AbstractCallbackContext<E extends SocialEventAware<E>, S> {
        @Inject
        protected List<SocialEventCallback<E, S>> socialEventCallbacks;

        @Inject(required = false)
        protected Map<String, SocialEventCallback<E, S>> socialEventCallbackMap;

        /**
         * 子类指定了具体的泛型，所以这两个集合的元素数量应该都是 2
         * 实际上，socialEventCallbacks.size() == 4，
         * socialEventCallbackMap.size() == 1
         */
        public void check() {
            if (socialEventCallbackMap == null) {
                socialEventCallbackMap = new HashMap<>();
            }

            System.out.println("socialEventCallbacks: 2, " + socialEventCallbacks.size());
            System.out.println("socialEventCallbackMap: 0, " + socialEventCallbackMap.size());

            assert socialEventCallbacks.size() == 2; //为 2
            assert socialEventCallbackMap.size() == 0; //为 0, 因为都没有名字
        }
    }

    @Component
    public static class WxCallbackContext extends AbstractCallbackContext<WxEvent, String> {
    }

    @Component
    public static class FsCallbackContext extends AbstractCallbackContext<FsEvent, Integer> {
    }

    @Configuration
    public static class TestConfig {
        static List<SocialEventCallback<WxEvent, String>> v1;
        static Map<String, SocialEventCallback<WxEvent, String>> v2;

        public static void check() {
            System.out.println("v1: 2, " + v1.size());
            assert v1.size() == 2; //为 2

            System.out.println("v2: 0, " + v2.size());
            assert v2.size() == 0; //为 0, 因为都没有名字
        }

        @Bean
        public void test1(List<SocialEventCallback<WxEvent, String>> v1) {
            this.v1 = v1;
            System.out.println("v1: 2, " + v1.size());
            assert v1.size() == 2; //为 2
        }

        @Bean
        public void test2(@Inject(required = false) Map<String, SocialEventCallback<WxEvent, String>> v2) {
            this.v2 = v2;
            assert v2.size() ==0; //因为都没有名字
        }
    }
}