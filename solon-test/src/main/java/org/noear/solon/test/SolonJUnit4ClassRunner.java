package org.noear.solon.test;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.Aop;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


public class SolonJUnit4ClassRunner extends BlockJUnit4ClassRunner {
    public SolonJUnit4ClassRunner(Class<?> klass) throws InitializationError {
        super(klass);

        SolonTest anno = klass.getAnnotation(SolonTest.class);
        String[] debugArgs = new String[]{"-debug=1"};


        if (anno != null) {
            try {
                Method main = anno.value().getMethod("main", String[].class);

                if (main != null && Modifier.isStatic(main.getModifiers())) {
                    if (anno.debug()) {
                        main.invoke(null, new Object[]{debugArgs});
                    } else {
                        main.invoke(null, new Object[]{});
                    }
                } else {
                    if (anno.debug()) {
                        XApp.start(anno.value(), debugArgs);
                    } else {
                        XApp.start(anno.value(), new String[]{});
                    }
                }
            } catch (Throwable ex) {
                XUtil.throwableUnwrap(ex).printStackTrace();
            }
        } else {
            XApp.start(klass, debugArgs);
        }
    }

    @Override
    protected Object createTest() throws Exception {
        Object tmp = super.createTest();
        Aop.inject(tmp);
        return tmp;
    }
}
