package org.noear.solon.core.event;

import org.noear.solon.SolonApp;

/**
 * Plugin init end 事件
 *
 * @author noear
 * @since 1.10
 * */
public class AppPluginInitEndEvent extends PluginInitEndEvent {
    public AppPluginInitEndEvent(SolonApp app) {
        super(app);
    }
}
