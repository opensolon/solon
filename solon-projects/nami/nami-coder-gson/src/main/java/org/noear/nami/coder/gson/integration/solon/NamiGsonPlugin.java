package org.noear.nami.coder.gson.integration.solon;

import org.noear.nami.NamiManager;
import org.noear.nami.coder.gson.GsonDecoder;
import org.noear.nami.coder.gson.GsonEncoder;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * gson插件,负责注册编解码器
 * 
 * @author cqyhm
 * @since 2025年12月30日13:24:30
 */
public class NamiGsonPlugin implements Plugin {
    @Override
    public void start(AppContext context) {
        NamiManager.reg(GsonDecoder.instance);
        NamiManager.reg(GsonEncoder.instance);
    }
}
