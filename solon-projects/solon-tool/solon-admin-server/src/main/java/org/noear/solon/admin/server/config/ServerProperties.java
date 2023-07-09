package org.noear.solon.admin.server.config;

import lombok.Data;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Inject;

@Condition(onProperty = "${solon.admin.server.enabled:true} = true")
@Component
@Data
public class ServerProperties {

    @Inject(value = "${solon.admin.server.enabled}", required = false)
    private boolean enabled = true;

    @Inject(value = "${solon.admin.server.mode}", required = false)
    private String mode = "local";

    @Inject(value = "${solon.admin.server.heartbeatInterval}", required = false)
    private long heartbeatInterval = 10 * 1000;

    @Inject(value = "${solon.admin.server.clientMonitorPeriod}", required = false)
    private long clientMonitorPeriod = 2 * 1000;

    @Inject(value = "${solon.admin.server.clientMonitorTimeout}", required = false)
    private long connectTimeout = 5 * 1000;

    @Inject(value = "${solon.admin.server.clientMonitorTimeout}", required = false)
    private long readTimeout = 5 * 1000;

}
