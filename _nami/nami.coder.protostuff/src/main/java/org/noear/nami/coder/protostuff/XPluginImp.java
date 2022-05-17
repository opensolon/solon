package org.noear.nami.coder.protostuff;

import org.noear.nami.NamiManager;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.2
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        NamiManager.reg(ProtostuffDeoder.instance);
        NamiManager.reg(ProtostuffEncoder.instance);
    }
}
