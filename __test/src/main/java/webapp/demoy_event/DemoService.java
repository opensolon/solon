package webapp.demoy_event;

import lombok.Getter;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.event.EventListener;
import org.noear.solon.aspect.annotation.Service;

/**
 * @author noear 2022/5/18 created
 */
@Service
public class DemoService {
    public void hello(String name){
        //发布事件
        EventBus.push(new HelloEvent(name));
    }

    //定义事件模型
    @Getter
    public static class HelloEvent {
        private String name;
        public HelloEvent(String name){
            this.name = name;
        }
    }

    //监听事件并处理
    @Component
    public static class HelloEventListener implements EventListener<HelloEvent>{
        @Override
        public void onEvent(HelloEvent event) throws Throwable {
            event.getName();
        }
    }
}
