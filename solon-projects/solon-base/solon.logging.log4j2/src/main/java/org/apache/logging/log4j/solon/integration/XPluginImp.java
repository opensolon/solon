package org.apache.logging.log4j.solon.integration;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.6
 */
public class XPluginImp extends LogIncubatorImpl implements Plugin {

    @Override
    public void start(AopContext context) throws Throwable {
        //容器加载完后，允许再次处理
        incubate();
    }
}
