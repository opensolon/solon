package features.solon.generic3;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.*;
import org.noear.solon.core.AppContext;

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
    private WxCallbackContext wxCallbackContext;

    @Inject
    private FsCallbackContext fsCallbackContext;


    public interface SocialEventAware<E extends SocialEventAware<E>> {
    }

    public interface SocialEventCallback<E extends SocialEventAware<E>, S> {
    }

    public static class WxEvent implements SocialEventAware<WxEvent> {
    }

    public static class FsEvent implements SocialEventAware<FsEvent> {
    }

    @Managed("WxUserCallback")
    public static class WxUserCallback implements SocialEventCallback<WxEvent, String> {
    }

    @Managed("WxDeptCallback")
    public static class WxDeptCallback implements SocialEventCallback<WxEvent, String> {
    }

    @Managed("FsUserCallback")
    public static class FsUserCallback implements SocialEventCallback<FsEvent, Integer> {
    }

    @Managed("FsDeptCallback")
    public static class FsDeptCallback implements SocialEventCallback<FsEvent, Integer> {
    }

    public abstract static class AbstractCallbackContext<E extends SocialEventAware<E>, S> {
        @Inject
        protected List<SocialEventCallback<E, S>> socialEventCallbacks;

        @Inject
        protected Map<String, SocialEventCallback<E, S>> socialEventCallbackMap;


        public void check() {
            System.out.println("socialEventCallbacks: 2, " + socialEventCallbacks.size());
            System.out.println("socialEventCallbackMap: 2, " + socialEventCallbackMap.size());

            assert socialEventCallbacks.size() == 2; //为 2
            assert socialEventCallbackMap.size() == 2; //为 2
        }
    }

    @Managed
    public static class WxCallbackContext extends AbstractCallbackContext<WxEvent, String> {
    }

    @Managed
    public static class FsCallbackContext extends AbstractCallbackContext<FsEvent, Integer> {
    }

    @Configuration
    public static class TestConfig {
        @Managed
        public void test1(List<SocialEventCallback<WxEvent, String>> v1) {
            System.out.println("v1: 2, " + v1.size());
            assert v1.size() == 2; //为 2
        }

        @Managed
        public void test2(Map<String, SocialEventCallback<WxEvent, String>> v2) {
            System.out.println("v2: 2, " + v2.size());
            assert v2.size() == 2; //为 2
        }
    }
}