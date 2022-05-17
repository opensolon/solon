package org.noear.solon.cloud.extend.cloudeventplus.integration;

import org.noear.solon.cloud.extend.cloudeventplus.CloudEventSubscribe;
import org.noear.solon.cloud.extend.cloudeventplus.impl.CloudEventSubscribeBeanBuilder;
import org.noear.solon.cloud.extend.cloudeventplus.impl.CloudEventSubscribeBeanExtractor;
import org.noear.solon.core.Aop;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.SolonApp;

/**
 * @author iYarnFog
 * @since 1.5
 */
public class XPluginImp implements Plugin {

    @Override
    public void start(AopContext context) {
        context.beanBuilderAdd(CloudEventSubscribe.class,
                new CloudEventSubscribeBeanBuilder());

        context.beanExtractorAdd(CloudEventSubscribe.class,
                new CloudEventSubscribeBeanExtractor());
    }
}
