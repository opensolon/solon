package org.noear.solon.extend.powerjob.impl;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.powerjob.worker.PowerJobWorker;
import tech.powerjob.worker.common.PowerJobWorkerConfig;
import tech.powerjob.worker.extension.processor.ProcessorFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

/**
 * Solon Powerjob worker implementation.
 *
 * @see BuiltInSolonProcessorFactory
 * @see tech.powerjob.worker.PowerJobSpringWorker
 */
public class PowerjobSolonWorker extends PowerJobWorker {

    private static final Logger logger = LoggerFactory.getLogger(PowerjobSolonWorker.class);

    public PowerjobSolonWorker(PowerJobWorkerConfig config) {
        super(config);
        try {
            init();
        } catch (Exception e) {
            logger.error("Powerjob worker start failed.", e);
        }
    }

    @Override
    public void init() throws Exception {
        addProcessorFactory(new BuiltInSolonProcessorFactory());
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
