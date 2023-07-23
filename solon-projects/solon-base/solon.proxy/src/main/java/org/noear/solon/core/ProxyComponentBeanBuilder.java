package org.noear.solon.core;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.annotation.ProxyComponent;
import org.noear.solon.proxy.ProxyUtil;
import org.noear.solon.proxy.integration.UnsupportedUtil;

/**
 * @author noear
 * @since 2.4
 */
public class ProxyComponentBeanBuilder implements BeanBuilder<ProxyComponent> {
    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, ProxyComponent anno) throws Throwable {
        String beanName = Utils.annoAlias(anno.value(), anno.name());

        bw.nameSet(beanName);
        bw.tagSet(anno.tag());
        bw.typedSet(anno.typed());

        //确定顺序位
        bw.indexSet(anno.index());

        ProxyUtil.binding(bw, beanName, anno.typed());

        if (Solon.cfg().isDebugMode()) {
            UnsupportedUtil.check(clz, bw.context(), anno);
        }
    }
}
