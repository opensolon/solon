package org.noear.solon.core;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

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
                    AopUtils.beanBuildDo(m_an.value(), mWrap, bw, (p1) -> {
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
            AopUtils.beanInjectDo(fwT, anno.value());
        }));
    }

    /**
     * XBean 的处理
     * */
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
     * */
    public void beanRegister(BeanWrap bw, String name) {
        if (XUtil.isEmpty(name)) {
            Aop.put(bw.clz().getName(), bw);
        } else {
            Aop.put(name, bw);
        }

        //如果有父级接口，则建立关系映射
        Class<?>[] list = bw.clz().getInterfaces();
        for (Class<?> c : list) {
            if (c.getName().contains("java.") == false) {
                //建立关系映射
                clzMapping.put(c, bw.clz());
                beanNotice(c, bw);//通知子类订阅
            }
        }
    }

    //::包装

    /**
     * 获取一个clz的包装（唯一的）
     */
    @Override
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
            bw = new BeanWrap(clz, raw);
            BeanWrap l = beanWraps.putIfAbsent(clz, bw);
            if(l != null){
                bw = l;
            }
        }

        if (bw.raw() == null) {
            bw.rawSet(raw);
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
            Annotation[] annS = f.getDeclaredAnnotations();
            if (annS.length > 0) {
                VarHolder varH = clzWrap.getFieldWrap(f).holder(obj);
                tryBeanInject(varH, annS);
            }
        }
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
     * 加载 bean 及对应处理
     */
    public void beanLoad(Class<?> source) {
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
                        Annotation[] annoSet = clz.getDeclaredAnnotations();
                        if (annoSet.length > 0) {
                            try {
                                tryBeanCreate(clz, annoSet);
                            } catch (Throwable ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });

        //尝试加载事件（不用函数包装，是为了减少代码）
        loadedEvent.forEach(f -> f.run());
    }
}
