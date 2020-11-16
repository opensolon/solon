package org.noear.solon.extend.aspect;

import org.noear.solon.Solon;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.aspect.annotation.XDao;
import org.noear.solon.extend.aspect.annotation.XService;

public class XPluginImp implements Plugin {
    @Override
    public void start(Solon app) {
        Aop.context().beanBuilderAdd(XDao.class, (clz, bw, anno) -> {
            bw.proxySet(BeanProxyImp.global());

            Aop.context().beanRegister(bw,"",true);
        });

        Aop.context().beanBuilderAdd(XService.class, (clz, bw, anno) -> {
            bw.proxySet(BeanProxyImp.global());

            Aop.context().beanRegister(bw,"",true);
        });
    }
}
