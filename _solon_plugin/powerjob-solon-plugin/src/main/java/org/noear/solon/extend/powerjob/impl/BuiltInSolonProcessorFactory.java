package org.noear.solon.extend.powerjob.impl;

import com.google.common.collect.Sets;
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
 *
 * @author fzdwx
 * @since 2.0
 */
public class BuiltInSolonProcessorFactory implements ProcessorFactory {

    private static final Logger logger = LoggerFactory.getLogger(BuiltInSolonProcessorFactory.class);

    private final AopContext context;

    public BuiltInSolonProcessorFactory(AopContext context) {
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
            return new ProcessorBean().setProcessor(bean).setClassLoader(bean.getClass().getClassLoader());
        } catch (Exception e) {
            logger.warn("[ProcessorFactory] load by BuiltInSolonProcessorFactory failed. If you are using Solon, make sure this bean was managed by Solon", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getBean(String className) throws Exception {
        if (className.contains(".")) {
            // by type
            ClassLoader classLoader = context.getClassLoader(); //todo::classLoader 不会为 null
            Class<?> clz = classLoader.loadClass(className);

            return (T) context.getBeanOrNew(clz); //使用 getBeanOrNew，当没有时会自动创建 bean
        } else {
            // by name  //todo::这段代码，可能是无用的
            String[] split = className.split("\\.");
            String beanName = split[split.length - 1];
            // 小写转大写
            char[] cs = beanName.toCharArray();
            cs[0] += 32;
            String beanName0 = String.valueOf(cs);

            return (T) context.getBean(beanName0);
        }
    }
}
