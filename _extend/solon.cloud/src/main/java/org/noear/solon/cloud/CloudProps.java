package org.noear.solon.cloud;

import org.noear.solon.Solon;

/**
 * 云服务属性配置
 *
 * @author noear
 * @since 1.2
 */
public class CloudProps {
    private String SERVER = "solon.cloud.@@.server";
    private String TOKEN = "solon.cloud.@@.token";
    private String USERNAME = "solon.cloud.@@.username";
    private String PASSWORD = "solon.cloud.@@.password";

    private String CONFIG_ENABLE = "solon.cloud.@@.config.enable";
    private String CONFIG_LOAD_GROUP = "solon.cloud.@@.config.loadGroup"; //（对某些框架来讲，可能没用处）
    private String CONFIG_LOAD_KEY = "solon.cloud.@@.config.loadKey";

    private String DISCOVERY_ENABLE = "solon.cloud.@@.discovery.enable";
    private String DISCOVERY_HOSTNAME = "solon.cloud.@@.discovery.hostname";
    private String DISCOVERY_TAGS = "solon.cloud.@@.discovery.tags";
    private String DISCOVERY_HEALTH_CHECK_PATH = "solon.cloud.@@.discovery.healthCheckPath";
    private String DISCOVERY_HEALTH_CHECK_INTERVAL = "solon.cloud.@@.discovery.healthCheckInterval";
    private String DISCOVERY_HEALTH_DETECTOR = "solon.cloud.@@.discovery.healthDetector";

    private String EVENT_ENABLE = "solon.cloud.@@.event.enable";
    private String EVENT_SEAL = "solon.cloud.@@.event.seal";

    public CloudProps(String frame) {
        SERVER = SERVER.replace("@@", frame);
        TOKEN = TOKEN.replace("@@", frame);
        USERNAME = USERNAME.replace("@@", frame);
        PASSWORD = PASSWORD.replace("@@", frame);

        CONFIG_ENABLE = CONFIG_ENABLE.replace("@@", frame);
        CONFIG_LOAD_GROUP = CONFIG_LOAD_GROUP.replace("@@", frame);
        CONFIG_LOAD_KEY = CONFIG_LOAD_KEY.replace("@@", frame);

        DISCOVERY_ENABLE = DISCOVERY_ENABLE.replace("@@", frame);
        DISCOVERY_HOSTNAME = DISCOVERY_HOSTNAME.replace("@@", frame);
        DISCOVERY_TAGS = DISCOVERY_TAGS.replace("@@", frame);
        DISCOVERY_HEALTH_CHECK_PATH = DISCOVERY_HEALTH_CHECK_PATH.replace("@@", frame);
        DISCOVERY_HEALTH_CHECK_INTERVAL = DISCOVERY_HEALTH_CHECK_INTERVAL.replace("@@", frame);
        DISCOVERY_HEALTH_DETECTOR = DISCOVERY_HEALTH_DETECTOR.replace("@@", frame);

        EVENT_ENABLE = EVENT_ENABLE.replace("@@", frame);
        EVENT_SEAL = EVENT_SEAL.replace("@@", frame);
    }


    //
    //公共
    //
    public String getServer() {
        return Solon.cfg().get(SERVER);
    }

    public String getToken() {
        return Solon.cfg().get(TOKEN);
    }

    public String getUsername() {
        return Solon.cfg().get(USERNAME);
    }

    public String getPassword() {
        return Solon.cfg().get(PASSWORD);
    }

    //
    //配置
    //
    public boolean getConfigEnable() {
        return Solon.cfg().getBool(CONFIG_ENABLE, true);
    }
    public String getConfigLoadGroup() {
        return Solon.cfg().get(CONFIG_LOAD_GROUP);
    }
    public String getConfigLoadKey() {
        return Solon.cfg().get(CONFIG_LOAD_KEY);
    }


    //
    //发现
    //
    public boolean getDiscoveryEnable() {
        return Solon.cfg().getBool(DISCOVERY_ENABLE, true);
    }

    public String getDiscoveryHostname() {
        return Solon.cfg().get(DISCOVERY_HOSTNAME);
    }
    public String getDiscoveryTags() {
        return Solon.cfg().get(DISCOVERY_TAGS);
    }

    public String getDiscoveryHealthCheckPath() {
        return Solon.cfg().get(DISCOVERY_HEALTH_CHECK_PATH,"/run/check/");
    }

    public String getDiscoveryHealthCheckInterval() {
        return Solon.cfg().get(DISCOVERY_HEALTH_CHECK_INTERVAL,"5s");
    }
    public String getDiscoveryHealthDetector() {
        return Solon.cfg().get(DISCOVERY_HEALTH_DETECTOR);
    }


    //
    //事件
    //
    public boolean getEventEnable() {
        return Solon.cfg().getBool(EVENT_ENABLE, true);
    }
    public String getEventSeal() {
        return Solon.cfg().get(EVENT_SEAL);
    }
}
