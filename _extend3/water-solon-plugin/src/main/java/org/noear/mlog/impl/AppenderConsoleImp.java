package org.noear.mlog.impl;

import org.noear.mlog.AppenderSimple;
import org.noear.mlog.Level;
import org.noear.mlog.Metainfo;
import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.core.util.PrintUtil;

import java.util.Date;

/**
 * @author noear
 * @since 1.3
 */
public class AppenderConsoleImp extends AppenderSimple {
    public AppenderConsoleImp() {
        String levelStr = Solon.cfg().get("solon.mlog.appender." + getName() + ".level");
        setLevel(Level.of(levelStr, Level.TRACE));
    }

    @Override
    public String getName() {
        return "console";
    }

    @Override
    public void append(String name, Class<?> clz, Level level, Metainfo metainfo, Object content) {
        String levelStr = "[" + level.name() + "]";
        switch (level) {
            case ERROR: {
                PrintUtil.red(levelStr);
                break;
            }
            case INFO: {
                PrintUtil.blue(levelStr);
                break;
            }
            default: {
                PrintUtil.black(levelStr);
                break;
            }
        }

        StringBuilder buf = new StringBuilder();
        buf.append(" ").append(new Date().toInstant()).append(" ");
        buf.append("[").append(Thread.currentThread().getName()).append("]");

        if (metainfo != null) {
            buf.append(metainfo.toString());
        }

        if (clz != null) {
            buf.append(" ").append(clz.getTypeName()).append("#").append(getName());
        } else {
            buf.append(" ").append(name).append("#").append(getName());
        }

        buf.append(" ::\r\n");

        if (content instanceof String) {
            buf.append(content);
        } else {
            buf.append(ONode.loadObj(content));
        }

        if (level == Level.ERROR) {
            System.err.println(buf.toString());
        } else {
            System.out.println(buf.toString());
        }
    }
}
