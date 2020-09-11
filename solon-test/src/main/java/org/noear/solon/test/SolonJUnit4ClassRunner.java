package org.noear.solon.test;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.noear.solon.XApp;
import org.noear.solon.core.Aop;

public class SolonJUnit4ClassRunner extends BlockJUnit4ClassRunner {
    public SolonJUnit4ClassRunner(Class<?> klass) throws InitializationError {
        super(klass);

        SolonTest anno = klass.getAnnotation(SolonTest.class);

        if (anno != null && anno.value() != null) {
            if (anno.debug()) {
                XApp.start(anno.value(), new String[]{"-debug=1"});
            } else {
                XApp.start(anno.value(), new String[]{});
            }
        } else {
            XApp.start(klass, new String[]{"-debug=1"});
        }
    }

    @Override
    protected Object createTest() throws Exception {
        Object tmp = super.createTest();
        Aop.inject(tmp);
        return tmp;
    }
}
