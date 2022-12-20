package org.noear.solon.core.event;

import org.noear.solon.SolonApp;

/**
 * Bean load end 事件
 *
 * @author noear
 * @since 1.1
 * */
public class AppBeanLoadEndEvent extends BeanLoadEndEvent {
    public AppBeanLoadEndEvent(SolonApp app) {
        super(app);
    }
}
