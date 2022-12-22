package org.noear.solon.cloud;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Props;

import java.util.Properties;

/**
 * 云服务属性模板
 *
 * @author noear
 * @since 1.2
 */
public class CloudProps {
    public static String LOG_DEFAULT_LOGGER;

    private String ROOT = "solon.cloud.@@.";

    private String SERVER = "solon.cloud.@@.server";
    private String TOKEN = "solon.cloud.@@.token";
    private String USERNAME = "solon.cloud.@@.username";
    private String PASSWORD = "solon.cloud.@@.password";
    private String ALARM = "solon.cloud.@@.alarm";
    private String ACCESS_KEY = "solon.cloud.@@.accessKey";
    private String SECRET_KEY = "solon.cloud.@@.secretKey";

    //配置服务相关
    private String CONFIG_ENABLE = "solon.cloud.@@.config.enable";
    private String CONFIG_SERVER = "solon.cloud.@@.config.server";
    private String CONFIG_LOAD = "solon.cloud.@@.config.load";
    private String CONFIG_REFRESH_INTERVAL = "solon.cloud.@@.config.refreshInterval";

    //发现服务相关
    private String DISCOVERY_ENABLE = "solon.cloud.@@.discovery.enable";
    private String DISCOVERY_SERVER = "solon.cloud.@@.discovery.server";
    private String DISCOVERY_TAGS = "solon.cloud.@@.discovery.tags";
    private String DISCOVERY_UNSTABLE = "solon.cloud.@@.discovery.unstable";
    private String DISCOVERY_HEALTH_CHECK_PATH = "solon.cloud.@@.discovery.healthCheckPath";
    private String DISCOVERY_HEALTH_CHECK_INTERVAL = "solon.cloud.@@.discovery.healthCheckInterval";
    private String DISCOVERY_HEALTH_DETECTOR = "solon.cloud.@@.discovery.healthDetector";
    private String DISCOVERY_REFRESH_INTERVAL = "solon.cloud.@@.discovery.refreshInterval";

    //事件总线服务相关
    private String EVENT_ENABLE = "solon.cloud.@@.event.enable";
    private String EVENT_SERVER = "solon.cloud.@@.event.server";
    private String EVENT_PREFETCH_COUNT = "solon.cloud.@@.event.prefetchCount";
    private String EVENT_PUBLISH_TIMEOUT = "solon.cloud.@@.event.publishTimeout";

    private String EVENT_CHANNEL = "solon.cloud.@@.event.channel"; //虚拟通道（在客户端，可以多通道路由）
    private String EVENT_BROKER = "solon.cloud.@@.event.broker"; //broker
    private String EVENT_GROUP = "solon.cloud.@@.event.group"; //虚拟分组 //默认分组配置（即给所有的发送和订阅加上分组）
    private String EVENT_CONSUMER = "solon.cloud.@@.event.consumer"; //配置组
    private String EVENT_PRODUCER = "solon.cloud.@@.event.producer"; //配置组
    private String EVENT_CLIENT = "solon.cloud.@@.event.client"; //配置组
    private String EVENT_ACCESS_KEY = "solon.cloud.@@.event.accessKey";
    private String EVENT_SECRET_KEY = "solon.cloud.@@.event.secretKey";


    //锁服务相关
    private String LOCK_ENABLE = "solon.cloud.@@.lock.enable";
    private String LOCK_SERVER = "solon.cloud.@@.lock.server";

    //日志总线服务相关
    private String LOG_ENABLE = "solon.cloud.@@.log.enable";
    private String LOG_SERVER = "solon.cloud.@@.log.server";
    private String LOG_DEFAULT = "solon.cloud.@@.log.default";

    //链路跟踪服务相关
    private String TRACE_ENABLE = "solon.cloud.@@.trace.enable";
    private String TRACE_EXCLUDE = "solon.cloud.@@.trace.exclude";



    //度量服务相关
    private String METRIC_ENABLE = "solon.cloud.@@.metric.enable";

    //文件服务相关
    private String FILE_ENABLE = "solon.cloud.@@.file.enable";
    private String FILE_BUCKET = "solon.cloud.@@.file.bucket";
    private String FILE_ENDPOINT = "solon.cloud.@@.file.endpoint";
    private String FILE_REGION_ID = "solon.cloud.@@.file.regionId";
    private String FILE_ACCESS_KEY = "solon.cloud.@@.file.accessKey";
    private String FILE_SECRET_KEY = "solon.cloud.@@.file.secretKey";

