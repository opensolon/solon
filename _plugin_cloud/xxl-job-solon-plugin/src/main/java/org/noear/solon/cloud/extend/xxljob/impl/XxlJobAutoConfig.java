package org.noear.solon.cloud.extend.xxljob.impl;

import com.xxl.job.core.executor.XxlJobExecutor;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
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

    @Inject("${xxl.job.admin.addresses}")
    private String adminAddresses;

    @Inject("${xxl.job.accessToken}")
    private String accessToken;

    @Inject("${xxl.job.executor.appname}")
    private String appname;

    @Inject("${xxl.job.executor.address}")
    private String address;

    @Inject("${xxl.job.executor.ip}")
    private String ip;

    @Inject("${xxl.job.executor.port}")
    private int port;

    @Inject("${xxl.job.executor.logpath}")
    private String logPath;

    @Inject("${xxl.job.executor.logretentiondays}")
    private int logRetentionDays;

    @Bean
    public XxlJobExecutor xxlJobExecutor() {
        logger.info(">>>>>>>>>>> xxl-job config init.");

        if(Utils.isEmpty(appname)){
            appname = Solon.cfg().appName();
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
