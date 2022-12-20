package org.noear.solon.core.event;

import org.noear.solon.SolonApp;

/**
 * App init end 事件
 *
 * @author noear
 * @since 1.3
 * */
public class AppInitEndEvent extends AppEvent {
    public AppInitEndEvent(SolonApp app) {
        super(app);
    }
}
