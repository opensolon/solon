package org.noear.solon.view.thymeleaf.tags;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author noear 2021/6/12 created
 */
public class AuthDialect extends AbstractProcessorDialect {
    private static AuthDialect _global;

    public static AuthDialect global() {
        if (_global == null) {
            _global = new AuthDialect();
        }

        return _global;
    }

    public AuthDialect() {
        super("AuthDialect", "auth", StandardDialect.PROCESSOR_PRECEDENCE);
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
