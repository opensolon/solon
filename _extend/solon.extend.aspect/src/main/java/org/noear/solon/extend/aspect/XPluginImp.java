package org.noear.solon.extend.aspect;

import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XPlugin;
import org.noear.solon.extend.aspect.annotation.XDao;
import org.noear.solon.extend.aspect.annotation.XService;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        Aop.factory().beanCreatorAdd(XDao.class, (clz, bw, anno) -> {
            bw.proxySet(BeanProxyImp.global());
            Aop.factory().beanNotice(clz,bw);
        });

        Aop.factory().beanCreatorAdd(XService.class, (clz, bw, anno) -> {
            bw.proxySet(BeanProxyImp.global());
            Aop.factory().beanNotice(clz,bw);
        });
    }
}
