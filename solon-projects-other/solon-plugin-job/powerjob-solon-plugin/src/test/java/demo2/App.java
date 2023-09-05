package demo2;

import org.apache.commons.lang3.StringUtils;
import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.extend.powerjob.impl.PowerJobBeanBuilder;
import org.noear.solon.extend.powerjob.impl.PowerJobProperties;
import org.noear.solon.extend.powerjob.impl.PowerJobWorkerOfSolon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.powerjob.client.PowerJobClient;
import tech.powerjob.common.utils.CommonUtils;
import tech.powerjob.solon.annotation.PowerJob;
import tech.powerjob.worker.common.PowerJobWorkerConfig;

public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args, app -> {
            //::::下面这个优先级不重要
            app.pluginAdd(1, new PowerjobPlugin());
        });
    }

    public static class PowerjobPlugin implements Plugin {
        private static final Logger logger = LoggerFactory.getLogger(PowerjobPlugin.class);

        //::::把旧的配置，前缀直接改掉（换个喜欢的）
        private static final String configStarts = "solon.powerjob2";

        @Override
        public void start(AppContext context) throws Throwable {
            PowerJobProperties properties = context.cfg().getBean(configStarts, PowerJobProperties.class);

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

            //Add anno support
            context.beanBuilderAdd(PowerJob.class, new PowerJobBeanBuilder());

            //::::调后启用服务（重点在这儿，比 solon.boot.undertow 启动要晚）
            context.onEvent(AppLoadEndEvent.class, e -> {
                startDo(context, properties);
            });
        }

        private void startDo(AppContext context, PowerJobProperties properties) throws Throwable {
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
             * Create PowerJobWorkerOfSolon object and inject it into Solon.
             */
            PowerJobWorkerOfSolon worker = new PowerJobWorkerOfSolon(context, config);
            context.beanInject(worker);
            context.wrapAndPut(PowerJobWorkerOfSolon.class, worker); //包装并注册到容器（如果做为临时变量，会被回收的）
        }
    }
}
