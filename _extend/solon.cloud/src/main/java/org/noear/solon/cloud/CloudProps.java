package org.noear.solon.cloud;

import org.noear.solon.Solon;

/**
 * @author noear 2021/1/15 created
 */
public class CloudProps {
    public static final String CONFIG_SERVER    = "solon.cloud.config.server";
    public static final String CONFIG_TOKEN     = "solon.cloud.config.token";
    public static final String CONFIG_USERNAME  = "solon.cloud.config.username";
    public static final String CONFIG_PASSWORD  = "solon.cloud.config.password";
    public static final String CONFIG_LOAD      = "solon.cloud.config.load";


    public static final String DISCOVERY_SERVER     = "solon.cloud.discovery.server";
    public static final String DISCOVERY_TOKEN      = "solon.cloud.discovery.token";
    public static final String DISCOVERY_USERNAME   = "solon.cloud.discovery.username";
    public static final String DISCOVERY_PASSWORD   = "solon.cloud.discovery.password";

    public static final String DISCOVERY_HOSTNAME   = "solon.cloud.discovery.hostname";

    public static final String DISCOVERY_HEALTH_CHECK_PATH      = "solon.cloud.discovery.healthCheckPath";
    public static final String DISCOVERY_HEALTH_CHECK_INTERVAL  = "solon.cloud.discovery.healthCheckInterval";


    public static String getConfigServer(){
        return Solon.cfg().get(CONFIG_SERVER);
    }

    public static String getConfigToken(){
        return Solon.cfg().get(CONFIG_TOKEN);
    }

    public static String getConfigUsername(){
        return Solon.cfg().get(CONFIG_USERNAME);
    }

    public static String getConfigPassword(){
        return Solon.cfg().get(CONFIG_PASSWORD);
    }

    public static String getConfigLoad(){
        return Solon.cfg().get(CONFIG_LOAD);
    }


    public static String getDiscoveryServer() {
        return Solon.cfg().get(DISCOVERY_SERVER);
    }

    public static String getDiscoveryToken() {
        return Solon.cfg().get(DISCOVERY_TOKEN);
    }

    public static String getDiscoveryUsername() {
        return Solon.cfg().get(DISCOVERY_USERNAME);
    }

    public static String getDiscoveryPassword() {
        return Solon.cfg().get(DISCOVERY_PASSWORD);
    }

    public static String getDiscoveryHostname() {
        return Solon.cfg().get(DISCOVERY_HOSTNAME);
    }

    public static String getDiscoveryHealthCheckPath() {
        return Solon.cfg().get(DISCOVERY_HEALTH_CHECK_PATH);
    }

    public static String getDiscoveryHealthCheckInterval() {
        return Solon.cfg().get(DISCOVERY_HEALTH_CHECK_INTERVAL);
    }
}
