package org.noear.solon.logging.appender;

import org.noear.snack.ONode;
import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.logging.event.Level;
import org.noear.solon.logging.event.LogEvent;

import java.io.OutputStream;
import java.io.PrintStream;


/**
 * console appender
 *
 * @author noear
 * @since 1.3
 */
public abstract class OutputStreamAppender extends AppenderSimple {
    protected PrintStream out = null;

    protected void setStream(OutputStream stream) {
        if (stream == null) {
            return;
        }

        //1.保存旧的打印器
        PrintStream outOld = out;

        //2.换为新的打印器
        if (stream instanceof PrintStream) {
            out = (PrintStream) stream;
        } else {
            out = new PrintStream(stream, true);
        }

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
        synchronized (out) {
            //print title
            //
            switch (level) {
                case ERROR:
                    redln(title);
                    break;
                case WARN:
                    yellowln(title);
                    break;
                case DEBUG:
                    blueln(title);
                    break;
                case TRACE:
                    purpleln(title);
                    break;
                default:
                    out.println(title);
                    break;
            }

            //print content
            //
            if (content instanceof String) {
                out.println(content);
            } else {
                out.println(ONode.stringify(content));
            }
        }
    }

    protected void redln(Object txt) {
        out.println(PrintUtil.ANSI_RED + txt);
        out.print(PrintUtil.ANSI_RESET);
    }

    protected void blueln(Object txt) {
        out.println(PrintUtil.ANSI_BLUE + txt);
        out.print(PrintUtil.ANSI_RESET);
    }

    protected void purpleln(String txt) {
        out.println(PrintUtil.ANSI_PURPLE + txt);
        out.print(PrintUtil.ANSI_RESET);
    }

    protected void yellowln(Object txt) {
        out.println(PrintUtil.ANSI_YELLOW + txt);
        out.print(PrintUtil.ANSI_RESET);
    }
}