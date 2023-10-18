package org.noear.nami.coder.fury;

import org.noear.nami.NamiManager;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.2
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AppContext context) {
        NamiManager.reg(FuryDecoder.instance);
        NamiManager.reg(FuryEncoder.instance);
    }
}
