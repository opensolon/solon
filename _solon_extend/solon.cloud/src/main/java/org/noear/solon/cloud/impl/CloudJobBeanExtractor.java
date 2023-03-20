package org.noear.solon.cloud.impl;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.annotation.CloudJob;
import org.noear.solon.core.BeanExtractor;
import org.noear.solon.core.BeanWrap;

import java.lang.reflect.Method;

/**
 * @author noear
 * @since 1.4
 */
public class CloudJobBeanExtractor implements BeanExtractor<CloudJob> {
    private static final CloudJobBeanExtractor instance = new CloudJobBeanExtractor();

    public static CloudJobBeanExtractor getInstance() {
        return instance;
    }

    @Override
    public void doExtract(BeanWrap bw, Method method, CloudJob anno) {
        if (CloudClient.job() == null) {
            throw new IllegalArgumentException("Missing CloudJobService component");
        }

        //支持${xxx}配置
        String name = Solon.cfg().getByParse(anno.value());
        if (Utils.isEmpty(name)) {
            name = Solon.cfg().getByParse(anno.name());
        }
        //支持${xxx}配置
        String description = Solon.cfg().getByParse(anno.description());

        if (name.trim().length() == 0) {
            throw new IllegalStateException("CloudJob name invalid, for[" + bw.clz() + "#" + method.getName() + "] .");
        }
        if (CloudClient.job().isRegistered(name)) {
            throw new IllegalStateException("CloudJob[" + name + "] naming conflicts.");
        }

        //method 可以有返回结果
        method.setAccessible(true);

        CloudClient.job().register(name, anno.cron7x(), description, new CloudJobMethod(bw, method));
    }
}
