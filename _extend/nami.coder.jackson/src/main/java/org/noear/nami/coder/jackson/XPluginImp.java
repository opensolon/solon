package org.noear.nami.coder.jackson;

import org.noear.nami.NamiManager;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;

/**
 * @author noear 2021/1/17 created
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        NamiManager.reg(JacksonDecoder.instance);
        NamiManager.reg(JacksonEncoder.instance);
        NamiManager.reg(JacksonTypeEncoder.instance);
    }
}
