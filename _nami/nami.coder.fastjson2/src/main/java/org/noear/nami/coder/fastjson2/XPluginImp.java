package org.noear.nami.coder.fastjson2;

import org.noear.nami.NamiManager;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.9
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        NamiManager.reg(Fastjson2Decoder.instance);
        NamiManager.reg(Fastjson2Encoder.instance);
        NamiManager.reg(Fastjson2TypeEncoder.instance);
    }
}
