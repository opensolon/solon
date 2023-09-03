package org.noear.solon.test;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.noear.solon.core.AppContext;

public class SolonJUnit4ClassRunner extends BlockJUnit4ClassRunner {
    private AppContext appContext;

    public SolonJUnit4ClassRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected Object createTest() throws Exception {
        try {
            //init
            Class<?> klass = super.getTestClass().getJavaClass();
            if (appContext == null) {
                appContext = RunnerUtils.initRunner(klass);
            }

            //create
            Object tmp = super.createTest();
            tmp = RunnerUtils.initTestTarget(appContext, tmp);

            return tmp;
        } catch (Exception e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
