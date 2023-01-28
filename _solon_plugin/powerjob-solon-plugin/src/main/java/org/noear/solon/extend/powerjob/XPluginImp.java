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
import tech.powerjob.common.utils.NetUtils;
import tech.powerjob.worker.common.PowerJobWorkerConfig;

import java.util.Arrays;
import java.util.List;

/**
 * PowerJob plugin
 */
public class XPluginImp implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger(XPluginImp.class);

    @Override
    public void start(AopContext context) throws Throwable {
        PowerjobProperties worker = PowerjobProperties.get();

        if (!worker.isEnabled()) {
            logger.error("PowerJob is disabled, powerjob will not start.");
            return;
        }

        if (StringUtils.isBlank(worker.getAppName())) {
            logger.error("PowerJob app Name is empty, powerjob will not start.");
            return;
        }

        CommonUtils.requireNonNull(worker.getServerAddress(), "serverAddress can't be empty! " +
                "if you don't want to enable powerjob, please config program arguments: solon.powerjob.worker.enabled=false");

        PowerJobWorkerConfig config = worker.toConfig();

        if (StringUtils.isNotBlank(worker.getPassword())) {
            // Create PowerJobClient object
            Solon.context().beanInject(new PowerJobClient(config.getServerAddress(), config.getAppName(), worker.getPassword()));
        }

        /*
         * Create PowerjobSolonWorker object and inject it into Solon.
         */
        Solon.context().beanInject(new PowerjobSolonWorker(config));
    }
}
