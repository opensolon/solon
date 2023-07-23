package features;

import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.event.EventListener;

import java.util.HashSet;

/**
 * @author noear 2023/7/23 created
 */
public class EventTest {
    public static void main(String args[]){
        DemoEventListener listener = new DemoEventListener();

        //订阅
        EventBus.subscribe(DemoEvent.class, e -> { System.out.println("fun"); });
        EventBus.subscribe(DemoEvent.class, 2, listener);
        //推送
        EventBus.push(new DemoEvent());
        //取消订阅
        EventBus.unsubscribe(listener);

        System.out.println("ok");

    }

    public static class DemoEventListener implements EventListener<DemoEvent>{
        @Override
        public void onEvent(DemoEvent demoEvent) throws Throwable {
            System.out.println("clz");
        }
    }

    public static class DemoEvent {

    }
}