    //国际化服务相关
    private String I18N_ENABLE = "solon.cloud.@@.i18n.enable";

    //ID服务相关
    private String ID_ENABLE = "solon.cloud.@@.id.enable";
    private String ID_START = "solon.cloud.@@.id.start";

    //名单服务相关
    private String LIST_ENABLE = "solon.cloud.@@.list.enable";

    //任务服务相关
    private String JOB_ENABLE = "solon.cloud.@@.job.enable";
    private String JOB_SERVER = "solon.cloud.@@.job.server";

    private final String frame;
    private final AopContext aopContext;

    public CloudProps(String frame) {
        this(Solon.context(), frame);
    }
    public CloudProps(AopContext aopContext, String frame) {
        this.frame = frame;
        this.aopContext = aopContext;

        ROOT = ROOT.replace("@@", frame);

        SERVER = SERVER.replace("@@", frame);
        TOKEN = TOKEN.replace("@@", frame);
        USERNAME = USERNAME.replace("@@", frame);
        PASSWORD = PASSWORD.replace("@@", frame);
        ALARM = ALARM.replace("@@", frame);
        ACCESS_KEY = ACCESS_KEY.replace("@@", frame);
        SECRET_KEY = SECRET_KEY.replace("@@", frame);


        CONFIG_ENABLE = CONFIG_ENABLE.replace("@@", frame);
        CONFIG_SERVER = CONFIG_SERVER.replace("@@", frame);
        CONFIG_LOAD = CONFIG_LOAD.replace("@@", frame);
        CONFIG_REFRESH_INTERVAL = CONFIG_REFRESH_INTERVAL.replace("@@", frame);

        DISCOVERY_ENABLE = DISCOVERY_ENABLE.replace("@@", frame);
        DISCOVERY_SERVER = DISCOVERY_SERVER.replace("@@", frame);
        DISCOVERY_TAGS = DISCOVERY_TAGS.replace("@@", frame);
        DISCOVERY_UNSTABLE = DISCOVERY_UNSTABLE.replace("@@", frame);
        DISCOVERY_HEALTH_CHECK_PATH = DISCOVERY_HEALTH_CHECK_PATH.replace("@@", frame);
        DISCOVERY_HEALTH_CHECK_INTERVAL = DISCOVERY_HEALTH_CHECK_INTERVAL.replace("@@", frame);
        DISCOVERY_HEALTH_DETECTOR = DISCOVERY_HEALTH_DETECTOR.replace("@@", frame);
        DISCOVERY_REFRESH_INTERVAL = DISCOVERY_REFRESH_INTERVAL.replace("@@", frame);

        EVENT_ENABLE = EVENT_ENABLE.replace("@@", frame);
        EVENT_SERVER = EVENT_SERVER.replace("@@", frame);
        EVENT_PREFETCH_COUNT = EVENT_PREFETCH_COUNT.replace("@@", frame);
        EVENT_PUBLISH_TIMEOUT = EVENT_PUBLISH_TIMEOUT.replace("@@", frame);
        EVENT_CHANNEL = EVENT_CHANNEL.replace("@@", frame);
        EVENT_BROKER = EVENT_BROKER.replace("@@", frame);
        EVENT_GROUP = EVENT_GROUP.replace("@@", frame);
        EVENT_CONSUMER = EVENT_CONSUMER.replace("@@", frame);
        EVENT_PRODUCER = EVENT_PRODUCER.replace("@@", frame);
        EVENT_CLIENT = EVENT_CLIENT.replace("@@", frame);
        EVENT_ACCESS_KEY = EVENT_ACCESS_KEY.replace("@@", frame);
        EVENT_SECRET_KEY = EVENT_SECRET_KEY.replace("@@", frame);

        LOCK_ENABLE = LOCK_ENABLE.replace("@@", frame);

        LOG_ENABLE = LOG_ENABLE.replace("@@", frame);
        LOG_SERVER = LOG_SERVER.replace("@@", frame);
        LOG_DEFAULT = LOG_DEFAULT.replace("@@", frame);

        TRACE_ENABLE = TRACE_ENABLE.replace("@@", frame);
        TRACE_EXCLUDE = TRACE_EXCLUDE.replace("@@", frame);

        METRIC_ENABLE = METRIC_ENABLE.replace("@@", frame);

        FILE_ENABLE = FILE_ENABLE.replace("@@", frame);
        FILE_ENDPOINT = FILE_ENDPOINT.replace("@@", frame);
        FILE_REGION_ID = FILE_REGION_ID.replace("@@", frame);
        FILE_BUCKET = FILE_BUCKET.replace("@@", frame);
        FILE_ACCESS_KEY = FILE_ACCESS_KEY.replace("@@", frame);
        FILE_SECRET_KEY = FILE_SECRET_KEY.replace("@@", frame);

        I18N_ENABLE = I18N_ENABLE.replace("@@", frame);

        ID_ENABLE = ID_ENABLE.replace("@@", frame);
        ID_START = ID_START.replace("@@", frame);

        LIST_ENABLE = LIST_ENABLE.replace("@@", frame);

        JOB_ENABLE = JOB_ENABLE.replace("@@", frame);
        JOB_SERVER = JOB_SERVER.replace("@@", frame);
    }


