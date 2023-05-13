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

}
