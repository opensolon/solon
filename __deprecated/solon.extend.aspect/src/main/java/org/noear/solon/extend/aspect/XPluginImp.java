package org.noear.solon.extend.aspect;

import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.aspect.annotation.Dao;
import org.noear.solon.extend.aspect.annotation.Repository;
import org.noear.solon.extend.aspect.annotation.Service;

@Deprecated
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        context.beanBuilderAdd(Dao.class, (clz, bw, anno) -> {
            String beanName = Utils.annoAlias(anno.value(), anno.name());

            AspectUtil.binding(bw, beanName, anno.typed());
        });

        context.beanBuilderAdd(Service.class, (clz, bw, anno) -> {
            String beanName = Utils.annoAlias(anno.value(), anno.name());

            AspectUtil.binding(bw, beanName, anno.typed());
        });

        context.beanBuilderAdd(Repository.class, (clz, bw, anno) -> {
            String beanName = Utils.annoAlias(anno.value(), anno.name());

            AspectUtil.binding(bw, beanName, anno.typed());
        });
    }
}
