package org.noear.solon.test;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.noear.solon.core.AopContext;

public class SolonJUnit4ClassRunner extends BlockJUnit4ClassRunner {
    AopContext aopContext;
    public SolonJUnit4ClassRunner(Class<?> klass) throws InitializationError {
        super(klass);

        try {
            aopContext = RunnerUtils.initRunner(klass);
        } catch (Throwable e) {
            throw new InitializationError(e);
        }
    }

    @Override
    protected Object createTest() throws Exception {
        Object tmp = super.createTest();
        RunnerUtils.initTestTarget(aopContext, tmp);

        return tmp;
    }
}
