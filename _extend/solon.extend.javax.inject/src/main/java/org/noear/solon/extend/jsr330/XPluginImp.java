package org.noear.solon.extend.jsr330;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.handle.HandlerLoader;
import org.noear.solon.core.Plugin;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        Aop.context().beanBuilderAdd(Named.class, (clz, bw, anno) -> {

            if (Plugin.class.isAssignableFrom(bw.clz())) {
                //如果是插件，则插入
                Solon.global().plug(bw.raw());
            } else {

                //注册到容器
                Aop.context().beanRegister(bw, anno.value(), false);

                //如果是remoting状态，转到 Solon 路由器
                if (bw.remoting()) {
                    HandlerLoader bww = new HandlerLoader(bw);
                    if (bww.mapping() != null) {
                        //
                        //如果没有xmapping，则不进行web注册
                        //
                        bww.load(Solon.global());
                    }
                }
            }
        });

        Aop.context().beanInjectorAdd(Inject.class, (fwT, anno) -> {
            Named tmp = fwT.getType().getAnnotation(Named.class);
            if(tmp == null || Utils.isEmpty(tmp.value())){
                Aop.context().beanInject(fwT, null);
            }else{
                Aop.context().beanInject(fwT, tmp.value());
            }
        });

        Aop.context().beanBuilderAdd(Singleton.class, (clz, bw, anno) -> {
            bw.singletonSet(true);
        });
    }
}
