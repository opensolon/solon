package org.noear.solon.core;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.annotation.*;
import org.noear.solon.annotation.XServerEndpoint;
import org.noear.solon.event.BeanLoadedEvent;
import org.noear.solon.ext.BiConsumerEx;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

/**
 * Aop 处理工厂（可以被继承重写；一般由 Aop 提供 AopContext 的手动使用模式）
 *
 * 主要实现两个动作：
 * 1.bean 构建
 * 2.bean 注入（字段 或 参数）
 *
 * @author noear
 * @since 1.0
 * */
public class AopContext extends BeanContainer {


    public AopContext() {
        initialize();
    }

    /**
     * ::初始化（独立出 initialize，方便重写）
     */
    protected void initialize() {

        //注册 @XConfiguration 构建器
        beanBuilderAdd(XConfiguration.class, (clz, bw, anno) -> {
            XInject typeInj = clz.getAnnotation(XInject.class);
            if (typeInj != null && XUtil.isNotEmpty(typeInj.value())) {
                if (typeInj.value().startsWith("${")) {
                    XUtil.injectProperties(bw.raw(), XApp.cfg().getPropByExpr(typeInj.value()));
                }
            }

            for (Method m  : ClassWrap.get(bw.clz()).getMethods()) {
                XBean m_an = m.getAnnotation(XBean.class);

                if (m_an != null) {
                    MethodWrap mWrap = MethodWrap.get(m);
                    XInject beanInj = m.getAnnotation(XInject.class);

                    //有参数的bean，采用线程池处理；所以需要锁等待
                    //
                    tryBuildBean(m_an, mWrap, bw, beanInj, (p1) -> {
                        XInject tmp = p1.getAnnotation(XInject.class);
                        if (tmp == null) {
                            return null;
                        } else {
                            return tmp.value();
                        }
                    });
                }
            }

            //添加bean形态处理
            addBeanShape(clz, bw);

            //尝试导入
            beanImport(clz.getAnnotation(XImport.class));

            //注册到容器 //XConfiguration 不进入二次注册
            //beanRegister(bw,bw.name(),bw.typed());
        });

        //注册 @XBean 构建器
        beanBuilderAdd(XBean.class, (clz, bw, anno) -> {
            bw.nameSet(anno.value());
            bw.tagSet(anno.tag());
            bw.attrsSet(anno.attrs());
            bw.typedSet(anno.typed());

            //添加bean形态处理
            addBeanShape(clz, bw);

            //设置remoting状态
            bw.remotingSet(anno.remoting());

            //注册到容器
            beanRegister(bw, anno.value(), anno.typed());

            //如果是remoting状态，转到XApp路由器
            if (bw.remoting()) {
                XHandlerLoader bww = new XHandlerLoader(bw);
                if (bww.mapping() != null) {
                    //
                    //如果没有xmapping，则不进行web注册
                    //
                    bww.load(XApp.global());
                }
            }
        });

        //注册 @XController 构建器
        beanBuilderAdd(XController.class, (clz, bw, anno) -> {
            new XHandlerLoader(bw).load(XApp.global());
        });

        //注册 @XServerEndpoint 构建器
        beanBuilderAdd(XServerEndpoint.class, (clz, wrap, anno) -> {
            if (XListener.class.isAssignableFrom(clz)) {
                XListener l = wrap.raw();
                XApp.global().router().add(anno.value(), anno.method(), l);
            }
        });

        //注册 @XInject 构建器
        beanInjectorAdd(XInject.class, ((fwT, anno) -> {
            beanInject(fwT, anno.value());
        }));
    }

    /**
     * 添加bean的不同形态
     * */
    private void addBeanShape(Class<?> clz, BeanWrap bw) {
        //XPlugin
        if (XPlugin.class.isAssignableFrom(bw.clz())) {
            //如果是插件，则插入
            XApp.global().plug(bw.raw());
            return;
        }

        //XEventListener
        if (XEventListener.class.isAssignableFrom(clz)) {
            addEventListener(clz, bw);
            return;
        }

        //XUpstreamFactory
        if (XUpstream.Factory.class.isAssignableFrom(clz)) {
            XBridge.upstreamFactorySet(bw.raw());
        }
    }

