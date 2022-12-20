package org.noear.solon.test;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.aspect.BeanProxy;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.BeanWrap;
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

/**
 * @author noear
 * @since 1.11
 */
class RunnerUtils {
    public static final String TAG_classpath = "classpath:";
    private static Set<Class<?>> appCached = new HashSet<>();

    public static Class<?> getMainClz(SolonTest anno, Class<?> klass) {
        if (anno == null) {
            return klass;
        }

        Class<?> mainClz = anno.value();
        if (mainClz == Void.class) {
            mainClz = anno.classes();
        }

        if (mainClz == Void.class) {
            return klass;
        } else {
            return mainClz;
        }
    }

    public static Method getMainMethod(Class<?> mainClz) {
        try {
            return mainClz.getMethod("main", String[].class);
        } catch (Exception ex) {
            return null;
        }
    }

    public static void addPropertySource(TestPropertySource propertySource) {
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

    public static Object initTestTarget(Object tmp) {
        AopContext aopContext = Solon.context();

        //注入
        aopContext.beanInject(tmp);
        //构建临时包装（用于支持提取操作）
        BeanWrap beanWrap = new BeanWrap(aopContext, tmp.getClass(), tmp);
        //尝试提取操作
        aopContext.beanExtract(beanWrap);

        tmp = BeanProxy.getGlobal().getProxy(aopContext, tmp);

        return tmp;
    }


    public static void initRunner(Class<?> klass) throws Throwable {
        SolonTest anno = klass.getAnnotation(SolonTest.class);
        TestPropertySource propAnno = klass.getAnnotation(TestPropertySource.class);

        EventBus.subscribe(AppInitEndEvent.class, e -> {
            //加载测试配置
            RunnerUtils.addPropertySource(propAnno);
            Solon.context().wrapAndPut(klass);
            Solon.context().beanAroundAdd(TestRollback.class, new TestRollbackInterceptor(), 120);
        });

        if (anno != null) {
            if (anno.properties().length > 0) {
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

            //添加调试模式
            if (anno.debug()) {
                args.add("-debug=1");
            }

            //添加环境变量
            if (Utils.isNotEmpty(anno.env())) {
                args.add("-env=" + anno.env());
            }


            if (appCached.contains(anno.getClass())) {
                return;
            } else {
                appCached.add(anno.getClass());
            }

            String[] argsStr = args.toArray(new String[args.size()]);
            Class<?> mainClz = RunnerUtils.getMainClz(anno, klass);

            startDo(mainClz, argsStr, klass);

            //延迟秒数
            if (anno.delay() > 0) {
                try {
                    Thread.sleep(anno.delay() * 1000);
                } catch (Exception ex) {

                }
            }
        } else {
            startDo(klass, new String[]{"-debug=1"}, klass);
        }
    }

    private static void startDo(Class<?> mainClz, String[] args, Class<?> klass) throws Throwable {
        if (mainClz == klass) {
            Solon.start(mainClz, args);
        } else {
            Method main = RunnerUtils.getMainMethod(mainClz);

            if (main != null && Modifier.isStatic(main.getModifiers())) {

                main.invoke(null, new Object[]{args});
            } else {
                Solon.start(mainClz, args);
            }
        }
    }
}
