package org.noear.solon.test;

import org.junit.jupiter.api.extension.*;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.util.ClassUtil;

/**
 * @author noear
 * @since 1.10
 */
public class SolonJUnit5Extension implements TestInstanceFactory {
    private Class<?> klassCached;
    private AopContext aopContext;

    @Override
    public Object createTestInstance(TestInstanceFactoryContext factory, ExtensionContext extensionContext) throws TestInstantiationException {

        try {
            //init
            Class<?> klass = factory.getTestClass();
            if (klassCached == null) {
                klassCached = klass;
                aopContext = RunnerUtils.initRunner(klass);
            }

            //create
            Object tmp = ClassUtil.newInstance(factory.getTestClass());
            tmp = RunnerUtils.initTestTarget(aopContext, tmp);

            return tmp;
        } catch (Throwable e) {
            throw new TestInstantiationException("Test class instantiation failed: " + factory.getTestClass().getName());
        }
    }
}
