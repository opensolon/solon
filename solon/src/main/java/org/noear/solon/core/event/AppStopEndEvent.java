package org.noear.solon.core.event;

import org.noear.solon.SolonApp;

/**
 * App stop end 事件
 *
 * @author noear
 * @since 2.0
 * */
public class AppStopEndEvent extends AppEvent {
    public AppStopEndEvent(SolonApp app) {
        super(app);
    }
}
