package org.noear.solon.proxy.integration;

import org.noear.solon.Utils;
import org.noear.solon.core.*;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.proxy.ProxyUtil;
import org.noear.solon.aspect.annotation.Dao;
import org.noear.solon.aspect.annotation.Repository;
import org.noear.solon.aspect.annotation.Service;

public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) {
        //@deprecated 2.2
        context.beanBuilderAdd(Dao.class, (clz, bw, anno) -> {
            String beanName = Utils.annoAlias(anno.value(), anno.name());

            ProxyUtil.binding(bw, beanName, anno.typed());
            LogUtil.global().error("@Dao will be removed, suggested use '@Component'");
        });

        //@deprecated 2.2
        context.beanBuilderAdd(Service.class, (clz, bw, anno) -> {
            String beanName = Utils.annoAlias(anno.value(), anno.name());

            ProxyUtil.binding(bw, beanName, anno.typed());
            LogUtil.global().error("@Service will be removed, suggested use '@Component'");
        });

        //@deprecated 2.2
        context.beanBuilderAdd(Repository.class, (clz, bw, anno) -> {
            String beanName = Utils.annoAlias(anno.value(), anno.name());

            ProxyUtil.binding(bw, beanName, anno.typed());
            LogUtil.global().error("@Repository will be removed, suggested use '@Component'");
        });
    }
}
