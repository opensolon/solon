package org.noear.solon.test;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.noear.solon.core.AopContext;

public class SolonJUnit4ClassRunner extends BlockJUnit4ClassRunner {
    private Class<?> klassCached;
    private AopContext aopContext;

    public SolonJUnit4ClassRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected Object createTest() throws Exception {
        try {
            //init
            Class<?> klass = super.getTestClass().getJavaClass();
            if (klassCached == null) {
                klassCached = klass;
                aopContext = RunnerUtils.initRunner(klass);
            }

            //create
            Object tmp = super.createTest();
            RunnerUtils.initTestTarget(aopContext, tmp);

            return tmp;
        } catch (Exception e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
