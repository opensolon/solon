package org.noear.solon.core;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.utils.TypeUtil;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

public class AopUtils {
    /**
     * 执行bean构建
     * */
    public static void beanBuildDo(String beanName, MethodWrap mWrap, BeanWrap bw, Function<Parameter,String> annoVal) throws Exception{
        int size2 = mWrap.getParameters().length;

        if (size2 == 0) {
            Object raw = mWrap.invoke(bw.raw());

            if (raw != null) {
                BeanWrap m_bw = Aop.put(raw.getClass(), raw);
                Aop.factory().beanRegister(m_bw, beanName);
            }
        } else {
            //1.构建参数
            List<Object> args2 = new ArrayList<>(size2);
            List<VarHolderParam> args1 = new ArrayList<>(size2);

            for (Parameter p1 : mWrap.getParameters()) {
                VarHolderParam p2 = new VarHolderParam(p1);
                args1.add(p2);

                beanInjectDo(p2, annoVal.apply(p1));
            }

            //异步获取注入值
            XUtil.commonPool.submit(() -> {
                for (VarHolderParam p2 : args1) {
                    args2.add(p2.getValue());
                }

                Object raw = mWrap.invoke(bw.raw(), args2.toArray());

                if (raw != null) {
                    BeanWrap m_bw = Aop.put(raw.getClass(), raw);
                    Aop.factory().beanRegister(m_bw, beanName);
                }

                return true;
            });
        }
    }

    /**
     * 执行变量注入
     * */
    public static void beanInjectDo(VarHolder varH, String name) {
        if (XUtil.isEmpty(name)) {
            //如果没有name,使用类型进行获取 bean
            Aop.getAsyn(varH.getType(), (bw) -> {
                varH.setValue(bw.get());
            });
        } else {
            if (name.startsWith("${")) {
                //配置
                name = name.substring(2,name.length()-1);

                if (Properties.class == varH.getType()) {
                    //如果是 Properties，只尝试从配置获取
                    Properties val = XApp.cfg().getProp(name);
                    varH.setValue(val);
                } else {
                    //2.然后尝试获取配置
                    String val = XApp.cfg().get(name);

                    if (XUtil.isEmpty(val) == false) {
                        Object val2 = TypeUtil.changeOfPop(varH.getType(), val);
                        varH.setValue(val2);
                    }else{
                        varH.setValue(null);
                    }
                }
            } else {
                //BEAN
                Object tmp = Aop.get(name);

                if (tmp != null) {
                    varH.setValue(tmp);
                } else {
                    //3.如果没有配置，尝试异步获取BEAN
                    Aop.getAsyn(name, (bw) -> {
                        varH.setValue(bw.get());
                    });
                }
            }
        }
    }
}
