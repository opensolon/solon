package org.noear.solon.cloud.eventplus.integration;

import org.noear.solon.cloud.eventplus.CloudEventSubscribe;
import org.noear.solon.cloud.eventplus.impl.CloudEventSubscribeBeanBuilder;
import org.noear.solon.cloud.eventplus.impl.CloudEventSubscribeBeanExtractor;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

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
