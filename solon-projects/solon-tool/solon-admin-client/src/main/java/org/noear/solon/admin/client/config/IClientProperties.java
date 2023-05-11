package org.noear.solon.admin.client.config;

public interface IClientProperties {

    boolean isEnabled();

    String getName();

    String getServerUrl();

    String getApiPath();

    long getHeartbeatInterval();

    long getConnectTimeout();

    long getReadTimeout();

    String getMetadata();

}
