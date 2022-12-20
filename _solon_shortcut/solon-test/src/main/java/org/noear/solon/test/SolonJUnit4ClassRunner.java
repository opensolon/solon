package org.noear.solon.test;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class SolonJUnit4ClassRunner extends BlockJUnit4ClassRunner {
    public SolonJUnit4ClassRunner(Class<?> klass) throws InitializationError {
        super(klass);

        try {
            initDo(klass);
        } catch (Throwable e) {
            throw new InitializationError(e);
        }
    }

    private void initDo(Class<?> klass) throws Throwable {
        RunnerUtils.initRunner(klass);
    }

    @Override
    protected Object createTest() throws Exception {
        Object tmp = super.createTest();
        RunnerUtils.initTestTarget(tmp);

        return tmp;
    }
}
