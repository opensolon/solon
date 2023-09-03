package org.noear.solon.extend.powerjob.impl;

import com.google.common.collect.Lists;
import org.noear.solon.core.AppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.powerjob.worker.PowerJobWorker;
import tech.powerjob.worker.common.PowerJobWorkerConfig;
import tech.powerjob.worker.extension.processor.ProcessorFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

/**
 * PowerJobWorker 的 solon 实现
 *
 * @see ProcessorFactoryOfSolon
 * @see tech.powerjob.worker.PowerJobSpringWorker
 *
 * @author fzdwx
 * @since 2.0
 */
public class PowerJobWorkerOfSolon extends PowerJobWorker {

    private static final Logger logger = LoggerFactory.getLogger(PowerJobWorkerOfSolon.class);

    final ProcessorFactoryOfSolon processorFactory;

    public PowerJobWorkerOfSolon(AppContext context, PowerJobWorkerConfig config) {
        super(config);

        processorFactory = new ProcessorFactoryOfSolon(context);

        try {
            init();
        } catch (Exception e) {
            logger.error("Powerjob worker start failed.", e);
        }
    }

    @Override
    public void init() throws Exception {
        addProcessorFactory(processorFactory);
        super.init();
    }

    public void addProcessorFactory(ProcessorFactory processorFactory) {
        PowerJobWorkerConfig workerConfig = workerRuntime.getWorkerConfig();
        ArrayList<ProcessorFactory> processorFactories =
                Lists.newArrayList(
                        Optional.ofNullable(workerConfig.getProcessorFactoryList())
                                .orElse(Collections.emptyList()));
        processorFactories.add(processorFactory);
        workerConfig.setProcessorFactoryList(processorFactories);
    }
}
