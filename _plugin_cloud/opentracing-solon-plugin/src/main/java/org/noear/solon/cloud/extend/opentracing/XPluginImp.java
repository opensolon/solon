package org.noear.solon.cloud.extend.opentracing;

import org.noear.nami.NamiManager;
import org.noear.solon.SolonApp;
import org.noear.solon.cloud.extend.opentracing.adapter.NamiInterceptorAdapter;
import org.noear.solon.cloud.extend.opentracing.annotation.EnableOpentracing;
import org.noear.solon.cloud.extend.opentracing.adapter.SolonFilterAdapter;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.4
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        if (app.source().getAnnotation(EnableOpentracing.class) != null) {
            app.filter(new SolonFilterAdapter());
            NamiManager.reg(new NamiInterceptorAdapter());
        }
    }
}
