package org.noear.solon.extend.consul;

/**
 * 常量
 *
 * @author 夜の孤城
 * @since 1.2
 * */
interface Constants {
    /**
     * application.name=test
     * consul.service=test
     * consul.address=localhost
     * consul.discovery.enable=true
     * consul.discovery.check.interval=10
     * consul.discovery.check.path=/actuator/health
     * consul.locator.enable=true
     * consul.config.enable=true
     * consul.config.format=properties
     */
    String APP_NAME = "application.name";
    String APP_SERVICE_NAME = "application.service";
    String HOST = "consul.host";
    String CONFIG_ENABLE = "consul.config.enable";
    String DISCOVERY_ENABLE = "consul.discovery.enable";
    String DISCOVERY_ADDRESS = "consul.discovery.ip-address";
    String DISCOVERY_INTERVAL = "consul.discovery.check.interval";
    String DISCOVERY_CHECK_PATH = "consul.discovery.check.path";
    String LOCATOR_ENABLE = "consul.locator.enable";
    String LOCATOR_INTERVAL="consul.locator.interval";
    String CONFIG_INTERVAL="consul.config.interval";

}
