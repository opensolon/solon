package org.noear.solon.core;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.annotation.*;
import org.noear.solon.core.utils.TypeUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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

        beanLoaderAdd(XConfiguration.class, (clz, bw, anno) -> {
            for (MethodWrap mWrap : ClassWrap.get(bw.clz()).methodWraps) {
                XBean m_an = mWrap.method.getAnnotation(XBean.class);

                if (m_an != null && mWrap.parameters.length == 0) {
                    Object raw = mWrap.method.invoke(bw.raw());

                    BeanWrap m_bw = Aop.put(mWrap.method.getReturnType(), raw);
                    loadXBean(m_bw, m_an);
                }
            }
        });

        beanLoaderAdd(XBean.class, (clz, bw, anno) -> {
            loadXBean(bw, anno);
        });

        beanLoaderAdd(XController.class, (clz, bw, anno) -> {
            new BeanWebWrap(bw).load(XApp.global());
        });

        beanLoaderAdd(XInterceptor.class, (clz, bw, anno) -> {
            BeanWebWrap bww = new BeanWebWrap(bw);
            bww.endpointSet(anno.after() ? XEndpoint.after : XEndpoint.before);
            bww.load(XApp.global());
        });
    }

    protected void loadXBean(BeanWrap bw, XBean anno) {
        bw._remoting = anno.remoting();

        if (XPlugin.class.isAssignableFrom(bw.clz())) { //如果是插件，则插入
            XApp.global().plug(bw.raw());
        } else {
            if (XUtil.isEmpty(anno.value()) == false) {
                Aop.put(anno.value(), bw);
            } else {
                Aop.put(bw.clz().getName(), bw);
            }

            if (anno.remoting()) {
                BeanWebWrap bww = new BeanWebWrap(bw);
                bww.remotingSet(true);
                bww.load(XApp.global());
            }

            Class<?>[] list = bw.clz().getInterfaces();
            for (Class<?> c : list) {
                if (c.getName().contains("java.") == false) {
                    //建立关系映射
                    clzMapping.put(c, bw.clz());
                    beanNotice(c, bw);//通知子类订阅
                }
            }
        }
    }

    //::包装

    /**
     * 获取一个clz的包装（唯一的）
     */
    public BeanWrap wrap(Class<?> clz, Object raw) {
        //1.先用自己的类型找
        BeanWrap bw = beanWraps.get(clz);

        if (bw == null) {
            //2.尝试Mapping的类型找
            if (clzMapping.containsKey(clz)) {
                clz = clzMapping.get(clz);
                bw = beanWraps.get(clz);
            }
        }

        if (bw == null) {
            //3.如果还没有，则尝试构建
            if (clz.isInterface() && raw == null) { //如查是interfacle 不能入库；且无实例
                return null;
            }

            bw = new BeanWrap(clz, raw);
            beanWraps.putIfAbsent(clz, bw);
        }

        return bw;
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
            XInject xi = f.getAnnotation(XInject.class);
            if (xi != null) {
                FieldWrapTmp fwT = clzWrap.getFieldWrap(f).tmp(obj);

                if (XUtil.isEmpty(xi.value())) {
                    //如果没有name,使用类型进行获取 bean
                    Aop.getAsyn(f.getType(), fwT, (bw) -> {
                        fwT.setValue(bw.get());
                    });
                } else {
                    //如果有name
                    if (Properties.class == f.getType()) {
                        //如果是 Properties，只尝试从配置获取
                        Properties val = XApp.cfg().getProp(xi.value());
                        fwT.setValue(val);
                    } else {
                        //1.如果是单值，先尝试获取BEAN
                        Object tmp = Aop.get(xi.value());

                        if (tmp != null) {
                            fwT.setValue(tmp);
                        } else {
                            //2.然后尝试获取配置
                            String val = XApp.cfg().get(xi.value());

                            if (XUtil.isEmpty(val) == false) {
                                Object val2 = TypeUtil.changeOfPop(f.getType(), val);
                                fwT.setValue(val2);
                            } else {
                                //3.如果没有配置，尝试异步获取BEAN
                                Aop.getAsyn(xi.value(), (bw) -> {
                                    fwT.setValue(bw.get());
                                });
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 尝试外力构建Bean
     */
    protected boolean tryBuildBean(Class<?> clz, FieldWrapTmp fwT) {
        for (BeanBuilder bb : beanBuilders) {
            if(bb.build(clz, fwT)){
                return true;
            }
        }
        return false;
    }

    //::库管理

    /**
     * 加入到bean库
     */
    public void put(String key, BeanWrap wrap) {
        if (XUtil.isEmpty(key) == false) {
            if (beans.containsKey(key) == false) {
                beans.put(key, wrap);

                beanNotice(key, wrap);
            }
        }
    }

    /**
     * 获取一个bean
     */
    public BeanWrap get(String key) {
        return beans.get(key);
    }

    //::加载相关

    /**
     * 添加 bean loader
     */
    public <T extends Annotation> void beanLoaderAdd(Class<T> anno, BeanLoader<T> loader) {
        beanLoaders.put(anno, loader);
    }

    public void beanBuilderAdd(BeanBuilder builder) {
        beanBuilders.add(builder);
    }

    /**
     * 加载 bean 及对应处理
     */
    public void beanLoad(Class<?> source) {
        //确定文件夹名
        String dir = "";
        if(source.getPackage() != null) {
            dir = source.getPackage().getName().replace('.', '/');
        }

        //扫描类文件并处理（采用两段式加载，可以部分bean先处理；剩下的为第二段处理）
        List<Class<?>> tmpList = new ArrayList<>();
        XScaner.scan(dir, n -> n.endsWith(".class"))
                .stream()
                .map(name -> {
                    String className = name.substring(0, name.length() - 6);
                    return XUtil.loadClass(className.replace("/", "."));
                })
                .forEach((clz) -> {
                    if (clz != null) {
                        Annotation[] annoSet = clz.getDeclaredAnnotations();
                        if (annoSet.length > 0) {
                            try {
                                tryLoadClasses(clz, annoSet);
                            }catch (Throwable ex){
                                ex.printStackTrace();
                            }
                        }
                    }
                });

        //尝试加载事件（不用函数包装，是为了减少代码）
        loadedEvent.forEach(f -> f.run());
    }

    /**
     * 尝试加载一个类
     */
    protected boolean tryLoadClasses(Class<?> clz, Annotation[] annoSet) {
        for (Annotation a : annoSet) {
            BeanLoader loader = beanLoaders.get(a.annotationType());

            if (loader != null) {
                tryLoadAnno(clz, a, loader);
                return true;
            }
        }

        return false;
    }

    /**
     * 尝试加载一个注解
     */
    protected <T extends Annotation> void tryLoadAnno(Class<?> clz, T anno, BeanLoader<T> loader) {
        try {
            BeanWrap wrap = wrap(clz, null);
            loader.handler(clz, wrap, anno);

            beanNotice(clz, wrap);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
