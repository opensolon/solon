package org.noear.solon.core.event;

import org.noear.solon.SolonApp;

/**
 * App load end 事件
 *
 * @author noear
 * @since 1.1
 * */
public class AppLoadEndEvent  extends AppEvent {
    public AppLoadEndEvent(SolonApp app) {
        super(app);
    }
}
