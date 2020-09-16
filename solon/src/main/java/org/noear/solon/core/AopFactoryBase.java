package org.noear.solon.core;


import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.annotation.XBean;
import org.noear.solon.annotation.XInject;
import org.noear.solon.core.utils.TypeUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 为 AopFactory 提供存储 支持，并提供注册管理
 *
 * @author noear
 * @since 1.0
 * */
public abstract class AopFactoryBase {
    //////////////////////////
    //
    // 基础存储
    //
    /////////////////////////
    /**
     * bean包装库
     */
    protected final Map<Class<?>, BeanWrap> beanWraps = new ConcurrentHashMap<>();
    /**
     * bean库
     */
    protected final Map<String, BeanWrap> beans = new ConcurrentHashMap<>();
    /**
     * clz mapping
     */
    protected final Map<Class<?>, Class<?>> clzMapping = new ConcurrentHashMap<>();

    //启动时写入
    /**
     * bean loaders
     */
    protected final Map<Class<?>, BeanCreator<?>> beanCreators = new HashMap<>();
    /**
     * bean injectors
     */
    protected final Map<Class<?>, BeanInjector<?>> beanInjectors = new HashMap<>();


    /**
     * 添加 bean creator, injector
     */
    public <T extends Annotation> void beanCreatorAdd(Class<T> anno, BeanCreator<T> creater) {
        beanCreators.put(anno, creater);
    }

    public <T extends Annotation> void beanInjectorAdd(Class<T> anno, BeanInjector<T> injector) {
        beanInjectors.put(anno, injector);
    }

    //////////////////////////
    //
    // bean 对外事件存储
    //
    /////////////////////////

    /**
     * bean 加载完成事件
     */
    protected final Set<Runnable> loadedEvent = new LinkedHashSet<>();

    //////////////////////////
    //
    // bean 对内通知体系
    //
    /////////////////////////

    /**
     * bean订阅者
     */
    protected final Set<BeanSubscriber> subSet = new LinkedHashSet<>();

    /**
     * bean订阅
     */
    public void beanSubscribe(Object key, Consumer<BeanWrap> callback) {
        if (key != null) {
            subSet.add(new BeanSubscriber(key, callback));
        }
    }

    /**
     * bean通知
     */
    public void beanNotice(Object key, BeanWrap wrap) {
        if (wrap.raw() == null) {
            return;
        }

        //避免在forEach时，对它进行add
        new ArrayList<>(subSet).forEach(s1 -> {
            if (s1.key.equals(key)) {
                s1.callback.accept(wrap);
            }
        });
    }

    //public abstract BeanWrap wrap(Class<?> clz, Object raw);


    /**
     * 注册到bean库（注册成功会进行通知）
     *
     * @param wrap 如果raw为null，拒绝注册
     */
    public void putWrap(String key, BeanWrap wrap) {
        if (XUtil.isEmpty(key) == false && wrap.raw() != null) {
            if (beans.containsKey(key) == false) {
                beans.put(key, wrap);
                beanNotice(key, wrap);
            }
        }
    }

    /**
     * 注册到bean库（注册成功会进行通知）
     *
     * @param wrap 如果raw为null，拒绝注册
     */
    public void putWrap(Class<?> key, BeanWrap wrap) {
        if (key != null && wrap.raw() != null) {
            //
            //wrap.raw()==null, 说明它是接口；等它完成代理再注册；以@Db为例，可以看一下
            //
            if (beanWraps.containsKey(key) == false) {
                beanWraps.put(key, wrap);
                beanNotice(key, wrap);
            }
        }
    }

    /**
     * 获取一个bean包装
     */
    public BeanWrap getWrap(Object key) {
        if (key instanceof String) {
            return beans.get(key);
        }

        if (key instanceof Class<?>) {
            return beanWraps.get(key);
        }

        return null;
    }

    public BeanWrap wrap(Class<?> clz, Object bean) {
        BeanWrap wrap = getWrap(clz);
        if (wrap == null) {
            wrap = new BeanWrap(clz, bean);
        }

        return wrap;
    }


    /**
     * 尝试为bean注入
     */
    protected void tryInject(VarHolder varH, Annotation[] annS) {
        for (Annotation a : annS) {
            BeanInjector bi = beanInjectors.get(a.annotationType());
            if (bi != null) {
                bi.handler(varH, a);
            }
        }
    }


    /**
     * 尝试生成 bean
     */
    public void tryCreateBean(Class<?> clz) {
        Annotation[] annS = clz.getDeclaredAnnotations();

        if (annS.length > 0) {
            try {
                for (Annotation a : annS) {
                    BeanCreator bc = beanCreators.get(a.annotationType());
                    if (bc != null) {
                        tryCreateBeanByAnno(clz, a, bc);
                    }
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }


    /**
     * 尝试一个注解处理
     */
    protected <T extends Annotation> void tryCreateBeanByAnno(Class<?> clz, T anno, BeanCreator<T> loader) {
        try {
            //包装
            BeanWrap wrap = this.wrap(clz, null);
            loader.handler(clz, wrap, anno);
            //尝试入库
            this.putWrap(clz, wrap);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
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
    public void tryBuildBean(XBean anno, MethodWrap mWrap, BeanWrap bw, XInject beanInj, Function<Parameter, String> injectVal) throws Exception {
        int size2 = mWrap.getParameters().length;

        if (size2 == 0) {
            //0.没有参数
            Object raw = mWrap.invoke(bw.raw());
            tryBuildBean0(anno, beanInj, mWrap.getReturnType(), raw);
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
                    tryBuildBean0(anno, beanInj, mWrap.getReturnType(), raw);
                } catch (Throwable ex) {
                    XEventBus.push(ex);
                }

                return true;
            });
        }
    }

    protected void tryBuildBean0(XBean anno, XInject beanInj, Class<?> clz, Object raw) {
        if (raw != null) {
            if (beanInj != null && XUtil.isEmpty(beanInj.value()) == false) {
                if (beanInj.value().startsWith("${")) {
                    Aop.inject(raw, XApp.cfg().getPropByExpr(beanInj.value()));
                }
            }

            //动态构建的bean, 可通过广播进行扩展
            XEventBus.push(raw);

            //动态构建的bean，都用新生成wrap（否则会类型混乱）
            BeanWrap m_bw = new BeanWrap(clz, raw);
            m_bw.nameSet(anno.value());
            m_bw.tagSet(anno.tag());
            m_bw.attrsSet(anno.attrs());

            Aop.factory().beanRegister(m_bw, anno.value(), anno.typed());

            //@XBean 产生的 beanWrap（含 name,tag,attrs），进行事件通知
            XEventBus.push(m_bw);
        }
    }

    /**
     * 尝试变量注入 字段或参数
     *
     * @param varH 变量包装器
     * @param name 名字（bean name || config ${name}）
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
            String url = name.substring(12, name.length() - 1);
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
