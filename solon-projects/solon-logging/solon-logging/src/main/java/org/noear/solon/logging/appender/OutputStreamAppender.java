/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.logging.appender;

import org.noear.snack4.ONode;
import org.noear.solon.core.util.JavaUtil;
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
                    greenln(title);
                    break;
            }

            //print content
            //
            if (content instanceof String) {
                out.println(content);
            } else {
                out.println(ONode.serialize(content));
            }
        } finally {
            SYNC_LOCK.unlock();
        }
    }


    protected void redln(Object txt) {
        colorln(PrintUtil.ANSI_RED, txt);
    }

    protected void blueln(Object txt) {
        colorln(PrintUtil.ANSI_BLUE, txt);
    }

    protected void greenln(Object txt) {
        colorln(PrintUtil.ANSI_GREEN, txt);
    }

    protected void purpleln(Object txt) {
        colorln(PrintUtil.ANSI_PURPLE, txt);
    }

    protected void yellowln(Object txt) {
        colorln(PrintUtil.ANSI_YELLOW, txt);
    }

    protected void colorln(String color, Object s) {
        if (JavaUtil.IS_WINDOWS) {
            out.println(s);
        } else {
            out.println(color + s);
            out.print(PrintUtil.ANSI_RESET);
        }
    }
}