package demo;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.event.EventListener;
import org.noear.solon.data.tran.TranEvent;
import org.noear.solon.data.tran.TranPhase;

/**
 * @author noear 2023/8/28 created
 */
@Component
public class EventTest implements EventListener<TranEvent> {
    @Override
    public void onEvent(TranEvent tranEvent) throws Throwable {
        if(tranEvent.getPhase() == TranPhase.BEFORE_COMMIT){
            System.out.println(tranEvent.getMeta().message());
        }
    }
}
