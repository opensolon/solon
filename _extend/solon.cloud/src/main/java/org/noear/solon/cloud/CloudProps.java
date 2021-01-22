package org.noear.solon.cloud;

import org.noear.solon.Solon;
import org.noear.solon.Utils;

/**
 * 云服务属性配置
 *
 * @author noear
 * @since 1.2
 */
public class CloudProps {
    public static String LOG_DEFAULT_LOGGER;

    private String SERVER = "solon.cloud.@@.server";
    private String TOKEN = "solon.cloud.@@.token";
    private String USERNAME = "solon.cloud.@@.username";
    private String PASSWORD = "solon.cloud.@@.password";

    //配置服务相关
    private String CONFIG_ENABLE = "solon.cloud.@@.config.enable";
    private String CONFIG_SERVER = "solon.cloud.@@.config.server";
    private String CONFIG_LOAD_GROUP = "solon.cloud.@@.config.loadGroup"; //（对某些框架来讲，可能没用处）
    private String CONFIG_LOAD_KEY = "solon.cloud.@@.config.loadKey";
    private String CONFIG_REFRESH_INTERVAL = "solon.cloud.@@.config.refreshInterval";

    //发现服务相关
    private String DISCOVERY_ENABLE = "solon.cloud.@@.discovery.enable";
    private String DISCOVERY_SERVER = "solon.cloud.@@.discovery.server";
    private String DISCOVERY_HOSTNAME = "solon.cloud.@@.discovery.hostname";
    private String DISCOVERY_TAGS = "solon.cloud.@@.discovery.tags";
    private String DISCOVERY_HEALTH_CHECK_PATH = "solon.cloud.@@.discovery.healthCheckPath";
    private String DISCOVERY_HEALTH_CHECK_INTERVAL = "solon.cloud.@@.discovery.healthCheckInterval";
    private String DISCOVERY_HEALTH_DETECTOR = "solon.cloud.@@.discovery.healthDetector";
    private String DISCOVERY_REFRESH_INTERVAL = "solon.cloud.@@.discovery.refreshInterval";

    //事件服务相关
    private String EVENT_ENABLE = "solon.cloud.@@.event.enable";
    private String EVENT_SERVER = "solon.cloud.@@.event.server";
    private String EVENT_HOSTNAME = "solon.cloud.@@.event.hostname";
    private String EVENT_ALARM = "solon.cloud.@@.event.alarm";
    private String EVENT_SEAL = "solon.cloud.@@.event.seal";

    //日志服务相关
    private String LOG_ENABLE = "solon.cloud.@@.log.enable";
    private String LOG_SERVER = "solon.cloud.@@.log.server";
    private String LOG_LOGGER = "solon.cloud.@@.log.logger";

