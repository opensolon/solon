package org.noear.solon.extend.springboot.rpc;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.annotation.NamiServer;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.HandlerLoader;

/**
 * @author noear 2021/3/1 created
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        //注册 @Component 构建器
        Aop.context().beanBuilderAdd(NamiServer.class, (clz, bw, anno) -> {
            //设置remoting状态
            bw.remotingSet(true);

            //注册到容器
            Aop.context().beanRegister(bw, "", true);

            //如果是remoting状态，转到 Solon 路由器
            if (bw.remoting()) {
                HandlerLoader bww = new HandlerLoader(bw, anno.mapping());
                if (bww.mapping() != null) {
                    //
                    //如果没有xmapping，则不进行web注册
                    //
                    bww.load(Solon.global());
                }
            }
        });
    }
}
