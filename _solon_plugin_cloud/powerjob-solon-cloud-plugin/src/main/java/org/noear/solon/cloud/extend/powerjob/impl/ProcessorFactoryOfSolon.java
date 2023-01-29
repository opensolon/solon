package org.noear.solon.cloud.extend.powerjob.impl;

import com.google.common.collect.Sets;
import org.noear.solon.cloud.extend.powerjob.JobManager;
import org.noear.solon.core.AopContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.powerjob.common.enums.ProcessorType;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.extension.processor.ProcessorBean;
import tech.powerjob.worker.extension.processor.ProcessorDefinition;
import tech.powerjob.worker.extension.processor.ProcessorFactory;

import java.util.Set;

/**
 * ProcessorFactory 的 solon 实现，对接 ico 容器和任务管理器
 *
 * @author fzdwx
 * @since 2.0
 */
public class ProcessorFactoryOfSolon implements ProcessorFactory {

    private static final Logger log = LoggerFactory.getLogger(ProcessorFactoryOfSolon.class);

    private final AopContext context;

    public ProcessorFactoryOfSolon(AopContext context) {
        this.context = context;
    }

    @Override
    public Set<String> supportTypes() {
        return Sets.newHashSet(ProcessorType.BUILT_IN.name());
    }

    @Override
    public ProcessorBean build(ProcessorDefinition processorDefinition) {
        try {
            BasicProcessor bean = getBean(processorDefinition.getProcessorInfo());
            return new ProcessorBean()
                    .setProcessor(bean)
                    .setClassLoader(bean.getClass().getClassLoader());
        } catch (Exception e) {
            log.warn("[ProcessorFactory] load by BuiltInSolonProcessorFactory failed. If you are using Solon, make sure this bean was managed by Solon", e);
            return null;
        }
    }

    private BasicProcessor getBean(String className) throws Exception {
        BasicProcessor processorProxy = JobManager.getJob(className);

        if (processorProxy == null) {
            // by type
            ClassLoader classLoader = context.getClassLoader(); //todo::classLoader 不会为 null
            Class<?> clz = classLoader.loadClass(className);

            //使用 getBeanOrNew，当没有时会自动创建 bean
            return (BasicProcessor) context.getBeanOrNew(clz);
        } else {
            //by name；如果 JobManager 里有的，用 JobManager 里登记的
            return processorProxy;
        }
    }
}
