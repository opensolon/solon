package labs.genericsTest;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventListener;

import java.util.List;
import java.util.Map;

@Configuration
public class GenericsTest implements EventListener<AppLoadEndEvent> {

    @Inject
    private WxCallbackContext wxCallbackContext;

    @Inject
    private FsCallbackContext fsCallbackContext;

    @Override
    public void onEvent(AppLoadEndEvent event) throws Throwable {
        wxCallbackContext.check();
        fsCallbackContext.check();
    }

    public static void main(String[] args) throws Exception {
        Solon.start(GenericsTest.class, args);
    }

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

        @Inject
        protected Map<String, SocialEventCallback<E, S>> socialEventCallbackMap;

        /**
         * 子类指定了具体的泛型，所以这两个集合的元素数量应该都是 2
         * 实际上，socialEventCallbacks.size() == 4，
         *        socialEventCallbackMap.size() == 1
         */
        public void check() {
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
        @Bean
        public void test1(List<SocialEventCallback<WxEvent, String>> v1) {
            System.out.println("v1: 2, " + v1.size());
            assert v1.size() == 2; //为 2
        }

        @Bean
        public void test2(Map<String, SocialEventCallback<WxEvent, String>> v2) {
            System.out.println("v2: 0, " + v2.size());
            assert v2.size() == 0; //为 0, 因为都没有名字
        }
    }
}