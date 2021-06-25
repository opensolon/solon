package org.noear.solon.cloud.model;

import org.noear.solon.core.handle.Handler;

/**
 * @author noear
 * @since 1.4
 */
public class HandlerEntity {
    private String name;
    private String cron7x;
    private String description;
    private Handler handler;

    public HandlerEntity(String name, String cron7x, String description, Handler handler) {
        this.name = name;
        this.cron7x = cron7x;
        this.description = description;
        this.handler = handler;
    }

    public String getName() {
        return name;
    }

    public String getCron7x() {
        return cron7x;
    }

    public String getDescription() {
        return description;
    }

    public Handler getHandler() {
        return handler;
    }
}
