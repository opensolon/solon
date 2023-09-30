package org.noear.nami.coder.protostuff;

import org.noear.nami.NamiManager;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.2
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) {
        NamiManager.reg(ProtostuffDeoder.instance);
        NamiManager.reg(ProtostuffEncoder.instance);
    }
}
