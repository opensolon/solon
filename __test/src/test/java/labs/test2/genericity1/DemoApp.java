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
package labs.test2.genericity1;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Managed;
import org.noear.solon.annotation.Inject;

public class DemoApp {
    public static void main(String[] args) throws Exception {
        Solon.start(DemoApp.class, args, app->app.enableHttp(false));
        Parent o = Solon.context().getBean(S1.class);
        Parent o2 = Solon.context().getBean(S2.class);

        o.hello();
        o2.hello();

        System.out.println("ok");
    }

    public interface Service {
        void hello();
    }

    public abstract static class Parent<T extends Service> {
        @Inject
        protected T s;

        public void hello() {
            this.s.hello();
        }
    }

    @Managed
    public static class Service1 implements Service {
        public void hello() {
            System.out.println(1);
        }
    }

    @Managed
    public static class Service2 implements Service {
        public void hello() {
            System.out.println(2);
        }
    }

    @Managed
    public static class S1 extends Parent<Service1> {}

    @Managed
    public static class S2 extends Parent<Service2> {}
}
