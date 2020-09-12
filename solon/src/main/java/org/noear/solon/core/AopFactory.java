package org.noear.solon.core;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Aop 处理工厂（可以被继承重写）
 *
 * 主要实现三个动作：
 * 1.生成 bean
 * 2.构建 bean
 * 3.注入 字段或参数
 *
 * @author noear
 * @since 1.0
 * */
public class AopFactory extends AopFactoryBase {


    public AopFactory() {
        initialize();
    }

    /**
     * ::初始化（独立出 initialize，方便重写）
     */
    protected void initialize() {

        beanCreatorAdd(XConfiguration.class, (clz, bw, anno) -> {
            XInject typeInj = clz.getAnnotation(XInject.class);
            if (typeInj != null && XUtil.isNotEmpty(typeInj.value())) {
                if (typeInj.value().startsWith("${")) {
                    Aop.inject(bw.raw(), XApp.cfg().getPropByExpr(typeInj.value()));
                }
            }

            for (MethodWrap mWrap : ClassWrap.get(bw.clz()).methodWraps) {
                XBean m_an = mWrap.getMethod().getAnnotation(XBean.class);

                if (m_an != null) {
                    XInject beanInj = mWrap.getMethod().getAnnotation(XInject.class);

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
        });

        beanCreatorAdd(XBean.class, (clz, bw, anno) -> {
            bw.tagsSet(anno.tags());
            bw.metaSet(anno.attrs());

            if (XPlugin.class.isAssignableFrom(bw.clz())) {
                //如果是插件，则插入
                XApp.global().plug(bw.raw());
            } else {
                //设置remoting状态
                bw.remotingSet(anno.remoting());

                //注册到管理中心
                beanRegister(bw, anno.value(), anno.typed());

                //如果是remoting状态，转到XApp路由器
                if (bw.remoting()) {
                    BeanWebWrap bww = new BeanWebWrap(bw);
                    if (bww.mapping() != null) {
                        //
                        //如果没有xmapping，则不进行web注册
                        //
                        bww.load(XApp.global());
                    }
                }
            }
        });

        beanCreatorAdd(XController.class, (clz, bw, anno) -> {
            new BeanWebWrap(bw).load(XApp.global());
        });

        beanCreatorAdd(XInterceptor.class, (clz, bw, anno) -> {
            new BeanWebWrap(bw).main(false).load(XApp.global());
        });

        beanCreatorAdd(XEvent.class, (clz, bw, anno) -> {
            if (bw.raw() instanceof XEventListener) {
                XEventBus.subscribe(anno.value(), bw.raw());
            }
        });

        beanInjectorAdd(XInject.class, ((fwT, anno) -> {
            tryInjectByName(fwT, anno.value());
        }));

    }

    /**
     * ::加载 bean 及对应处理
     */
    public void beanLoad(Class<?> source, boolean loaded) {
        //确定文件夹名
        String dir = "";
        if (source.getPackage() != null) {
            dir = source.getPackage().getName().replace('.', '/');
        }

        //扫描类文件并处理（采用两段式加载，可以部分bean先处理；剩下的为第二段处理）
        XScaner.scan(dir, n -> n.endsWith(".class"))
                .stream().sorted(Comparator.comparing(s -> s.length())).forEach(name -> {
            String className = name.substring(0, name.length() - 6);

            Class<?> clz = XUtil.loadClass(className.replace("/", "."));
            if (clz != null) {
                tryCreateBean(clz);
            }
        });

        //尝试加载事件（不用函数包装，是为了减少代码）
        if (loaded) {
            loadedEvent.forEach(f -> f.run());
        }
    }

    /**
     * 注册到管理中心
     */
    public void beanRegister(BeanWrap bw, String name, boolean typed) {
        if (XUtil.isNotEmpty(name)) {
            //有name的，只用name注入
            //
            Aop.factory().putWrap(name, bw);
            if (typed == false) {
                //如果非typed，则直接返回
                return;
            }
        }

        Aop.factory().putWrap(bw.clz(), bw);
        Aop.factory().putWrap(bw.clz().getName(), bw);

        //如果有父级接口，则建立关系映射
        Class<?>[] list = bw.clz().getInterfaces();
        for (Class<?> c : list) {
            if (c.getName().contains("java.") == false) {
                //建立关系映射
                clzMapping.putIfAbsent(c, bw.clz());
                Aop.factory().putWrap(c, bw);
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
        Field[] fs = clzWrap.fields;
        for (Field f : fs) {
            Annotation[] annS = f.getDeclaredAnnotations();
            if (annS.length > 0) {
                VarHolder varH = clzWrap.getFieldWrap(f).holder(obj);
                tryInject(varH, annS);
            }
        }
    }
}
