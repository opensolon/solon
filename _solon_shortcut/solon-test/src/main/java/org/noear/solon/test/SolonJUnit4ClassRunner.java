package org.noear.solon.test;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.noear.solon.Solon;
import org.noear.solon.aspect.BeanProxy;
import java.util.*;


public class SolonJUnit4ClassRunner extends BlockJUnit4ClassRunner {
    private static Set<Class<?>> appCached = new HashSet<>();
    public SolonJUnit4ClassRunner(Class<?> klass) throws InitializationError {
        super(klass);

        initDo(klass);
    }

    private void initDo(Class<?> klass){
        RunnerUtils.initRunner(klass);
    }

    @Override
    protected Object createTest() throws Exception {
        Object tmp = super.createTest();
        Solon.context().beanInject(tmp);

        tmp = BeanProxy.getGlobal().getProxy(Solon.context(),tmp);

        return tmp;
    }
}
