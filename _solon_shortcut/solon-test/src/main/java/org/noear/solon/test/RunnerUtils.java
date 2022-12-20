package org.noear.solon.test;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.aspect.BeanProxy;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.NvMap;
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
    private static Map<Class<?>, AopContext> appCached = new HashMap<>();

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

    public static void addPropertySource(AopContext context, TestPropertySource propertySource) {
        if (propertySource == null) {
            return;
        }

        for (String uri : propertySource.value()) {
            if (uri.startsWith(TAG_classpath)) {
                context.getProps().loadAdd(uri.substring(TAG_classpath.length()));
            } else {
                try {
                    context.getProps().loadAdd(new File(uri).toURI().toURL());
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static Object initTestTarget(AopContext aopContext, Object tmp) {
        //注入
        aopContext.beanInject(tmp);
        //构建临时包装（用于支持提取操作）
        BeanWrap beanWrap = new BeanWrap(aopContext, tmp.getClass(), tmp);
        //尝试提取操作
        aopContext.beanExtract(beanWrap);

        tmp = BeanProxy.getGlobal().getProxy(aopContext, tmp);

        return tmp;
    }


    public static AopContext initRunner(Class<?> klass) throws Throwable {
        SolonTest anno = klass.getAnnotation(SolonTest.class);

        if (anno != null) {
            if (anno.properties().length > 0) {
                for (String tmp : anno.properties()) {
                    String[] kv = tmp.split("=");
                    if (kv.length == 2) {
                        System.setProperty(kv[0], kv[1]);
                    }
                }
            }

            List<String> argsArg = new ArrayList<>();
            if (anno.args().length > 0) {
                argsArg.addAll(Arrays.asList(anno.args()));
            }

            //添加调试模式
            if (anno.debug()) {
                argsArg.add("-debug=1");
            }

            //添加环境变量
            if (Utils.isNotEmpty(anno.env())) {
                argsArg.add("-env=" + anno.env());
            }

            Class<?> mainClz = RunnerUtils.getMainClz(anno, klass);

            if (appCached.containsKey(mainClz)) {
                return appCached.get(mainClz);
            }

            AopContext aopContext = startDo(mainClz, argsArg, klass);

            appCached.put(mainClz, aopContext);
            //延迟秒数
            if (anno.delay() > 0) {
                try {
                    Thread.sleep(anno.delay() * 1000);
                } catch (Exception ex) {

                }
            }

            return aopContext;
        } else {
            return startDo(klass, Arrays.asList("-debug=1"), klass);
        }
    }

    private static AopContext startDo(Class<?> mainClz, List<String> argsArg, Class<?> klass) throws Throwable {


        if (mainClz == klass) {
            argsArg.add("isolated=1");
            String[] args = argsArg.toArray(new String[argsArg.size()]);

            SolonApp app = new SolonApp(mainClz, NvMap.from(args));
            Solon.startIsolatedApp(app, x -> {
                initDo(x, klass);
            });

            return app.context();

        } else {
            Method main = RunnerUtils.getMainMethod(mainClz);

            if (main != null && Modifier.isStatic(main.getModifiers())) {
                String[] args = argsArg.toArray(new String[argsArg.size()]);

                initDo(null, klass);
                main.invoke(null, new Object[]{args});

                return Solon.context();
            } else {
                argsArg.add("isolated=1");
                String[] args = argsArg.toArray(new String[argsArg.size()]);

                SolonApp app = new SolonApp(mainClz, NvMap.from(args));
                Solon.startIsolatedApp(app, x -> {
                    initDo(x, klass);
                });

                return app.context();
            }
        }
    }

    private static void initDo(SolonApp app, Class<?> klass) {
        TestPropertySource propAnno = klass.getAnnotation(TestPropertySource.class);

        if (app == null) {
            EventBus.subscribe(AppInitEndEvent.class, event -> {
                //加载测试配置
                RunnerUtils.addPropertySource(event.context(), propAnno);
                //event.context().wrapAndPut(klass);
                event.context().beanAroundAdd(TestRollback.class, new TestRollbackInterceptor(), 120);
            });
        } else {
            //加载测试配置
            RunnerUtils.addPropertySource(app.context(), propAnno);
            //app.context().wrapAndPut(klass);
            app.context().beanAroundAdd(TestRollback.class, new TestRollbackInterceptor(), 120);
        }
    }
}
