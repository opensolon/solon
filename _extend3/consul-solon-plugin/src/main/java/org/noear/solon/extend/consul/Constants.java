package org.noear.solon.extend.consul;

/**
 * 常量
 *
 * @author 夜の孤城
 * @since 1.2
 * */
interface Constants {
    /**
     * solon.app.name = test-api
     * solon.app.group = test
     *
     * consul.host=localhost
     *
     * #consul.config.enable = true
     * #consul.config.key = test
     * #consul.config.interval = 10s
     *
     * #consul.discovery.enable = true
     * #consul.discovery.hostname = 127.0.0.1
     * #consul.discovery.healthCheckInterval = 10000
     * #consul.discovery.healthCheckPath = /run/check/
     *
     * #consul.locator.enable=true
     * #consul.locator.interval=10s
     *
     */

    String HOST     = "consul.host";
    String TOKEN    = "consul.token";
    String PORT     = "consul.port";

    String CONFIG_ENABLE    = "consul.config.enable";
    String CONFIG_KEY       = "consul.config.key";
    String CONFIG_WATCH     = "consul.config.watch";
    String CONFIG_INTERVAL  = "consul.config.interval";


    String DISCOVERY_ENABLE                 = "consul.discovery.enable";
    String DISCOVERY_HOSTNAME               = "consul.discovery.hostname";
    String DISCOVERY_TAGS                   = "consul.discovery.tags";
    String DISCOVERY_HEALTH_CHECK_PATH      = "consul.discovery.healthCheckPath";
    String DISCOVERY_HEALTH_CHECK_INTERVAL  = "consul.discovery.healthCheckInterval";
    String DISCOVERY_HEALTH_DETECTOR        = "consul.discovery.healthDetector";

    String LOCATOR_ENABLE   = "consul.locator.enable";
    String LOCATOR_INTERVAL = "consul.locator.interval";

}
