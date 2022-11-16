package org.noear.solon.cloud.extend.xxljob;

import com.xxl.job.core.executor.XxlJobExecutor;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.cloud.utils.LocalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * auto config XxlJobExecutor
 *
 * @author noear
 * @since 1.4
 */
@Configuration
public class XxlJobAutoConfig {
    private static final Logger logger = LoggerFactory.getLogger(XxlJobAutoConfig.class);

    @Inject(value = "${xxl.job.admin.addresses}", required = false)
    private String adminAddresses;

    @Inject(value = "${xxl.job.accessToken}", required = false)
    private String accessToken;

    @Inject(value = "${xxl.job.executor.appname}", required = false)
    private String appname;

    @Inject(value = "${xxl.job.executor.address}", required = false)
    private String address;

    @Inject(value = "${xxl.job.executor.ip}", required = false)
    private String ip;

    @Inject(value = "${xxl.job.executor.port}", required = false)
    private int port;

    @Inject(value = "${xxl.job.executor.logpath}", required = false)
    private String logPath;

    @Inject(value = "${xxl.job.executor.logretentiondays}", required = false)
    private int logRetentionDays;

    @Bean
    public XxlJobExecutor xxlJobExecutor() {
        logger.info(">>>>>>>>>>> xxl-job config init.");

        if (Utils.isEmpty(adminAddresses)) {
            adminAddresses = XxlJobProps.instance.getJobServer();
        }

        if (Utils.isEmpty(appname)) {
            appname = Solon.cfg().appName();
        }

        if (Utils.isEmpty(ip)) {
            ip = LocalUtils.getLocalAddress();
        }

        if (port < 1000) {
            port = 9999;
        }

        if (logRetentionDays < 1) {
            logRetentionDays = 30;
        }

        if (Utils.isEmpty(logPath)) {
            logPath = "/data/applogs/xxl-job/jobhandler";
        }

        if (Utils.isEmpty(accessToken)) {
            accessToken = XxlJobProps.instance.getToken();
            if (Utils.isEmpty(accessToken)) {
                //兼容旧的
                accessToken = XxlJobProps.instance.getPassword();
            }
        }


        XxlJobExecutor executor = new XxlJobExecutor();

        executor.setAdminAddresses(adminAddresses);
        executor.setAppname(appname);
        executor.setAddress(address);
        executor.setIp(ip);
        executor.setPort(port);
        executor.setAccessToken(accessToken);
        executor.setLogPath(logPath);
        executor.setLogRetentionDays(logRetentionDays);

        return executor;
    }
}
