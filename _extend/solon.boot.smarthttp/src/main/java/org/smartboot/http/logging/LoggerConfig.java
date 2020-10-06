package org.smartboot.http.logging;

import org.smartboot.http.utils.Param;
import org.smartboot.http.utils.ParamReflect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

/**
 * 日志工具RunLogger配置参数类
 *
 * @author 三刀
 * @version V1.0 , 2020/1/1
 */
class LoggerConfig {
    private static final int MB = 1024 * 1024;

    /**
     * 日志级别
     */
    @Param(
            value = "FINE")
    private Level level;

    /**
     * 日志是否输出至控制台
     */
    @Param(
            value = "true")
    private boolean log2console;

    /**
     * 日志记录编码方式
     */
    @Param(
            value = "utf-8")
    private String encoding;

    /**
     * 日志文件限制大小,单位MB
     */
    @Param(
            value = "2")
    private int limit;

    /**
     * 日志文件存放目录
     */
    @Param(
            value = "")
    private String logDir;

    /**
     * 当前日志系统名称
     */
    @Param(
            value = "runlogger.log")
    private String logName;

    /**
     * 日志文件输出至文件
     */
    @Param(
            value = "false")
    private boolean log2File;

    /**
     * System.err流输出至文件
     */
    @Param(
            value = "false")
    private boolean err2File;

    /**
     * System.out流输出至文件
     */
    @Param(
            value = "false")
    private boolean out2File;

    /**
     * 是否对日志文件打包处理
     */
    @Param(
            value = "false")
    private boolean pack;

    public boolean load(String file) {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            return load(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                in = null;
            }
        }
        return false;
    }

    public boolean load(InputStream in) {
        return ParamReflect.reflect(in, this);
    }

    /**
     * 是否需要将日志输出至文件
     *
     * @return
     */
    public boolean isLog2File() {
        return log2File;
    }

    public void setLog2File(boolean log2File) {
        this.log2File = log2File;
    }

    public String getLogDir() {
        return (logDir != null && !logDir.endsWith(File.separator)) ? logDir
                + File.separator : logDir;
    }

    public void setLogDir(String logDir) {
        this.logDir = logDir;
    }

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public boolean isPack() {
        return pack;
    }

    public void setPack(boolean pack) {
        this.pack = pack;
    }

    public String getEncoding() {
        return encoding == null ? "utf-8" : encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public int getLimit() {
        return limit * MB;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public boolean isErr2File() {
        return err2File;
    }

    public void setErr2File(boolean err2File) {
        this.err2File = err2File;
    }

    public boolean isOut2File() {
        return out2File;
    }

    public void setOut2File(boolean out2File) {
        this.out2File = out2File;
    }

    public boolean isLog2console() {
        return log2console;
    }

    public void setLog2console(boolean log2console) {
        this.log2console = log2console;
    }

    Handler getLogFileHandler() {
        Handler fh = null;
        try {
            LogFormatter sf = new LogFormatter();
            fh = new FileHandler(getLogDir() + getLogName(), getLimit(), 100,
                    true);
            fh.setFormatter(sf);
            fh.setEncoding(getEncoding());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fh;
    }

    /**
     * 获取运行时异常的Handler;文件输出流设置为System.err,用于捕获异常、错误信息
     *
     * @return
     */
    Handler getErrorFileHandler() {
        Handler errFh = null;
        try {
            LogFormatter sf = new LogFormatter();
            // 设置运行时异常的Handler
            errFh = new FileHandler(getLogDir() + "error.log", getLimit(), 100,
                    true) {
                @Override
                protected synchronized void setOutputStream(OutputStream out)
                        throws SecurityException {
                    System.setErr(new PrintStream(out, true));
                }
            };
            errFh.setFormatter(sf);
            errFh.setEncoding(getEncoding());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return errFh;
    }

    /**
     * 文件输出流设置为System.out,并将该输出流设置为FileHandler
     *
     * @return
     */
    Handler getOutFileHandler() {
        Handler outFh = null;
        try {
            LogFormatter sf = new LogFormatter();
            // 设置运行时异常的Handler
            outFh = new FileHandler(getLogDir() + "out.log", getLimit(), 100,
                    true) {
                @Override
                protected synchronized void setOutputStream(OutputStream out)
                        throws SecurityException {
                    System.setOut(new PrintStream(out, true));
                }
            };
            outFh.setFormatter(sf);
            outFh.setEncoding(getEncoding());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outFh;
    }

    Handler getConsoleHandler() {
        ConsoleHandler ch = new ConsoleHandler();
        ch.setFormatter(new LogFormatter());
        ch.setLevel(Level.ALL);
        try {
            ch.setEncoding(getEncoding());
            // ch.setEncoding("utf8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ch;
    }
}