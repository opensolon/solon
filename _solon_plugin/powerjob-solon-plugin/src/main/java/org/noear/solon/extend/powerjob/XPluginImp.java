package org.noear.solon.extend.powerjob;

import org.apache.commons.lang3.StringUtils;
import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.powerjob.impl.PowerjobSolonWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.powerjob.client.PowerJobClient;
import tech.powerjob.common.utils.CommonUtils;
import tech.powerjob.worker.common.PowerJobWorkerConfig;

/**
 * PowerJob plugin
 *
 * @author fzdwx
 * @since 2.0
 */
public class XPluginImp implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger(XPluginImp.class);

    @Override
    public void start(AopContext context) throws Throwable {
        PowerjobProperties properties = context.cfg().getBean("solon.powerjob", PowerjobProperties.class);

        if (!properties.isEnabled()) {
            logger.warn("PowerJob is disabled, powerjob worker will not start.");
            return;
        }

        if (StringUtils.isBlank(properties.getAppName())) {
            //如果没有配置 appName，则使用 solon.app.name 配置
            properties.setAppName(Solon.cfg().appName());
        }

        if (StringUtils.isBlank(properties.getAppName())) {
            logger.error("PowerJob app Name is empty, powerjob worker will not start.");
            return;
        }

        CommonUtils.requireNonNull(properties.getServerAddress(), "serverAddress can't be empty! " +
                "if you don't want to enable powerjob, please config program arguments: solon.powerjob.worker.enabled=false");

        PowerJobWorkerConfig config = properties.toConfig();

        if (StringUtils.isNotBlank(properties.getPassword())) {
            // Create PowerJobClient object
            PowerJobClient client = new PowerJobClient(config.getServerAddress(), config.getAppName(), properties.getPassword());
            context.beanInject(client);
            context.wrapAndPut(PowerJobClient.class, client); //包装并注册到容器（如果做为临时变量，会被回收的）
        }

        /*
         * Create PowerjobSolonWorker object and inject it into Solon.
         */
        PowerjobSolonWorker worker = new PowerjobSolonWorker(context, config);
        context.beanInject(worker);
        context.wrapAndPut(PowerjobSolonWorker.class, worker); //包装并注册到容器（如果做为临时变量，会被回收的）
    }
}
