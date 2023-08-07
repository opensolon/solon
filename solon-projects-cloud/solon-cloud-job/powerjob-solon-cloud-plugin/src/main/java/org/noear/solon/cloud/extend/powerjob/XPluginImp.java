package org.noear.solon.cloud.extend.powerjob;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.powerjob.impl.PowerJobBeanBuilder;
import org.noear.solon.cloud.extend.powerjob.impl.PowerJobProperties;
import org.noear.solon.cloud.extend.powerjob.impl.PowerJobWorkerOfSolon;
import org.noear.solon.cloud.extend.powerjob.service.CloudJobServiceImpl;
import org.noear.solon.cloud.impl.CloudJobBeanBuilder;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.powerjob.client.PowerJobClient;
import tech.powerjob.worker.common.PowerJobWorkerConfig;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;

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
        CloudProps cloudProps = new CloudProps(context, "powerjob");

        if (cloudProps.getJobEnable() == false) {
            logger.warn("PowerJob is disabled, powerjob worker will not start.");
            return;
        }

        if (Utils.isBlank(cloudProps.getJobServer())) {
            logger.error("PowerJob server can't be empty! ");
            return;
        }

        if (Utils.isBlank(Solon.cfg().appName())) {
            logger.error("solon.app.name is empty, powerjob worker will not start.");
            return;
        }


        if (Utils.isNotBlank(cloudProps.getPassword())) {
            // Create PowerJobClient object
            PowerJobClient client = new PowerJobClient(cloudProps.getJobServer(), Solon.cfg().appName(), cloudProps.getPassword());
            context.beanInject(client);
            context.wrapAndPut(PowerJobClient.class, client); //包装并注册到容器（如果做为临时变量，会被回收的）
        }

        /*
         * Create PowerJobWorkerOfSolon object and inject it into Solon.
         */
        PowerJobProperties properties = cloudProps.getProp("job").getBean(PowerJobProperties.class);
        PowerJobWorkerConfig config = properties.toConfig(cloudProps);

        PowerJobWorkerOfSolon worker = new PowerJobWorkerOfSolon(context, config);
        context.beanInject(worker);
        context.wrapAndPut(PowerJobWorkerOfSolon.class, worker); //包装并注册到容器（如果做为临时变量，会被回收的）


        CloudManager.register(new CloudJobServiceImpl());

        //添加 @CloudJob 支持 BasicProcessor 原生类型
        CloudJobBeanBuilder.getInstance().addBuilder(BasicProcessor.class, new PowerJobBeanBuilder());
    }
}
