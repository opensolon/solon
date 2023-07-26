package org.noear.solon.admin.client.config;

import lombok.Data;

/**
 * 本地模式配置文件
 *
 * @author shaokeyibb
 * @since 2.3
 */
@Data
public class LocalClientProperties implements IClientProperties {

    private boolean enabled = true;

    private String mode = "local";

    private String serverUrl = "http://localhost:8080";

    private long heartbeatInterval = 10 * 1000;

    private long connectTimeout = 5 * 1000;

    private long readTimeout = 5 * 1000;

    private boolean showSecretInformation = false;
}
