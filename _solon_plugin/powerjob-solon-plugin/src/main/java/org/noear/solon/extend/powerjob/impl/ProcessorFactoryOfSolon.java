package org.noear.solon.extend.powerjob.impl;

import com.google.common.collect.Sets;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.extend.powerjob.JobBeanManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.powerjob.common.enums.ProcessorType;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.extension.processor.ProcessorBean;
import tech.powerjob.worker.extension.processor.ProcessorDefinition;
import tech.powerjob.worker.extension.processor.ProcessorFactory;

import java.util.Set;

/**
 * ProcessorFactory 的 solon 实现，对接 ico 容器
 *
 * @author fzdwx
 * @since 2.0
 */
public class ProcessorFactoryOfSolon implements ProcessorFactory {

    private static final Logger logger = LoggerFactory.getLogger(ProcessorFactoryOfSolon.class);

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
            logger.warn("[ProcessorFactory] load by ProcessorFactoryOfSolon failed. If you are using Solon, make sure this bean was managed by Solon", e);
            return null;
        }
    }

    private BasicProcessor getBean(String name) throws Exception {
        BeanWrap beanWrap = JobBeanManager.getJob(name);

        if (beanWrap == null) {
            throw new IllegalStateException("[ProcessorFactory] Missing processor info： " + name);
        }

        return beanWrap.get();
    }
}
