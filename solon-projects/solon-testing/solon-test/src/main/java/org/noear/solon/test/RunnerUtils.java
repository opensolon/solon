/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.noear.solon.*;
import org.noear.solon.annotation.Import;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.event.AppInitEndEvent;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.runtime.RuntimeDetector;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.test.annotation.Rollback;
import org.noear.solon.test.aot.SolonAotTestProcessor;
import org.noear.solon.test.data.RollbackInterceptor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;

/**
 * @author noear
 * @since 1.11
 */
public class RunnerUtils {
    private static Map<Class<?>, AppContext> contextCached = new HashMap<>();

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
            URL url = ResourceUtil.findResource(uri);
            context.cfg().loadAdd(url);
        }
    }

    /**
     * 初始化测试目标类
     */
    public static Object initTestTarget(AppContext appContext, Class<?> klass) {
        //create
        BeanWrap testWrap = appContext.getWrap(klass);
        if (testWrap == null) {
            testWrap = appContext.beanMake(klass);

            if (testWrap == null) {
                testWrap = appContext.wrapAndPut(klass);
            }

            if (testWrap != null) {
                appContext.beanExtractOrProxy(testWrap);
            }
        }

        if (testWrap.proxy() != null) {
            //如果有代理，把代理也注入下
            appContext.beanInject(testWrap.raw());
        }

        return testWrap.raw();
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

            argsAry.add("-testing=1");

            //添加调试模式
            if (anno.debug()) {
                argsAry.add("-debug=1");
            }

            //添加扫描模式
            if (anno.scanning() == false) {
                argsAry.add("-scanning=0");
            }

            //添加环境变量
            if (Utils.isNotEmpty(anno.env())) {
                argsAry.add("-env=" + anno.env());
            }

            Class<?> mainClz = RunnerUtils.getMainClz(anno, klass);

            if (contextCached.containsKey(klass)) {
                return contextCached.get(klass);
            }

            try {
                if (anno.isAot()) {
                    System.setProperty(RuntimeDetector.AOT_PROCESSING, "true");
                }

                AppContext appContext = startDo(mainClz, argsAry, klass, anno.enableHttp());

                if (anno.isAot()) {
                    new SolonAotTestProcessor(mainClz).process(appContext);
                }

                contextCached.put(klass, appContext);
                //延迟秒数
                if (anno.delay() > 0) {
                    try {
                        Thread.sleep(anno.delay() * 1000);
                    } catch (Exception ex) {

                    }
                }

                return appContext;
            } finally {
                System.clearProperty(RuntimeDetector.AOT_PROCESSING);
            }
        } else {
            List<String> argsAry = new ArrayList<>();
            argsAry.add("-testing=1");
            argsAry.add("-debug=1");
            return startDo(klass, argsAry, klass, false);
        }
    }

    private static AppContext startDo(Class<?> mainClz, List<String> argsAry, Class<?> klass, boolean enableHttp) throws Throwable {

        if (mainClz == klass) {
            String[] args = argsAry.toArray(new String[argsAry.size()]);

            SimpleSolonApp testApp = new SimpleSolonApp(mainClz, args);
            testApp.start(x -> {
                //默认关闭 http（避免与已经存在的服务端口冲突）
                x.enableHttp(enableHttp);
                initDo(klass, x);
            });

            return testApp.context();

        } else {
            Method main = RunnerUtils.getMainMethod(mainClz);
            String[] args = argsAry.toArray(new String[argsAry.size()]);

            if (main != null && Modifier.isStatic(main.getModifiers())) {
                initDo(klass, null);
                main.invoke(null, new Object[]{args});

                if (Solon.app() != null) {
                    return Solon.context();
                } else {
                    //如果不是 solon 应用，则启动一个 solon
                    SimpleSolonApp testApp = new SimpleSolonApp(mainClz, args);
                    testApp.start(x -> {
                        //默认关闭 http（避免与已经存在的服务端口冲突）
                        x.enableScanning(false);
                        x.enableHttp(enableHttp);
                        initDo(klass, x);
                    });

                    return testApp.context();
                }
            } else {
                SimpleSolonApp testApp = new SimpleSolonApp(mainClz, args);
                testApp.start(x -> {
                    //默认关闭 http（避免与已经存在的服务端口冲突）
                    x.enableHttp(enableHttp);
                    initDo(klass, x);
                });

                return testApp.context();
            }
        }
    }

    private static void initDo(Class<?> klass, SolonApp app) throws Throwable {
        List<String> propertySources = new ArrayList<>();

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
        context.beanInterceptorAdd(Rollback.class, new RollbackInterceptor(), 120);//v2.5

        //添加 Mock 注解支持
        context.beanInjectorAdd(Mock.class, (vh, anno) -> {
            Object val = Mockito.mock(vh.getType(), anno.answer());
            vh.setValue(val);
        });
    }
}
