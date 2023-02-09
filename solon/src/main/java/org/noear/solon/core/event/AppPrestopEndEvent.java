package org.noear.solon.core.event;

import org.noear.solon.SolonApp;

/**
 * App prestop end 事件
 *
 * @author noear
 * @since 2.0
 * */
public class AppPrestopEndEvent extends AppEvent {
    public AppPrestopEndEvent(SolonApp app) {
        super(app);
    }
}
