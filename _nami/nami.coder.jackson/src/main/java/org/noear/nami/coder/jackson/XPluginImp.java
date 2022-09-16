package org.noear.nami.coder.jackson;

import org.noear.nami.NamiManager;
import org.noear.solon.SolonApp;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.2
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        NamiManager.reg(JacksonDecoder.instance);
        NamiManager.reg(JacksonEncoder.instance);
        NamiManager.reg(JacksonTypeEncoder.instance);
    }
}
