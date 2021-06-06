package org.noear.solon.cloud.extend.opentracing;

import org.noear.solon.SolonApp;
import org.noear.solon.cloud.extend.opentracing.filter.FilterAdapter;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.4
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        if (app.source().getAnnotation(EnableOpentracing.class) != null) {
            app.filter(new FilterAdapter());
        }
    }
}
