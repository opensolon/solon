package test3;

/**
 * @author noear 2022/1/19 created
 */

import org.noear.solon.Solon;
import org.noear.solon.annotation.*;


@Configuration
public class DemoApp {

    public static void main(String[] args) throws Exception {
        Solon.start(DemoApp.class, args);
    }

    public static class Bean1 {}
    public static class Bean2 {}
    public static class Entity {}

    public interface Base<T, K> {}

    @Component
    public static class CommonBaseImpl implements Base<Bean1, Integer> {}

    @Configuration
    public static class DefaultBaseImpl implements Base<Entity, Integer> {
        @Bean
        public Base<Bean1, Long> base1() { return new Base<Bean1, Long>() {}; }

        @Bean
        public Base<Bean2, Long> base2() { return new Base<Bean2, Long>() {}; }
    }

    public static abstract class BaseController<T, K> {
        @Inject
        protected Base<T, K> service;

        //@Inject //@Inject 与 @Bean 配合才有效；//避免误用，取消函数注入了
        public void setService(Base<T, K> service) {
            if (this.service != service) {
                throw new IllegalStateException();
            }
        }
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