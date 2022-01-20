package test2;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.Aop;

/**
 * @author noear 2022/1/17 created
 */
public class DemoApp {
    public static void main(String[] args) throws Exception {
        Solon.start(DemoApp.class, args);
        Parent o = Aop.get(S1.class);
        Parent o2 = Aop.get(S2.class);

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

    @Component
    public static class Service1 implements Service {
        public void hello() {
            System.out.println(1);
        }
    }

    @Component
    public static class Service2 implements Service {
        public void hello() {
            System.out.println(2);
        }
    }

    @Component
    public static class S1 extends Parent<Service1> {}

    @Component
    public static class S2 extends Parent<Service2> {}
}
