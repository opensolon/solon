package org.noear.solon.core.event;

import org.noear.solon.SolonApp;

/**
 * Bean load end 事件
 *
 * @author noear
 * @since 1.1
 * */
@Deprecated
public class BeanLoadEndEvent extends AppEvent {
    public BeanLoadEndEvent(SolonApp app) {
        super(app);
    }
}
