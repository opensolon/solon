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
public class AopFactory extends AopFactoryBase{

    public AopFactory(){
        initialize();
    }

    //::初始化
    /** 初始化（独立出 initialize，方便重写） */
    protected void initialize(){

        beanLoaderAdd(XBean.class,(bw, anno)->{
            if (XPlugin.class.isAssignableFrom(bw.clz())) { //如果是插件，则插入
                XApp.global().plug(bw.raw());
            } else {
                if(XUtil.isEmpty(anno.value()) == false) {
                    Aop.put(anno.value(), bw);
                }

                if (anno.remoting()) {
                    BeanWebWrap bww = new BeanWebWrap(bw);
                    bww.remotingSet(true);
                    bww.load(XApp.global());
                }

                Class<?>[] list = bw.clz().getInterfaces();
                for (Class<?> c : list) {
                    if (c.getName().contains("java.") == false) {
                        clzMapping.put(c, bw.clz());
                        beanNotice(c, bw);//通知子类订阅
                    }
                }
            }
        });

        beanLoaderAdd(XController.class,(bw,anno)->{
            new BeanWebWrap(bw).load(XApp.global());
        });

        beanLoaderAdd(XInterceptor.class,(bw,anno)->{
            BeanWebWrap bww = new BeanWebWrap(bw);
            bww.endpointSet(anno.after() ? XEndpoint.after : XEndpoint.before);
            bww.load(XApp.global());
        });
    }

    //::包装
    /** 获取一个clz的包装（唯一的） */
    public BeanWrap wrap(Class<?> clz, Object raw){
        if(clzMapping.containsKey(clz)){
            clz = clzMapping.get(clz);
        }

        BeanWrap bw = beanWraps.get(clz);
        if(bw == null){
            if(clz.isInterface() && raw == null){ //如查是interfacle 不能入库；且无实例
                return null;
            }

            synchronized (clz) {
                bw = beanWraps.get(clz);
                if(bw==null) {
                    bw = new BeanWrap().build(clz, raw);
                    beanWraps.put(clz, bw);
                }
            }
        }
        return bw;
    }

    //::注入
    /** 为一个对象注入（可以重写） */
    public void inject(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field f : fields) {
            XInject xi = f.getAnnotation(XInject.class);
            if (xi != null) {
                if (XUtil.isEmpty(xi.value())) {
                    //如果没有name,使用类型进行获取 bean
                    Aop.getAsyn(f.getType(), (bw) -> {
                        fieldSet(obj, f, bw.get());
                    });
                } else {
                    //如果有name
                    if(Properties.class == f.getType()){
                        //如果是 Properties，只尝试从配置获取
                        Properties val = Aop.prop().getProp(xi.value());
                        fieldSet(obj, f, val);
                    }else{
                        //如果是单值，先尝试获取配置
                        String val = Aop.prop().get(xi.value());

                        if (XUtil.isEmpty(val) == false) {
                            Object val2 = TypeUtil.change(f.getType(), val);
                            fieldSet(obj, f, val2);
                        } else {
                            //如果没有配置，则找bean
                            Aop.getAsyn(xi.value(), (bw) -> {
                                fieldSet(obj, f, bw.get());
                            });
                        }
                    }
                }
            }
        }
    }

    /** 设置字段值 */
    protected static void fieldSet(Object obj, Field f, Object  val) {
        try {
            f.setAccessible(true);
            f.set(obj, val);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    //::库管理
    /** 加入到bean库 */
    public void put(String key, BeanWrap wrap){
        if(XUtil.isEmpty(key)==false) {
            if (beans.containsKey(key) == false) {
                beans.put(key, wrap);

                beanNotice(key, wrap);
            }
        }
    }

    /** 获取一个bean */
    public BeanWrap get(String key) {
        return beans.get(key);
    }

    //::加载相关
    /** 添加 bean loader */
    public <T extends Annotation> void beanLoaderAdd(Class<T> anno, BeanLoader<T> loader) {
        beanLoaders.put(anno,loader);
    }

    /** 加载 bean 及对应处理 */
    public void beanLoad(Class<?> source) {
        //确定文件夹名
        String dir = source.getPackage().getName().replace('.', '/');

        //扫描类文件并处理（采用两段式加载，可以部分bean先处理；剩下的为第二段处理）
        List<Class<?>> tmpList = new ArrayList<>();
        XScaner.scan(dir, ".class")
                .stream()
                .map(name -> {
                    String className = name.substring(0, name.length() - 6);
                    return XUtil.loadClass(className.replace("/", "."));
                })
                .forEach((clz) -> {
                    if (clz != null) {
                        Annotation[] annoSet = clz.getDeclaredAnnotations();
                        if(annoSet.length>0) {
                            tryLoadClasses(clz, annoSet);
                        }
                    }
                });

        //尝试加载事件（不用函数包装，是为了减少代码）
        loadedEvent.forEach(f->f.run());
    }

    /** 尝试加载一个类 */
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

    /** 尝试加载一个注解 */
    protected <T extends Annotation> void tryLoadAnno(Class<?> clz, T anno, BeanLoader<T> loader) {
        try {
            BeanWrap wrap = wrap(clz, null);
            loader.handler(wrap, anno);

            beanNotice(clz, wrap);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