    public CloudProps(String frame) {
        SERVER = SERVER.replace("@@", frame);
        TOKEN = TOKEN.replace("@@", frame);
        USERNAME = USERNAME.replace("@@", frame);
        PASSWORD = PASSWORD.replace("@@", frame);

        CONFIG_ENABLE = CONFIG_ENABLE.replace("@@", frame);
        CONFIG_SERVER = CONFIG_SERVER.replace("@@", frame);
        CONFIG_LOAD_GROUP = CONFIG_LOAD_GROUP.replace("@@", frame);
        CONFIG_LOAD_KEY = CONFIG_LOAD_KEY.replace("@@", frame);
        CONFIG_REFRESH_INTERVAL = CONFIG_REFRESH_INTERVAL.replace("@@", frame);

        DISCOVERY_ENABLE = DISCOVERY_ENABLE.replace("@@", frame);
        DISCOVERY_SERVER = DISCOVERY_SERVER.replace("@@", frame);
        DISCOVERY_HOSTNAME = DISCOVERY_HOSTNAME.replace("@@", frame);
        DISCOVERY_TAGS = DISCOVERY_TAGS.replace("@@", frame);
        DISCOVERY_HEALTH_CHECK_PATH = DISCOVERY_HEALTH_CHECK_PATH.replace("@@", frame);
        DISCOVERY_HEALTH_CHECK_INTERVAL = DISCOVERY_HEALTH_CHECK_INTERVAL.replace("@@", frame);
        DISCOVERY_HEALTH_DETECTOR = DISCOVERY_HEALTH_DETECTOR.replace("@@", frame);
        DISCOVERY_REFRESH_INTERVAL = DISCOVERY_REFRESH_INTERVAL.replace("@@", frame);

        EVENT_ENABLE = EVENT_ENABLE.replace("@@", frame);
        EVENT_SERVER = EVENT_SERVER.replace("@@", frame);
        EVENT_HOSTNAME = EVENT_HOSTNAME.replace("@@", frame);
        EVENT_ALARM = EVENT_ALARM.replace("@@", frame);
        EVENT_SEAL = EVENT_SEAL.replace("@@", frame);

        LOG_ENABLE = LOG_ENABLE.replace("@@", frame);
        LOG_SERVER = LOG_SERVER.replace("@@", frame);
        LOG_LOGGER = LOG_LOGGER.replace("@@", frame);
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

    public String getConfigServer() {
        String tmp = Solon.cfg().get(CONFIG_SERVER);
        if (Utils.isEmpty(tmp)) {
            return getServer();
        } else {
            return tmp;
        }
    }

    public String getConfigLoadGroup() {
        return Solon.cfg().get(CONFIG_LOAD_GROUP);
    }

    public String getConfigLoadKey() {
        return Solon.cfg().get(CONFIG_LOAD_KEY);
    }

    public String getConfigRefreshInterval(String def) {
        return Solon.cfg().get(CONFIG_REFRESH_INTERVAL, def);//def:10s
    }


    //
    //发现
    //
    public boolean getDiscoveryEnable() {
        return Solon.cfg().getBool(DISCOVERY_ENABLE, true);
    }

    public String getDiscoveryServer() {
        String tmp = Solon.cfg().get(DISCOVERY_SERVER);
        if (Utils.isEmpty(tmp)) {
            return getServer();
        } else {
            return tmp;
        }
    }

    public String getDiscoveryHostname() {
        return Solon.cfg().get(DISCOVERY_HOSTNAME);
    }

    public String getDiscoveryTags() {
        return Solon.cfg().get(DISCOVERY_TAGS);
    }

    public String getDiscoveryHealthCheckPath() {
        return Solon.cfg().get(DISCOVERY_HEALTH_CHECK_PATH, "/run/check/");
    }

    public String getDiscoveryHealthCheckInterval(String def) {
        return Solon.cfg().get(DISCOVERY_HEALTH_CHECK_INTERVAL, def); //def:5s
    }

    public String getDiscoveryHealthDetector() {
        return Solon.cfg().get(DISCOVERY_HEALTH_DETECTOR);
    }

    public String getDiscoveryRefreshInterval(String def) {
        return Solon.cfg().get(DISCOVERY_REFRESH_INTERVAL, def);//def:10s
    }

    //
    //事件服务相关
    //
    public boolean getEventEnable() {
        return Solon.cfg().getBool(EVENT_ENABLE, true);
    }

    public String getEventServer() {
        String tmp = Solon.cfg().get(EVENT_SERVER);
        if (Utils.isEmpty(tmp)) {
            return getServer();
        } else {
            return tmp;
        }
    }

    public String getEventHostname() {
        return Solon.cfg().get(EVENT_HOSTNAME);
    }

    public String getEventSeal() {
        return Solon.cfg().get(EVENT_SEAL);
    }


    //
    //日志服务相关
    //
    public boolean getLogEnable() {
        return Solon.cfg().getBool(LOG_ENABLE, true);
    }

    public String getLogServer() {
        String tmp = Solon.cfg().get(LOG_SERVER);
        if (Utils.isEmpty(tmp)) {
            return getServer();
        } else {
            return tmp;
        }
    }

    public String getLogLogger() {
        return Solon.cfg().get(LOG_LOGGER);
    }
}
