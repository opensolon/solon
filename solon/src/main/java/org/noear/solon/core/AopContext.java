package org.noear.solon.core;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.annotation.*;
import org.noear.solon.annotation.XServerEndpoint;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.Map;

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
public class AopContext extends AopContainer {


    public AopContext() {
        initialize();
    }

    /**
     * ::初始化（独立出 initialize，方便重写）
     */
    protected void initialize() {

        Aop.context().beanCreatorAdd(XConfiguration.class, (clz, bw, anno) -> {
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

                    Aop.context().tryBuildBean(m_an, mWrap, bw, beanInj, (p1) -> {
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
        });

        Aop.context().beanCreatorAdd(XBean.class, (clz, bw, anno) -> {
            bw.nameSet(anno.value());
            bw.tagSet(anno.tag());
            bw.attrsSet(anno.attrs());
            bw.typedSet(anno.typed());

            //添加bean形态处理
            addBeanShape(clz, bw);

            //设置remoting状态
            bw.remotingSet(anno.remoting());

            //注册到管理中心
            Aop.context().beanRegister(bw, anno.value(), anno.typed());

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
        });

        Aop.context().beanCreatorAdd(XController.class, (clz, bw, anno) -> {
            new BeanWebWrap(bw).load(XApp.global());
        });

        Aop.context().beanCreatorAdd(XInterceptor.class, (clz, bw, anno) -> {
            new BeanWebWrap(bw).main(false).load(XApp.global());
        });

        Aop.context().beanCreatorAdd(XServerEndpoint.class, (clz, wrap, anno) -> {
            if (XListener.class.isAssignableFrom(clz)) {
                XListener l = wrap.raw();
                XApp.global().router().add(anno.value(), anno.method(), l);
            }
        });


        Aop.context().beanInjectorAdd(XInject.class, ((fwT, anno) -> {
            Aop.context().tryInjectByName(fwT, anno.value());
        }));
    }

    private void addBeanShape(Class<?> clz, BeanWrap bw){
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
        if(XUpstreamFactory.class.isAssignableFrom(clz)){
            XBridge.upstreamFactorySet(bw.raw());
        }
    }

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
        for (Map.Entry<String, FieldWrap> kv : clzWrap.fieldAll().entrySet()) {
            Annotation[] annS = kv.getValue().field.getDeclaredAnnotations();
            if (annS.length > 0) {
                VarHolder varH = kv.getValue().holder(obj);
                tryInject(varH, annS);
            }
        }
    }

    ////////////

    /**
     * ::扫描源下的所有 bean 及对应处理
     */
    public void beanScan(Class<?> source) {
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
    }

    /**
     * ::制造当前 bean 及对应处理
     * */
    public BeanWrap beanMake(Class<?> clz) {
        //包装
        BeanWrap bw = wrap(clz, null);

        tryCreateBean(bw);

        //尝试入库
        putWrap(clz, bw);

        return bw;
    }

    /**
     * 完成加载时调用，会进行事件通知
     * */
    public void beanLoaded(){
        //尝试加载事件（不用函数包装，是为了减少代码）
        loadedEvent.forEach(f -> f.run());
    }
}
