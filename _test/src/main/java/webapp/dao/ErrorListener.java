package webapp.dao;

import org.noear.solon.annotation.XConfiguration;
import org.noear.solon.core.XEventListener;

@XConfiguration
public class ErrorListener implements XEventListener<Throwable> {
    @Override
    public void onEvent(Throwable err) {
        err.printStackTrace();
    }
}
