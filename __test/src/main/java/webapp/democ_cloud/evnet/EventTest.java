package webapp.democ_cloud.evnet;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventListener;

/**
 * @author noear 2023/7/28 created
 */
@Component
public class EventTest implements EventListener<AppLoadEndEvent> {

    @Override
    public void onEvent(AppLoadEndEvent appLoadEndEvent) throws Throwable {
        new HelloEntity("solon").publish();
    }
}
