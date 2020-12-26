package org.noear.solon.extend.consul;

/**
 * 常量
 *
 * @author 夜の孤城
 * @since 1.2
 * */
interface Constants {
    /**
     * solon.app.name=test-api
     * solon.app.group=test
     *
     * consul.host=localhost
     *
     * consul.config.enable=true
     * consul.config.key=test
     * consul.config.interval=10000
     *
     * consul.discovery.enable=true
     * consul.discovery.ip-address=127.0.0.1
     * consul.discovery.check.interval=10000
     * consul.discovery.check.path=/run/check/
     *
     * consul.locator.enable=true
     * consul.locator.interval=10000
     *
     */

    String HOST = "consul.host";

    String CONFIG_ENABLE = "consul.config.enable";
    String CONFIG_KEY = "consul.config.key";
    String CONFIG_INTERVAL="consul.config.interval";

    String DISCOVERY_ENABLE = "consul.discovery.enable";
    String DISCOVERY_HOSTNAME = "consul.discovery.hostname";
    String DISCOVERY_TAGS = "consul.discovery.tags";
    String DISCOVERY_HEALTH_CHECK_URL = "consul.discovery.healthCheckUrl";
    String DISCOVERY_HEALTH_CHECK_INTERVAL = "consul.discovery.healthCheckInterval";

    String LOCATOR_ENABLE = "consul.locator.enable";
    String LOCATOR_INTERVAL="consul.locator.interval";

}