    //
    //公共
    //
    public String getServer() {
        return aopContext.cfg().get(SERVER);
    }

    public String getToken() {
        return aopContext.cfg().get(TOKEN);
    }

    public String getUsername() {
        return aopContext.cfg().get(USERNAME);
    }

    public String getPassword() {
        return aopContext.cfg().get(PASSWORD);
    }

    public String getAlarm() {
        return aopContext.cfg().get(ALARM);
    }

    public String getAccessKey() {
        return aopContext.cfg().get(ACCESS_KEY);
    }

    public String getSecretKey() {
        return aopContext.cfg().get(SECRET_KEY);
    }

    //
    //配置
    //
    public boolean getConfigEnable() {
        return aopContext.cfg().getBool(CONFIG_ENABLE, true);
    }

    public String getConfigServer() {
        String tmp = aopContext.cfg().get(CONFIG_SERVER);
        if (Utils.isEmpty(tmp)) {
            return getServer();
        } else {
            return tmp;
        }
    }

    public String getConfigLoad() {
        return aopContext.cfg().get(CONFIG_LOAD);
    }

    public String getConfigRefreshInterval(String def) {
        return aopContext.cfg().get(CONFIG_REFRESH_INTERVAL, def);//def:10s
    }


    //
    //发现
    //
    public boolean getDiscoveryEnable() {
        return aopContext.cfg().getBool(DISCOVERY_ENABLE, true);
    }

    public String getDiscoveryServer() {
        String tmp = aopContext.cfg().get(DISCOVERY_SERVER);
        if (Utils.isEmpty(tmp)) {
            return getServer();
        } else {
            return tmp;
        }
    }


    public String getDiscoveryTags() {
        return aopContext.cfg().get(DISCOVERY_TAGS);
    }

    public boolean getDiscoveryUnstable() {
        return aopContext.cfg().getBool(DISCOVERY_UNSTABLE, false);
    }

    public String getDiscoveryHealthCheckInterval(String def) {
        return aopContext.cfg().get(DISCOVERY_HEALTH_CHECK_INTERVAL, def); //def:5s
    }

    public String getDiscoveryRefreshInterval(String def) {
        return aopContext.cfg().get(DISCOVERY_REFRESH_INTERVAL, def);//def:5s
    }

    //
    //事件总线服务相关
    //
    public boolean getEventEnable() {
        return aopContext.cfg().getBool(EVENT_ENABLE, true);
    }

    public String getEventServer() {
        String tmp = aopContext.cfg().get(EVENT_SERVER);
        if (Utils.isEmpty(tmp)) {
            return getServer();
        } else {
            return tmp;
        }
    }

    public int getEventPrefetchCount() {
        return aopContext.cfg().getInt(EVENT_PREFETCH_COUNT, 0);
    }

    public long getEventPublishTimeout() {
        return getEventPublishTimeout(3000L);
    }

    public long getEventPublishTimeout(long def) {
        return aopContext.cfg().getLong(EVENT_PUBLISH_TIMEOUT, def);
    }

    public String getEventChannel() {
        return aopContext.cfg().get(EVENT_CHANNEL, "");
    }

    public String getEventBroker() {
        return aopContext.cfg().get(EVENT_BROKER, "");
    }

    public String getEventGroup() {
        return aopContext.cfg().get(EVENT_GROUP, "");
    }

    public Properties getEventConsumerProps() {
        return aopContext.cfg().getProp(EVENT_CONSUMER);
    }

    public Properties getEventProducerProps() {
        return aopContext.cfg().getProp(EVENT_PRODUCER);
    }

    public Properties getEventClientProps() {
        return aopContext.cfg().getProp(EVENT_CLIENT);
    }

