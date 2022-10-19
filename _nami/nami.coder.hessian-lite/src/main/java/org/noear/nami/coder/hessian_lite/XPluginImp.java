package org.noear.nami.coder.hessian_lite;

import org.noear.nami.NamiManager;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.10
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        NamiManager.reg(HessianLiteDecoder.instance);
        NamiManager.reg(HessianLiteEncoder.instance);
    }
}
