package org.noear.solon.test;

import org.junit.jupiter.api.extension.*;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.aspect.BeanProxy;
import java.util.*;

/**
 * @author noear
 * @since 1.10
 */
public class SolonJUnit5Extension implements TestInstanceFactory {
    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(new Object[]{SolonJUnit5Extension.class});
    private static Set<Class<?>> appCached = new HashSet<>();

    @Override
    public Object createTestInstance(TestInstanceFactoryContext factory, ExtensionContext extensionContext) throws TestInstantiationException {
        //init
        initDo(factory.getTestClass());

        //create
        Object tmp = null;

        try {
            tmp = Utils.newInstance(factory.getTestClass());
        } catch (Exception e) {
            throw new TestInstantiationException("Test class instantiation failed: " + factory.getTestClass().getName());
        }

        Solon.context().beanInject(tmp);

        tmp = BeanProxy.getGlobal().getProxy(Solon.context(), tmp);

        return tmp;
    }

    private Class<?> klassCached;

    private void initDo(Class<?> klass) {
        if (klassCached == null) {
            klassCached = klass;
        } else {
            return;
        }

        RunnerUtils.initRunner(klass);
    }
}
