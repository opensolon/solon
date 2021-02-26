package org.noear.solon.logging;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.Plugin;
import org.noear.solon.logging.event.Appender;

import java.util.Properties;


/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        Properties props = app.cfg().getProp("solon.logging.appender");

        if (props.size() > 0) {
            props.forEach((k, v) -> {
                String key = (String) k;
                String val = (String) v;

                if (key.endsWith(".class")) {
                    Appender appender = Utils.newInstance(val);
                    if (appender != null) {
                        AppenderManager.getInstance().register(appender);
                    }
                }
            });
        }
    }
}
