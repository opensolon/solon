package org.noear.solon.test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.SolonTestApp;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Import;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.event.AppInitEndEvent;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.test.annotation.Rollback;
import org.noear.solon.test.annotation.TestPropertySource;
import org.noear.solon.test.annotation.TestRollback;
import org.noear.solon.test.aot.SolonAotTestProcessor;
import org.noear.solon.test.data.RollbackInterceptor;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author noear
 * @since 1.11
 */
public class RunnerUtils {
    private static Map<Class<?>, AppContext> appCached = new HashMap<>();

    private static Class<?> getMainClz(SolonTest anno, Class<?> klass) {
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

    private static Method getMainMethod(Class<?> mainClz) {
        try {
            return mainClz.getMethod("main", String[].class);
        } catch (Exception ex) {
            return null;
        }
    }

    private static void addPropertySource(AppContext context, List<String> propertySources) throws Throwable {
        if (propertySources == null) {
            return;
        }

        for (String uri : propertySources) {
            if (uri.startsWith(Utils.TAG_classpath)) {
                context.cfg().loadAdd(uri.substring(Utils.TAG_classpath.length()));
            } else {
                context.cfg().loadAdd(new File(uri).toURI().toURL());
            }
        }
    }

    /**
     * 初始化测试目标类
     */
    public static Object initTestTarget(AppContext appContext, Object tmp) {
        //注入
        appContext.beanInject(tmp);
        //构建临时包装（用于支持提取操作）
        BeanWrap beanWrap = new BeanWrap(appContext, tmp.getClass(), tmp);
        //尝试提取操作和代理
        appContext.beanExtractOrProxy(beanWrap);
        if (beanWrap.proxy() != null) {
            //如果有代理，把代理也注入下
            appContext.beanInject(beanWrap.raw());
        }
        //重新获取bean
        tmp = beanWrap.get();


        return tmp;
    }


    /**
     * 初始化测试运行器
     */
    public static AppContext initRunner(Class<?> klass) throws Throwable {
        //添加测试类包名检测（包名为必须要求）
        if (klass.getPackage() == null || Utils.isEmpty(klass.getPackage().getName())) {
            throw new IllegalStateException("The test class is missing package: " + klass.getName());
        }

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

            List<String> argsAry = new ArrayList<>();
            if (anno.args().length > 0) {
                argsAry.addAll(Arrays.asList(anno.args()));
            }

            //添加调试模式
            if (anno.debug()) {
                argsAry.add("-debug=1");
            }

            //添加环境变量
            if (Utils.isNotEmpty(anno.env())) {
                argsAry.add("-env=" + anno.env());
            }

            Class<?> mainClz = RunnerUtils.getMainClz(anno, klass);

            if (appCached.containsKey(mainClz)) {
                return appCached.get(mainClz);
            }

            try {
                if (anno.isAot()) {
                    System.setProperty(NativeDetector.AOT_PROCESSING, "true");
                }

                AppContext appContext = startDo(mainClz, argsAry, klass);

                if (anno.isAot()) {
                    new SolonAotTestProcessor(mainClz).process(appContext);
                }

                appCached.put(mainClz, appContext);
                //延迟秒数
                if (anno.delay() > 0) {
                    try {
                        Thread.sleep(anno.delay() * 1000);
                    } catch (Exception ex) {

                    }
                }

                return appContext;
            } finally {
                System.clearProperty(NativeDetector.AOT_PROCESSING);
            }
        } else {
            List<String> argsAry = new ArrayList<>();
            argsAry.add("-debug=1");
            return startDo(klass, argsAry, klass);
        }
    }

    private static AppContext startDo(Class<?> mainClz, List<String> argsAry, Class<?> klass) throws Throwable {

        if (mainClz == klass) {
            String[] args = argsAry.toArray(new String[argsAry.size()]);

            SolonTestApp testApp = new SolonTestApp(mainClz, NvMap.from(args));
            testApp.start(x -> {
                initDo(klass, x);
            });

            return testApp.context();

        } else {
            Method main = RunnerUtils.getMainMethod(mainClz);

            if (main != null && Modifier.isStatic(main.getModifiers())) {
                String[] args = argsAry.toArray(new String[argsAry.size()]);

                initDo(klass, null);
                main.invoke(null, new Object[]{args});

                return Solon.context();
            } else {
                String[] args = argsAry.toArray(new String[argsAry.size()]);

                SolonTestApp testApp = new SolonTestApp(mainClz, NvMap.from(args));
                testApp.start(x -> {
                    initDo(klass, x);
                });

                return testApp.context();
            }
        }
    }

    private static void initDo(Class<?> klass, SolonApp app) throws Throwable {
        List<String> propertySources = new ArrayList<>();
        TestPropertySource anno1 = klass.getAnnotation(TestPropertySource.class);
        if(anno1 != null){
            for(String s1 : anno1.value()) {
                propertySources.add(s1);
            }
        }

        Import anno2 = klass.getAnnotation(Import.class);
        if(anno2 != null){
            for(String s1 : anno2.profiles()) {
                propertySources.add(s1);
            }
        }


        if (app == null) {
            EventBus.subscribe(AppInitEndEvent.class, event -> {
                initContextDo(klass, event.context(), propertySources);
            });
        } else {
            initContextDo(klass, app.context(), propertySources);
        }
    }

    private static void initContextDo(Class<?> klass, AppContext context, List<String> propertySources) throws Throwable {
        //添加 TestPropertySource 注解支持 //加载测试配置
        RunnerUtils.addPropertySource(context, propertySources);

        //添加 TestRollback 注解支持
        context.beanInterceptorAdd(TestRollback.class, new RollbackInterceptor(), 120);
        context.beanInterceptorAdd(Rollback.class, new RollbackInterceptor(), 120);//v2.5

        //添加 Mock 注解支持
        context.beanInjectorAdd(Mock.class, (varH, anno) -> {
            Object val = Mockito.mock(varH.getType(), anno.answer());
            varH.setValue(val);
        });
    }
}
