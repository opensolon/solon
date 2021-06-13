package org.noear.nami.coder.json.hutool;

import org.noear.nami.NamiManager;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.5
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        NamiManager.reg(HutoolJsonDecoder.instance);
        NamiManager.reg(HutoolJsonEncoder.instance);
        NamiManager.reg(HutoolJsonTypeEncoder.instance);
    }
}
