package org.noear.solon.admin.server.config;

import lombok.Data;
import org.noear.solon.annotation.Inject;

@Data
public class ServerProperties {

    @Inject("${solon.admin.server.enabled}")
    private boolean enabled = true;

    @Inject("${server.contextPath}")
    private String contextPath = "";

}
