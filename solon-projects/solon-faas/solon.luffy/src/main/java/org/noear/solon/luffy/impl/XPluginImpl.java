package org.noear.solon.luffy.impl;

import org.noear.luffy.dso.JtFun;
import org.noear.luffy.dso.JtUtil;
import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AppContext context) {
        Solon.app().sharedAdd("XFun", JtFun.g);
        Solon.app().sharedAdd("XUtil", JtUtil.g);

        JtRun.init();

        JtRun.xfunInit();
    }
}
