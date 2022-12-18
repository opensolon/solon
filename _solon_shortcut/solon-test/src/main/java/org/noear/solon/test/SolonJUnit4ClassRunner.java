package org.noear.solon.test;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.aspect.BeanProxy;
import org.noear.solon.core.event.AppInitEndEvent;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.test.annotation.TestPropertySource;
import org.noear.solon.test.annotation.TestRollback;
import org.noear.solon.test.data.TestRollbackInterceptor;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.*;


public class SolonJUnit4ClassRunner extends BlockJUnit4ClassRunner {
    private static final String TAG_classpath = "classpath:";
    private static Set<Class<?>> appCached = new HashSet<>();
    public SolonJUnit4ClassRunner(Class<?> klass) throws InitializationError {
        super(klass);

        initDo(klass);
    }

    private void initDo(Class<?> klass){
        SolonTest anno = klass.getAnnotation(SolonTest.class);
        TestPropertySource propAnno = klass.getAnnotation(TestPropertySource.class);

        EventBus.subscribe(AppInitEndEvent.class, e->{
            //加载测试配置
            addPropertySource(propAnno);
            Solon.context().beanAroundAdd(TestRollback.class, new TestRollbackInterceptor(), 120);
        });

        if (anno != null) {
            if(anno.properties().length > 0) {
                for (String tmp : anno.properties()) {
                    String[] kv = tmp.split("=");
                    if (kv.length == 2) {
                        System.setProperty(kv[0], kv[1]);
                    }
                }
            }


            List<String> args = new ArrayList<>();
            if (anno.args().length > 0) {
                args.addAll(Arrays.asList(anno.args()));
            }

            if (anno.debug()) {
                args.add("-debug=1");
            }

            String[] argsStr = args.toArray(new String[args.size()]);

            if (appCached.contains(anno.getClass())) {
                return;
            } else {
                appCached.add(anno.getClass());
            }

            try {
                Method main = getMain(anno);

                if (main != null && Modifier.isStatic(main.getModifiers())) {

                    main.invoke(null, new Object[]{argsStr});
                } else {
                    Solon.start(anno.value(), argsStr);
                }
            } catch (Throwable ex) {
                Utils.throwableUnwrap(ex).printStackTrace();
            }


            //延迟秒数
            if (anno.delay() > 0) {
                try {
                    Thread.sleep(anno.delay() * 1000);
                } catch (Exception ex) {

                }
            }
        } else {
            Solon.start(klass, new String[]{"-debug=1"});
        }
    }

    private void addPropertySource(TestPropertySource propertySource) {
        if (propertySource == null) {
            return;
        }

        for (String uri : propertySource.value()) {
            if (uri.startsWith(TAG_classpath)) {
                Solon.cfg().loadAdd(uri.substring(TAG_classpath.length()));
            } else {
                try {
                    Solon.cfg().loadAdd(new File(uri).toURI().toURL());
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private Method getMain(SolonTest anno) {
        try {
            return anno.value().getMethod("main", String[].class);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    protected Object createTest() throws Exception {
        Object tmp = super.createTest();
        Solon.context().beanInject(tmp);

        tmp = BeanProxy.getGlobal().getProxy(Solon.context(),tmp);

        return tmp;
    }
}
