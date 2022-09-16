package org.noear.nami.coder.snack3;

import org.noear.nami.NamiManager;
import org.noear.solon.SolonApp;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear 2021/1/3 created
 * @since 1.2
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        NamiManager.reg(SnackDecoder.instance);
        NamiManager.reg(SnackEncoder.instance);
        NamiManager.reg(SnackTypeEncoder.instance);
    }
}