    //添加事件监听
    private void addEventListener(Class<?> clz, BeanWrap bw) {
        for (Type t1 : clz.getGenericInterfaces()) {
            if (t1 instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) t1;
                if (pt.getRawType() == XEventListener.class) {
                    Class<?> et = (Class<?>) pt.getActualTypeArguments()[0];
                    XEventBus.subscribe(et, bw.raw());
                    return;
                }
            }
        }
    }

    //::注入

    /**
     * 为一个对象注入（可以重写）
     */
    public void beanInject(Object obj) {
        if (obj == null) {
            return;
        }

        ClassWrap clzWrap = ClassWrap.get(obj.getClass());

        //支持父类注入
        for (Map.Entry<String, FieldWrap> kv : clzWrap.getFieldAllWraps().entrySet()) {
            Annotation[] annS = kv.getValue().annoS;
            if (annS.length > 0) {
                VarHolder varH = kv.getValue().holder(obj);
                tryInject(varH, annS);
            }
        }
    }

    ////////////

    /**
     * 根据配置导入bean
     * */
    public void beanImport(XImport anno) {
        if (anno != null) {
            for (Class<?> clz : anno.value()) {
                beanMake(clz);
            }

            for (String pkg : anno.scanPackages()) {
                beanScan(pkg);
            }
        }
    }

    /**
     * ::扫描源下的所有 bean 及对应处理
     */
    public void beanScan(Class<?> source) {
        //确定文件夹名
        if (source.getPackage() != null) {
            beanScan(source.getPackage().getName());
        }
    }

    /**
     * ::扫描源下的所有 bean 及对应处理
     */
    public void beanScan(String basePackage) {
        if (XUtil.isEmpty(basePackage)) {
            return;
        }

        String dir = basePackage.replace('.', '/');

        //扫描类文件并处理（采用两段式加载，可以部分bean先处理；剩下的为第二段处理）
        XScaner.scan(dir, n -> n.endsWith(".class"))
                .stream().sorted(Comparator.comparing(s -> s.length())).forEach(name -> {
            String className = name.substring(0, name.length() - 6);

            Class<?> clz = XUtil.loadClass(className.replace("/", "."));
            if (clz != null) {
                tryCreateBean(clz);
            }
        });
    }

    /**
     * ::制造当前 bean 及对应处理
     */
    public BeanWrap beanMake(Class<?> clz) {
        //包装
        BeanWrap bw = wrap(clz, null);

        tryCreateBean(bw);

        //尝试入库
        putWrap(clz, bw);

        return bw;
    }


    ////////////////////////////////////////////////////
    //
    //

    /**
     * 尝试为bean注入
     */
    protected void tryInject(VarHolder varH, Annotation[] annS) {
        for (Annotation a : annS) {
            BeanInjector bi = beanInjectors.get(a.annotationType());
            if (bi != null) {
                bi.doInject(varH, a);
            }
        }
    }


    /**
     * 尝试生成 bean
     */
    protected void tryCreateBean(Class<?> clz) {
        tryCreateBean0(clz, (c, a) -> {
            //包装
            BeanWrap bw = this.wrap(clz, null);
            c.doBuild(clz, bw, a);
            //尝试入库
            this.putWrap(clz, bw);
        });
    }

    protected void tryCreateBean(BeanWrap bw) {
        tryCreateBean0(bw.clz(), (c, a) -> {
            c.doBuild(bw.clz(), bw, a);
        });
    }

    protected void tryCreateBean0(Class<?> clz, BiConsumerEx<BeanBuilder, Annotation> consumer) {
        Annotation[] annS = clz.getDeclaredAnnotations();

        if (annS.length > 0) {
            try {
                for (Annotation a : annS) {
                    BeanBuilder creator = beanBuilders.get(a.annotationType());
                    if (creator != null) {
                        consumer.accept(creator, a);
                    }
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 尝试构建 bean
     *
     * @param anno      bean 注解
     * @param mWrap     方法包装器
     * @param bw        bean 包装器
     * @param beanInj   类注入
     * @param injectVal 参数注入
     */
    protected void tryBuildBean(XBean anno, MethodWrap mWrap, BeanWrap bw, XInject beanInj, Function<Parameter, String> injectVal) throws Exception {
        int size2 = mWrap.getParameters().length;

        if (size2 == 0) {
            //0.没有参数
            Object raw = mWrap.doInvoke(bw.raw(), new Object[]{});
            tryBuildBean0(anno, beanInj, mWrap.getReturnType(), raw);
        } else {
            //1.构建参数
            VarGather gather = new VarGather(size2, (args2) -> {
                try {
                    Object raw = mWrap.doInvoke(bw.raw(), args2);
                    tryBuildBean0(anno, beanInj, mWrap.getReturnType(), raw);
                } catch (Throwable ex) {
                    XEventBus.push(ex);
                }
            });

            for (Parameter p1 : mWrap.getParameters()) {
                VarHolder p2 = gather.add(p1);
                beanInject(p2, injectVal.apply(p1));
            }
        }
    }

    protected void tryBuildBean0(XBean anno, XInject beanInj, Class<?> clz, Object raw) {
        if (raw != null) {
            BeanWrap m_bw = null;
            if (raw instanceof BeanWrap) {
                m_bw = (BeanWrap) raw;
            } else {
                if (beanInj != null && XUtil.isEmpty(beanInj.value()) == false) {
                    if (beanInj.value().startsWith("${")) {
                        XUtil.injectProperties(raw, XApp.cfg().getPropByExpr(beanInj.value()));
                    }
                }

                //动态构建的bean, 可通过广播进行扩展
                XEventBus.push(raw);

                //动态构建的bean，都用新生成wrap（否则会类型混乱）
                m_bw = new BeanWrap(clz, raw);
                m_bw.attrsSet(anno.attrs());
            }

            m_bw.nameSet(anno.value());
            m_bw.tagSet(anno.tag());
            m_bw.typedSet(anno.typed());

            beanRegister(m_bw, anno.value(), anno.typed());

            //@XBean 动态产生的 beanWrap（含 name,tag,attrs），进行事件通知
            XEventBus.push(m_bw);
        }
    }

    /////////

    //加载完成标志
    private boolean loadDone;
    //加载事件
    private Set<Runnable> loadEvents = new LinkedHashSet<>();

    //::bean事件处理

    /**
     * 添加bean加载完成事件
     */
    @XNote("添加bean加载完成事件")
    public void beanOnloaded(Runnable fun) {
        loadEvents.add(fun);

        //如果已加载完成，则直接返回
        if (loadDone) {
            fun.run();
        }
    }

    /**
     * 完成加载时调用，会进行事件通知
     */
    public void beanLoaded() {
        loadDone = true;

        //1.广播事件
        XEventBus.push(BeanLoadedEvent.instance);

        //2.执行加载事件（不用函数包装，是为了减少代码）
        loadEvents.forEach(f -> f.run());
    }
}
