package org.noear.nami.coder.hessian;

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
        NamiManager.reg(HessianDecoder.instance);
        NamiManager.reg(HessianEncoder.instance);
    }
}
