package org.noear.solon.core.event;

import org.noear.solon.SolonApp;

/**
 * Plugin load end 事件
 *
 * @author noear
 * @since 1.1
 * */
public class AppPluginLoadEndEvent extends PluginLoadEndEvent {
    public AppPluginLoadEndEvent(SolonApp app) {
        super(app);
    }
}
