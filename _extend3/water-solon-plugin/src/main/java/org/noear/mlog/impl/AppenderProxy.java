package org.noear.mlog.impl;

import org.noear.mlog.AppenderSimple;
import org.noear.mlog.Level;
import org.noear.mlog.Metainfo;
import org.noear.solon.Solon;

/**
 * @author noear
 * @since 1.3
 */
public class AppenderProxy extends AppenderSimple {
    private static AppenderProxy instance;

    public static AppenderProxy getInstance() {
        if (instance == null) {
            instance = new AppenderProxy();
        }

        return instance;
    }

    protected AppenderConsoleImp consoleImp;
    protected AppenderWaterImp waterImp;

    public AppenderProxy() {
        consoleImp = new AppenderConsoleImp();
        waterImp = new AppenderWaterImp();
    }

    @Override
    public String getName() {
        return "proxy";
    }

    @Override
    public void append(String name, Class<?> clz, Level level, Metainfo metainfo, Object content) {
        waterImp.append(name, clz, level, metainfo, content);

        if (Solon.cfg().isFilesMode() || Solon.cfg().isDebugMode()) {
            consoleImp.append(name, clz, level, metainfo, content);
        }
    }
}
