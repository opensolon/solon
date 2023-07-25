package org.noear.solon.admin.client.config;

/**
 * @author shaokeyibb
 * @since 2.3
 */
public interface IClientProperties {

    boolean isEnabled();

    String getMode();

    String getServerUrl();

    long getHeartbeatInterval();

    long getConnectTimeout();

    long getReadTimeout();

    String getMetadata();

    boolean isShowSecretInformation();

}
