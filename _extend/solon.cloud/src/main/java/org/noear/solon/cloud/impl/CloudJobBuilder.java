package org.noear.solon.cloud.impl;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.annotation.CloudJob;
import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.handle.Handler;

/**
 * @author noear
 * @since 1.4
 */
public class CloudJobBuilder implements BeanBuilder<CloudJob> {
    public static final CloudJobBuilder instance = new CloudJobBuilder();

    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, CloudJob anno) throws Exception {
        if (CloudClient.job() == null) {
            throw new IllegalArgumentException("Missing CloudJobService component");
        }

        if (Handler.class.isAssignableFrom(clz)) {
            //支持${xxx}配置
            String name = Solon.cfg().getByParse(Utils.annoAlias(anno.value(), anno.name()));
            //支持${xxx}配置
            String description = Solon.cfg().getByParse(anno.description());

            CloudClient.job().register(name, anno.cron7x(), description, bw.raw());
        }
    }
}
