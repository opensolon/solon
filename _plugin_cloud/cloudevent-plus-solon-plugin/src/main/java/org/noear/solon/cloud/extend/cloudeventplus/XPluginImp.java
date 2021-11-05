package org.noear.solon.cloud.extend.cloudeventplus;

import org.noear.solon.SolonApp;
import org.noear.solon.cloud.extend.cloudeventplus.impl.CloudEventSubscribeBeanBuilder;
import org.noear.solon.cloud.extend.cloudeventplus.impl.CloudEventSubscribeBeanExtractor;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;

/**
 * @author iYarnFog
 * @since 1.5
 */
public class XPluginImp implements Plugin {

    @Override
    public void start(SolonApp app) {
        Aop.context().beanBuilderAdd(CloudEventSubscribe.class, CloudEventSubscribeBeanBuilder.instance);

        Aop.context().beanExtractorAdd(CloudEventSubscribe.class, CloudEventSubscribeBeanExtractor.instance);
    }
}
