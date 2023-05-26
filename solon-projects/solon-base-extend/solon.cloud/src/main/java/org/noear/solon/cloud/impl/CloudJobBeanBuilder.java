package org.noear.solon.cloud.impl;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.cloud.annotation.CloudJob;
import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanWrap;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.4
 */
public class CloudJobBeanBuilder implements BeanBuilder<CloudJob> {
    private static final CloudJobBeanBuilder instance = new CloudJobBeanBuilder();

    public static CloudJobBeanBuilder getInstance() {
        return instance;
    }

    //要使用有顺序的 LinkedHashMap
    private final Map<Class<?>, BeanBuilder<CloudJob>> builderMap = new LinkedHashMap<>();

    /**
     * @since 2.0
     * */
    public void addBuilder(Class<?> beanClz, BeanBuilder<CloudJob> beanBuilder) {
        if (!builderMap.containsKey(beanClz)) {
            builderMap.put(beanClz, beanBuilder);
        }
    }

    public CloudJobBeanBuilder() {
        //默认添加 CloudJobHandler 构建器
        addBuilder(CloudJobHandler.class, (clz, bw, anno) -> {
            //支持${xxx}配置
            String name = Solon.cfg().getByParse(Utils.annoAlias(anno.value(), anno.name()));
            //支持${xxx}配置
            String description = Solon.cfg().getByParse(anno.description());

            CloudClient.job().register(name, anno.cron7x(), description, new CloudJobBean(bw));
        });
    }

    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, CloudJob anno) throws Throwable {
        if (CloudClient.job() == null) {
            throw new IllegalArgumentException("Missing CloudJobService component");
        }

        //@since 2.0
        for (Map.Entry<Class<?>, BeanBuilder<CloudJob>> kv : builderMap.entrySet()) {
            if (kv.getKey().isAssignableFrom(clz)) {
                kv.getValue().doBuild(clz, bw, anno);
                return; //只处理第一个找到的类型
            }
        }

        throw new IllegalArgumentException("Illegal CloudJob type: " + clz.getName());
    }
}
