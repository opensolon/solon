package features.solon.generic2;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.*;
import org.noear.solon.core.AppContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class GenericsTest {
    static AppContext appContext;

    @BeforeAll
    public static void bef() throws Exception {
        appContext = new AppContext();
        appContext.beanScan(GenericsTest.class);
        appContext.start();
    }

    @AfterAll
    public static void aft() {
        appContext.stop();
    }

    @Test
    public void test() {
        GenericsTest self = appContext.getBean(GenericsTest.class);

        self.wxCallbackContext.check();
        self.fsCallbackContext.check();
    }


    @Inject
    public WxCallbackContext wxCallbackContext;

    @Inject
    public FsCallbackContext fsCallbackContext;

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
            if(socialEventCallbackMap == null){
                socialEventCallbackMap = new HashMap<>();
            }

            System.out.println("socialEventCallbacks: 2, " + socialEventCallbacks.size());
            System.out.println("socialEventCallbackMap: 0, " + socialEventCallbackMap);

            assert socialEventCallbacks.size() == 2; //为 2
            assert socialEventCallbackMap.size() == 0; //为 0（因为没有 name）
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
        public void test2(@Inject(required = false) Map<String, SocialEventCallback<WxEvent, String>> v2) {
            assert v2.size() == 0; //为 0（因为没有 name）
        }
    }
}