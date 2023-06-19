package org.noear.solon.web.sse.integration;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 2.3
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AopContext context) throws Throwable {
        Solon.app().chainManager().addReturnHandler(new ActionReturnSseHandler());
    }
}
