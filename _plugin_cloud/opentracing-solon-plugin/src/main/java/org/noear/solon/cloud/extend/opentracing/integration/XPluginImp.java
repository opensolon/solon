package org.noear.solon.cloud.extend.opentracing.integration;

import org.noear.nami.NamiManager;
import org.noear.solon.SolonApp;
import org.noear.solon.cloud.extend.opentracing.OpentracingProps;
import org.noear.solon.cloud.opentracing.TracingManager;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.4
 */
public class XPluginImp implements Plugin {

    @Override
    public void start(SolonApp app) {
        if (OpentracingProps.instance.getTraceEnable() == false) {
            return;
        }

        TracingManager.enable();
    }
}
