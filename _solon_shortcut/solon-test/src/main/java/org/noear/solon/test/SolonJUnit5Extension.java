package org.noear.solon.test;

import org.junit.Assert;
import org.junit.jupiter.api.extension.*;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.aspect.BeanProxy;
import org.noear.solon.core.event.AppInitEndEvent;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.test.annotation.TestPropertySource;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.*;

/**
 * @author noear
 * @since 1.10
 */
public class SolonJUnit5Extension implements TestInstanceFactory {
    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(new Object[]{SolonJUnit5Extension.class});
    private static final String TAG_classpath = "classpath:";
    private static Set<Class<?>> appCached = new HashSet<>();

    @Override
    public Object createTestInstance(TestInstanceFactoryContext factory, ExtensionContext extensionContext) throws TestInstantiationException {
        //init
        initDo(factory.getTestClass());

        //create
        Object tmp = null;

        try {
            tmp = getOnlyConstructor(factory.getTestClass()).newInstance();
        } catch (Exception e) {
            throw new TestInstantiationException("Test class instantiation failed: " + factory.getTestClass().getName());
        }

        Solon.context().beanInject(tmp);

        tmp = BeanProxy.getGlobal().getProxy(Solon.context(), tmp);

        return tmp;
    }

    private Class<?> klassCached;
    private void initDo(Class<?> klass){
        if(klassCached == null){
            klassCached = klass;
        }else{
            return;
        }

        SolonTest anno = klass.getAnnotation(SolonTest.class);
        TestPropertySource propAnno = klass.getAnnotation(TestPropertySource.class);

        if (anno != null) {
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
                    EventBus.subscribe(AppInitEndEvent.class, e->{
                        //加载测试配置
                        addPropertySource(propAnno);
                    });
                    main.invoke(null, new Object[]{argsStr});
                } else {
                    Solon.start(anno.value(), argsStr, app -> {
                        //加载测试配置
                        addPropertySource(propAnno);
                    });
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
            Solon.start(klass, new String[]{"-debug=1"}, app -> {
                //加载测试配置
                addPropertySource(propAnno);
            });
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

    private Constructor<?> getOnlyConstructor(Class clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();
        Assert.assertEquals(1L, (long) constructors.length);
        return constructors[0];
    }
}