    public String getEventAccessKey() {
        String tmp = aopContext.cfg().get(EVENT_ACCESS_KEY);

        if (Utils.isEmpty(tmp)) {
            return getAccessKey();
        } else {
            return tmp;
        }
    }

    public String getEventSecretKey() {
        String tmp = aopContext.cfg().get(EVENT_SECRET_KEY);

        if (Utils.isEmpty(tmp)) {
            return getSecretKey();
        } else {
            return tmp;
        }
    }

    //
    //锁服务相关
    //
    public boolean getLockEnable() {
        return aopContext.cfg().getBool(LOCK_ENABLE, true);
    }

    public String getLockServer() {
        String tmp = aopContext.cfg().get(LOCK_SERVER);
        if (Utils.isEmpty(tmp)) {
            return getServer();
        } else {
            return tmp;
        }
    }


    //
    //日志总线服务相关
    //
    public boolean getLogEnable() {
        return aopContext.cfg().getBool(LOG_ENABLE, true);
    }

    public String getLogServer() {
        String tmp = aopContext.cfg().get(LOG_SERVER);
        if (Utils.isEmpty(tmp)) {
            return getServer();
        } else {
            return tmp;
        }
    }

    public String getLogDefault() {
        return aopContext.cfg().get(LOG_DEFAULT);
    }


    //
    //链路跟踪服务相关
    //
    public boolean getTraceEnable() {
        return aopContext.cfg().getBool(TRACE_ENABLE, true);
    }


    public String getTraceExclude() {
        return aopContext.cfg().get(TRACE_EXCLUDE);
    }

    //
    //度量服务相关
    //
    public boolean getMetricEnable() {
        return aopContext.cfg().getBool(METRIC_ENABLE, true);
    }


    //
    //文件服务相关
    //
    public boolean getFileEnable() {
        return aopContext.cfg().getBool(FILE_ENABLE, true);
    }

    public String getFileEndpoint() {
        return aopContext.cfg().get(FILE_ENDPOINT);
    }

    public String getFileRegionId() {
        return aopContext.cfg().get(FILE_REGION_ID);
    }

    public String getFileBucket() {
        return aopContext.cfg().get(FILE_BUCKET);
    }

    public String getFileAccessKey() {
        String tmp = aopContext.cfg().get(FILE_ACCESS_KEY);

        if (Utils.isEmpty(tmp)) {
            return getAccessKey();
        } else {
            return tmp;
        }
    }

    public String getFileSecretKey() {
        String tmp = aopContext.cfg().get(FILE_SECRET_KEY);

        if (Utils.isEmpty(tmp)) {
            return getSecretKey();
        } else {
            return tmp;
        }
    }

    //
    //国际化服务相关
    //
    public boolean getI18nEnable() {
        return aopContext.cfg().getBool(I18N_ENABLE, true);
    }

    //
    //ID服务相关
    //
    public boolean getIdEnable() {
        return aopContext.cfg().getBool(ID_ENABLE, true);
    }

    public long getIdStart() {
        return aopContext.cfg().getLong(ID_START, 0L);
    }

    //
    //LIST服务相关
    //
    public boolean getListEnable() {
        return aopContext.cfg().getBool(LIST_ENABLE, true);
    }

    //
    //JOB服务相关
    //
    public boolean getJobEnable() {
        return aopContext.cfg().getBool(JOB_ENABLE, true);
    }

    public String getJobServer() {
        String tmp = aopContext.cfg().get(JOB_SERVER);
        if (Utils.isEmpty(tmp)) {
            return getServer();
        } else {
            return tmp;
        }
    }

    /**
     * 获取值
     * */
    public String getValue(String name) {
        return aopContext.cfg().get(ROOT + name); //"solon.cloud.@@.";
    }

    public String getValue(String name, String def) {
        return aopContext.cfg().get(ROOT + name, def); //"solon.cloud.@@.";
    }



    /**
     * 设置值
     * */
    public void setValue(String name, String value) {
        aopContext.cfg().setProperty(ROOT + name, value); //"solon.cloud.@@.";
    }

    /**
     * 获取所有属性
     * */
    public Props getProp(){
        return aopContext.cfg().getProp(ROOT.substring(0,ROOT.length()-1));
    }

    /**
     * 获取所有某一块属性
     * */
    public Props getProp(String keyStarts) {
        return aopContext.cfg().getProp(ROOT + keyStarts);
    }
}
