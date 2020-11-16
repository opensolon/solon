package webapp.dao;

import org.noear.solon.annotation.Configuration;
import org.noear.solon.core.event.EventListener;

@Configuration
public class ErrorListener implements EventListener<Throwable> {
    @Override
    public void onEvent(Throwable err) {
        err.printStackTrace();
    }
}
