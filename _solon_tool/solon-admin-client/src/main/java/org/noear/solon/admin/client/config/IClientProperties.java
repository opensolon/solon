package org.noear.solon.admin.client.config;

public interface IClientProperties {

    boolean isEnabled();

    String getMode();

    String getServerUrl();

    long getHeartbeatInterval();

    long getConnectTimeout();

    long getReadTimeout();

    String getMetadata();

}
