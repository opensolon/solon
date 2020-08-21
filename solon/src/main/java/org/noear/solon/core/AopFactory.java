package org.noear.solon.core;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.annotation.*;
import org.noear.solon.core.utils.TypeUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;

/**
 * Aop 处理工厂（可以被继承重写）
 *
 * 主要实现三个动作：
 * 1.生成 bean
 * 2.构建 bean
 * 3.注入 字段或参数
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
            for (MethodWrap mWrap : ClassWrap.get(bw.clz()).methodWraps) {
                XBean m_an = mWrap.getMethod().getAnnotation(XBean.class);

                if (m_an != null) {
                    XInject beanInj = mWrap.getMethod().getAnnotation(XInject.class);

                    tryBuildBean(m_an.value(), mWrap, bw, beanInj, (p1) -> {
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
                .stream()
                .map(name -> {
                    String className = name.substring(0, name.length() - 6);
                    return XUtil.loadClass(className.replace("/", "."));
                })
                .forEach((clz) -> {
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
                    clzMapping.putIfAbsent(c, bw.clz());
                    Aop.factory().putWrap(c, bw);
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
        Field[] fs = clzWrap.fields;
        for (Field f : fs) {
            Annotation[] annS = f.getDeclaredAnnotations();
            if (annS.length > 0) {
                VarHolder varH = clzWrap.getFieldWrap(f).holder(obj);
                tryInject(varH, annS);
            }
        }
    }


    ////////////////////////////////////////////////////////

    /**
     * 尝试构建 bean
     */
    public void tryBuildBean(String beanName, MethodWrap mWrap, BeanWrap bw, XInject beanInj, Function<Parameter, String> injectVal) throws Exception {
        int size2 = mWrap.getParameters().length;

        if (size2 == 0) {
            //0.没有参数
            Object raw = mWrap.invoke(bw.raw());
            tryBuildBean0(beanName, beanInj, raw);
        } else {
            //1.构建参数
            List<Object> args2 = new ArrayList<>(size2);
            List<VarHolderParam> args1 = new ArrayList<>(size2);

            for (Parameter p1 : mWrap.getParameters()) {
                VarHolderParam p2 = new VarHolderParam(p1);
                args1.add(p2);

                tryInjectByName(p2, injectVal.apply(p1));
            }

            //异步获取注入值
            XUtil.commonPool.submit(() -> {
                try {
                    for (VarHolderParam p2 : args1) {
                        args2.add(p2.getValue());
                    }

                    Object raw = mWrap.invoke(bw.raw(), args2.toArray());
                    tryBuildBean0(beanName, beanInj, raw);
                } catch (Throwable ex) {
                    XEventBus.push(ex);
                }

                return true;
            });
        }
    }

    protected void tryBuildBean0(String beanName, XInject beanInj, Object raw) {
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
     * 尝试变量注入 字段或参数
     */
    public void tryInjectByName(VarHolder varH, String name) {
        if (XUtil.isEmpty(name)) {
            //如果没有name,使用类型进行获取 bean
            Aop.getAsyn(varH.getType(), (bw) -> {
                varH.setValue(bw.get());
            });
        } else if (name.startsWith("${classpath:")) {
            //
            //demo:${classpath:user.yml}
            //
            String url = name.substring(12,name.length()-1);
            Properties val = XUtil.getProperties(XUtil.getResource(url));

            if (val == null) {
                throw new RuntimeException(name + "  failed to load!");
            }

            if (Properties.class == varH.getType()) {
                varH.setValue(val);
            } else if (Map.class == varH.getType()) {
                Map<String, String> val2 = new HashMap<>();
                val.forEach((k, v) -> {
                    if (k instanceof String && v instanceof String) {
                        val2.put((String) k, (String) v);
                    }
                });
                varH.setValue(val2);
            } else {
                Object val2 = ClassWrap.get(varH.getType()).newBy(val::getProperty);
                varH.setValue(val2);
            }

        } else if (name.startsWith("${")) {
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
        } else {
            //使用name, 获取BEAN
            Aop.getAsyn(name, (bw) -> {
                varH.setValue(bw.get());
            });
        }
    }
}
