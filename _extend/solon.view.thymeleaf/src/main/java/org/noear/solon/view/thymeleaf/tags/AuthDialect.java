package org.noear.solon.view.thymeleaf.tags;

import org.noear.solon.auth.tags.Constants;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author noear
 * @since 1.5
 */
public class AuthDialect extends AbstractProcessorDialect {

    public AuthDialect() {
        super("AuthDialect", Constants.PREFIX, StandardDialect.PROCESSOR_PRECEDENCE);
    }

    Set<IProcessor> processorSet = new LinkedHashSet<>();

    public void addProcessor(IProcessor processor) {
        processorSet.add(processor);
    }

    @Override
    public Set<IProcessor> getProcessors(String s) {
        return processorSet;
    }
}
