package org.noear.solon.extend.jsr330;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWebWrap;
import org.noear.solon.core.XPlugin;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        Aop.factory().beanCreatorAdd(Named.class, (clz, bw, anno) -> {

            if (XPlugin.class.isAssignableFrom(bw.clz())) {
                //如果是插件，则插入
                XApp.global().plug(bw.raw());
            } else {

                //注册到管理中心
                Aop.factory().beanRegister(bw, anno.value());

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

        Aop.factory().beanInjectorAdd(Inject.class, (fwT, anno) -> {
            Named tmp = fwT.getType().getAnnotation(Named.class);
            if(tmp == null || XUtil.isEmpty(tmp.value())){
                Aop.factory().beanInject(fwT, null);
            }else{
                Aop.factory().beanInject(fwT, tmp.value());
            }
        });

        Aop.factory().beanCreatorAdd(Singleton.class, (clz, bw, anno) -> {
            bw.singletonSet(true);
        });
    }
}
