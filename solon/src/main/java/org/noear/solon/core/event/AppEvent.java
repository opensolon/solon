package org.noear.solon.core.event;

import org.noear.solon.SolonApp;
import org.noear.solon.core.AppContext;

/**
 * 应用事件
 *
 * @author noear
 * @since 1.11
 */
public abstract class AppEvent {
    private final SolonApp app;

    /**
     * 应用实例
     * */
    public SolonApp app() {
        return app;
    }

    /**
     * 应用上下文
     * */
    public AppContext context(){
        return app.context();
    }

    public AppEvent(SolonApp app){
        this.app = app;
    }
}
