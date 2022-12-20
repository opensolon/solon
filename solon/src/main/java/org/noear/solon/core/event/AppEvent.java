package org.noear.solon.core.event;

import org.noear.solon.SolonApp;
import org.noear.solon.core.AopContext;

/**
 * @author noear
 * @since 1.11
 */
public abstract class AppEvent {
    private final SolonApp app;

    public SolonApp app() {
        return app;
    }

    public AopContext context(){
        return app.context();
    }

    public AppEvent(SolonApp app){
        this.app = app;
    }
}
