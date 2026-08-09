/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.common.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
class LogFormatter extends Formatter {
    private final static String format = "{0,date,yyyy-MM-dd} {0,time,HH:mm:ss.SS}";
    Date dat = new Date();
    String logClassName = RunLogger.class.getName();
    private MessageFormat formatter;
    private Object args[] = new Object[1];

    @Override
    public synchronized String format(LogRecord record) {
        StringBuffer sb = new StringBuffer();
        // Minimize memory allocations here.
        dat.setTime(record.getMillis());
        args[0] = dat;
        StringBuffer text = new StringBuffer();
        if (formatter == null) {
            formatter = new MessageFormat(format);
        }
        formatter.format(args, text, null);
        sb.append("[").append(text).append("] ["); // 时间

        sb.append(record.getLevel().getName());// 日志级别
        sb.append("] ");

//        sb.append("[Thread-" + record.getThreadID() + "] "); // 线程
        sb.append("[" + Thread.currentThread().getName() + "] "); // 线程

        StackTraceElement[] stackElement = new Throwable().getStackTrace();
        boolean lookingForLogger = true;
        for (StackTraceElement stack : stackElement) {
            String cname = stack.getClassName();
            if (lookingForLogger) {
                // Skip all frames until we have found the first logger frame.
                if (cname.matches(logClassName)) {
                    lookingForLogger = false;
                }
            } else {
                if (!cname.matches(logClassName)) {
                    String simpleClassName = cname.substring(cname
                            .lastIndexOf(".") + 1);
                    sb.append("[" + simpleClassName + "("
                            + stack.getMethodName() + ":"
                            + stack.getLineNumber() + ")]");
                    break;
                }
            }
        }

        String message = formatMessage(record);
        sb.append(message);
        if (record.getThrown() != null) {
            try {
                sb.append(System.lineSeparator());
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            } catch (Exception ex) {
            }
        }
        sb.append(System.lineSeparator());
        return sb.toString();
    }
}