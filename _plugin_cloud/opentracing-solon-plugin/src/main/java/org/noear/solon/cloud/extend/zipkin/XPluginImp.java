package org.noear.solon.cloud.extend.zipkin;

import org.noear.solon.SolonApp;
import org.noear.solon.cloud.extend.zipkin.filter.FilterAdapter;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.4
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        app.filter(new FilterAdapter());
    }
}
