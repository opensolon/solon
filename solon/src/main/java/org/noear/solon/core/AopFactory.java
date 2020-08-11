package org.noear.solon.core;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.annotation.*;
import org.noear.solon.core.utils.TypeUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

/**
 * Aop 处理工厂（可以被继承重写）
 * */
public class AopFactory extends AopFactoryBase {


    public AopFactory() {
        initialize();
    }

    //::初始化

    /**
     * 初始化（独立出 initialize，方便重写）
     */
    protected void initialize() {

        beanCreatorAdd(XConfiguration.class, (clz, bw, anno) -> {
            for (MethodWrap mWrap : ClassWrap.get(bw.clz()).methodWraps) {
                XBean m_an = mWrap.getMethod().getAnnotation(XBean.class);

                if (m_an != null) {
                    XInject beanInj = mWrap.getMethod().getAnnotation(XInject.class);

                    beanBuild(m_an.value(), mWrap, bw, beanInj, (p1) -> {
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
            beanAnnoHandle(bw, anno);
        });

        beanCreatorAdd(XController.class, (clz, bw, anno) -> {
            new BeanWebWrap(bw).load(XApp.global());
        });

        beanCreatorAdd(XInterceptor.class, (clz, bw, anno) -> {
            BeanWebWrap bww = new BeanWebWrap(bw);
            bww.endpointSet(anno.after() ? XEndpoint.after : XEndpoint.before);
            bww.load(XApp.global());
        });

        beanInjectorAdd(XInject.class, ((fwT, anno) -> {
            beanInject(fwT, anno.value());
        }));
    }

    /**
     * XBean 的处理
     */
    protected void beanAnnoHandle(BeanWrap bw, XBean anno) {
        bw.tagSet(anno.tag());

        if (XPlugin.class.isAssignableFrom(bw.clz())) {
            //如果是插件，则插入
            XApp.global().plug(bw.raw());
        } else {
            //设置remoting状态
            bw.remotingSet(anno.remoting());

            //注册到管理中心
            beanRegister(bw, anno.value());

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
    }


    /**
     * 注册到管理中心
     */
    public void beanRegister(BeanWrap bw, String name) {
        if (XUtil.isEmpty(name) == false) {
            //有name的，只用name注入
            //
            Aop.factory().putWrap(name, bw);
        } else {
            Aop.factory().putWrap(bw.clz(), bw);
            Aop.factory().putWrap(bw.clz().getName(), bw);

            //如果有父级接口，则建立关系映射
            Class<?>[] list = bw.clz().getInterfaces();
            for (Class<?> c : list) {
                if (c.getName().contains("java.") == false) {
                    //建立关系映射
                    clzMapping.put(c, bw.clz());
                    Aop.factory().putWrap(c,bw);
                }
            }
        }
    }

    //::注入

    /**
     * 为一个对象注入（可以重写）
     */
    public void inject(Object obj) {
        if (obj == null) {
            return;
        }

        ClassWrap clzWrap = ClassWrap.get(obj.getClass());
        Field[] fs = clzWrap.fields;
        for (Field f : fs) {
            Annotation[] annS = f.getDeclaredAnnotations();
            if (annS.length > 0) {
                VarHolder varH = clzWrap.getFieldWrap(f).holder(obj);
                tryBeanInject(varH, annS);
            }
        }
    }


    //::库管理



    //::加载相关

    /**
     * 加载 bean 及对应处理
     */
    public void beanLoad(Class<?>... sources) {
        if (sources == null || sources.length == 0) {
            return;
        }

        for (Class s1 : sources) {
            beanLoadDo(s1);
        }

        //尝试加载事件（不用函数包装，是为了减少代码）
        loadedEvent.forEach(f -> f.run());
    }

    private void beanLoadDo(Class<?> source){
        //确定文件夹名
        String dir = "";
        if (source.getPackage() != null) {
            dir = source.getPackage().getName().replace('.', '/');
        }

        //扫描类文件并处理（采用两段式加载，可以部分bean先处理；剩下的为第二段处理）
        XScaner.scan(dir, n -> n.endsWith(".class"))
                .stream()
                .map(name -> {
                    String className = name.substring(0, name.length() - 6);
                    return XUtil.loadClass(className.replace("/", "."));
                })
                .forEach((clz) -> {
                    if (clz != null) {
                        tryBeanCreate(clz);
                    }
                });
    }

    ////////////////////////////////////////////////////////

    /**
     * 执行对象构建
     */
    public static void beanBuild(String beanName, MethodWrap mWrap, BeanWrap bw, XInject beanInj, Function<Parameter, String> injectVal) throws Exception {
        int size2 = mWrap.getParameters().length;

        if (size2 == 0) {
            //0.没有参数
            Object raw = mWrap.invoke(bw.raw());
            beanBuildEnd(beanName, beanInj, raw);
        } else {
            //1.构建参数
            List<Object> args2 = new ArrayList<>(size2);
            List<VarHolderParam> args1 = new ArrayList<>(size2);

            for (Parameter p1 : mWrap.getParameters()) {
                VarHolderParam p2 = new VarHolderParam(p1);
                args1.add(p2);

                beanInject(p2, injectVal.apply(p1));
            }

            //异步获取注入值
            XUtil.commonPool.submit(() -> {
                try {
                    for (VarHolderParam p2 : args1) {
                        args2.add(p2.getValue());
                    }

                    Object raw = mWrap.invoke(bw.raw(), args2.toArray());
                    beanBuildEnd(beanName, beanInj, raw);
                } catch (Throwable ex) {
                    XEventBus.push(ex);
                }

                return true;
            });
        }
    }

    protected static void beanBuildEnd(String beanName, XInject beanInj, Object raw) {
        if (raw != null) {
            if (beanInj != null && XUtil.isEmpty(beanInj.value()) == false) {
                if (beanInj.value().startsWith("${")) {
                    Aop.inject(raw, XApp.cfg().getPropByExpr(beanInj.value()));
                }
            }

            //动态构建的bean，都用新生成wrap（否则会类型混乱）
            BeanWrap m_bw = new BeanWrap(raw.getClass(), raw);
            Aop.factory().beanRegister(m_bw, beanName);
        }
    }

    /**
     * 执行变量注入
     */
    public static void beanInject(VarHolder varH, String name) {
        if (XUtil.isEmpty(name)) {
            //如果没有name,使用类型进行获取 bean
            Aop.getAsyn(varH.getType(), (bw) -> {
                varH.setValue(bw.get());
            });
        } else if (name.startsWith("${") == false) {
            //使用name, 获取BEAN
            Aop.getAsyn(name, (bw) -> {
                varH.setValue(bw.get());
            });
        } else {
            //配置 ${xxx}
            name = name.substring(2, name.length() - 1);

            if (Properties.class == varH.getType()) {
                //如果是 Properties
                Properties val = XApp.cfg().getProp(name);
                varH.setValue(val);
            } else if (Map.class == varH.getType()) {
                //如果是 Map
                Map val = XApp.cfg().getXmap(name);
                varH.setValue(val);
            } else {
                //2.然后尝试获取配置
                String val = XApp.cfg().get(name);
                if (val == null) {
                    Class<?> pt = varH.getType();

                    if (pt.getName().startsWith("java.") || pt.isArray() || pt.isPrimitive()) {
                        //如果是java基础类型，则为null（后面统一地 isPrimitive 做处理）
                        //
                        varH.setValue(null); //暂时不支持数组注入
                    } else {
                        //尝试转为实体
                        Properties val0 = XApp.cfg().getProp(name);
                        Object val2 = ClassWrap.get(pt).newBy(val0::getProperty);
                        varH.setValue(val2);
                    }
                } else {
                    Object val2 = TypeUtil.changeOfPop(varH.getType(), val);
                    varH.setValue(val2);
                }
            }
        }
    }
}
