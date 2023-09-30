package org.noear.solon.test;

import org.junit.jupiter.api.extension.*;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.util.ClassUtil;

/**
 * @author noear
 * @since 1.10
 */
public class SolonJUnit5Extension implements TestInstanceFactory {
    private AppContext appContext;

    @Override
    public Object createTestInstance(TestInstanceFactoryContext factory, ExtensionContext extensionContext) throws TestInstantiationException {

        try {
            //init
            Class<?> klass = factory.getTestClass();
            if (appContext == null) {
                appContext = RunnerUtils.initRunner(klass);
            }

            //create
            Object tmp = ClassUtil.newInstance(klass);
            tmp = RunnerUtils.initTestTarget(appContext, tmp);

            return tmp;
        } catch (Throwable e) {
            throw new TestInstantiationException("Test class instantiation failed: " + factory.getTestClass().getName(), e);
        }
    }
}
