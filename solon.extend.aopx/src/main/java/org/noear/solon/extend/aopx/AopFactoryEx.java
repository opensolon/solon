package org.noear.solon.extend.aopx;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.annotation.XInject;
import org.noear.solon.core.*;
import org.noear.solon.extend.aopx.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AopFactoryEx extends AopFactory {

    public AopFactoryEx(){
        super();

        //也可以清掉以前的加载器
        //beanLoaders.clear();

        beanLoaderAdd(Component.class,(bw,anno)->{
            if(XUtil.isEmpty(anno.value())==false){
                Aop.put(anno.value(), bw);
            }
        });

        beanLoaderAdd( Configuration.class, (bw,anno)->{
            Method[] methods = bw.clz().getDeclaredMethods();
            for(Method m : methods) {
                Bean ma = m.getAnnotation(Bean.class);
                if (ma != null) {
                    ConfigurationProperties mac =  m.getAnnotation(ConfigurationProperties.class);

                    Object mval = m.invoke(bw.raw());

                    if(mac != null){
                        injectConfig(m.getReturnType(), mval, mac);
                    }

                    BeanWrap mwrap = wrap(m.getReturnType(), mval);
                    Aop.put(ma.name(), mwrap);
                }
            }
        });

        beanLoaderAdd(Dao.class, (bw,anno)->{
            Class<?>[] list = bw.clz().getInterfaces();
            for(Class<?> c : list) {
                if (c.getName().indexOf("java.") != 0) {
                    clzMapping.put(c, bw.clz());
                    beanNotice(c, bw);
                }
            }
        });

        beanLoaderAdd(Service.class, (bw,anno)->{
            if(anno.remoting()){
                BeanWebWrap wrap = new BeanWebWrap(bw);
                wrap.remotingSet(true);
                wrap.load(XApp.global());
            }else{
                Class<?>[] list = bw.clz().getInterfaces();
                for(Class<?> c : list) {
                    if (c.getName().indexOf("java.") != 0) {
                        clzMapping.put(c, bw.clz());
                        beanNotice(c, bw);
                    }
                }
            }
        });



        beanLoaderAdd(Controller.class,(bw,anno)->{
            BeanWebWrap wrap = new BeanWebWrap(bw);
            wrap.load(XApp.global());
        });

        beanLoaderAdd(Interceptor.class,(bw,anno)->{
            BeanWebWrap wrap = new BeanWebWrap(bw);
            wrap.endpointSet(anno.before()? XEndpoint.before : XEndpoint.after);
            wrap.load(XApp.global());
        });
    }

    @Override
    public void inject(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for(Field f : fields){
            XInject xi = f.getAnnotation(XInject.class);
            if(xi != null){
                if(XUtil.isEmpty(xi.value())){
                    Aop.getAsyn(f.getType(), (bw)->{
                        fieldSet(obj,f, bw.get());
                    });
                }else{
                    Aop.getAsyn(xi.value(), (bw)->{
                        fieldSet(obj,f, bw.get());
                    });
                }
            }else{
                Resource fa = f.getAnnotation(Resource.class);
                if(fa != null){
                    injectResource(f,obj);
                }
            }
        }
    }

    private static Map<Class<?>,Method> _propSetLib = new HashMap<>();
    public static void injectConfig(Class<?> clz, Object obj, ConfigurationProperties anno) throws Exception {
        Method method = null;
        Properties prop = XApp.global().prop().getProp(anno.value());

        if (_propSetLib.containsKey(clz)) {
            method = _propSetLib.get(clz);
            if (method != null) {
                method.invoke(obj, prop);
            }
            return;
        }

        method = clz.getMethod("setConfigurationProperties", Properties.class);
        if (method != null) {
            _propSetLib.put(clz, method);
            method.invoke(obj, prop);
        } else {
            _propSetLib.put(clz, null);
        }
    }

    public static void injectResource(Field f, Object obj)  {

        Resource fa = f.getAnnotation(Resource.class);
        if (fa != null) {
            //
            //如果找到 Resource 注解
            //
            if (XUtil.isEmpty(fa.value())) {
                //
                //如果没有名字，按类来
                //
                Aop.getAsyn(f.getType(), (bw)->{
                    fieldSet(obj, f, bw.get());
                });
            } else {
                //
                //如果有名字...（先在配置里找）
                //
                Object fval = XApp.global().prop().get(fa.value());
                if (fval == null) {
                    //如果配置里没有，去aop里找
                    Aop.getAsyn(fa.value(), (bw)->{
                        fieldSet(obj, f, bw.get());
                    });
                } else {
                    //如果配置里有，转换类型并附值
                    fval = typeChange(f.getType(), (String) fval);
                    fieldSet(obj, f, fval);
                }
            }
        }
    }


    protected static Object typeChange(Class<?> type, String val)  {
        if (String.class == (type)) {
            return val;
        }

        if(val.length()==0){
            return null;
        }

        if (Integer.class == (type) || type == Integer.TYPE) {
            return Integer.parseInt(val);
        }

        if (Long.class == (type) || type == Long.TYPE) {
            return Long.parseLong(val);
        }

        if (Double.class == (type) || type == Double.TYPE) {
            return Double.parseDouble(val);
        }

        if (Float.class == (type) || type == Float.TYPE) {
            return Float.parseFloat(val);
        }

        if (Boolean.class == (type) || type == Boolean.TYPE) {
            return Boolean.parseBoolean(val);
        }

        throw new RuntimeException("不支持类型:" + type.getName());
    }
}
