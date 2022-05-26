package webapp.dao;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.core.event.EventListener;

@Slf4j
@Configuration
public class ErrorListener implements EventListener<Throwable> {
    @Override
    public void onEvent(Throwable err) {
        //log.error("onEvent", err);
    }
}
