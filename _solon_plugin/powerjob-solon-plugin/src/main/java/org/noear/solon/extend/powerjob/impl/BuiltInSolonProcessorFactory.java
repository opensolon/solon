package org.noear.solon.extend.powerjob.impl;

import com.google.common.collect.Sets;
import org.noear.solon.Solon;
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
 * 内建 Solon 处理器工厂. 根据 processorDefinition 加载 Ioc 容器中的 Bean
 */
public class BuiltInSolonProcessorFactory implements ProcessorFactory {

    private static final Logger logger = LoggerFactory.getLogger(BuiltInSolonProcessorFactory.class);

    @Override
    public Set<String> supportTypes() {
        return Sets.newHashSet(ProcessorType.BUILT_IN.name());
    }

    @Override
    public ProcessorBean build(ProcessorDefinition processorDefinition) {
        try {
            BasicProcessor bean = getBean(processorDefinition.getProcessorInfo());
            return new ProcessorBean().setProcessor(bean).setClassLoader(bean.getClass().getClassLoader());
        } catch (Exception e) {
            logger.warn("[ProcessorFactory] load by BuiltInSolonProcessorFactory failed. If you are using Solon, make sure this bean was managed by Solon", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getBean(String className) throws Exception {
        // by type
        ClassLoader classLoader = Solon.context().getClassLoader();
        AopContext ctx = Solon.context();
        if (classLoader != null) {
            return (T) ctx.getBean(classLoader.loadClass(className));
        }

        // by name
        String[] split = className.split("\\.");
        String beanName = split[split.length - 1];
        // 小写转大写
        char[] cs = beanName.toCharArray();
        cs[0] += 32;
        String beanName0 = String.valueOf(cs);
        logger.warn("[BuiltInSolonProcessorFactory] can't get ClassLoader from context[{}], try to load by beanName:{}", ctx, beanName0);
        return (T) ctx.getBean(beanName0);
    }
}
