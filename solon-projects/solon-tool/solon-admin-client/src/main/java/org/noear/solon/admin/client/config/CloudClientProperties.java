package org.noear.solon.admin.client.config;

import lombok.Data;

@Data
public class CloudClientProperties implements IClientProperties {

    private boolean enabled = true;

    private String name;

    private String serverUrl;

    private String apiPath = "api";

    private long heartbeatInterval = 10 * 1000;

    private long connectTimeout = 5 * 1000;

    private long readTimeout = 5 * 1000;

    private String metadata = "";

}
