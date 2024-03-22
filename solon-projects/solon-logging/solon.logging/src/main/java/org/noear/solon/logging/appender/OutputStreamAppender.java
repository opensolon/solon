package org.noear.solon.logging.appender;

import org.noear.snack.ONode;
import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.logging.event.Level;
import org.noear.solon.logging.event.LogEvent;

import java.io.PrintWriter;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 输出流添加器实现类
 *
 * @author noear
 * @since 1.3
 */
public abstract class OutputStreamAppender extends AppenderSimple {
    protected PrintWriter out = null;
    protected final ReentrantLock SYNC_LOCK = new ReentrantLock(true);
    protected void setOutput(PrintWriter writer) {
        if (writer == null) {
            return;
        }

        //1.保存旧的打印器
        PrintWriter outOld = out;

        //2.换为新的打印器
        out = writer;

        //3.关注旧的打印器
        if (outOld != null) {
            outOld.flush();
            outOld.close();
        }
    }

    @Override
    public void append(LogEvent logEvent) {
        if (out == null) {
            return;
        }

        super.append(logEvent);
    }

    @Override
    protected void appendDo(Level level, String title, Object content) {
        SYNC_LOCK.lock();
        try {
            //print title
            //
            switch (level) {
                case ERROR:
                    PrintUtil.redln(title);
                    break;
                case WARN:
                    PrintUtil.yellowln(title);
                    break;
                case DEBUG:
                    PrintUtil.blueln(title);
                    break;
                case TRACE:
                    PrintUtil.purpleln(title);
                    break;
                default:
                    PrintUtil.greenln(title);
                    break;
            }

            //print content
            //
            if (content instanceof String) {
                out.println(content);
            } else {
                out.println(ONode.stringify(content));
            }
        } finally {
            SYNC_LOCK.unlock();
        }
    }
}