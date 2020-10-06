/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: RunLogger.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.logging;

import java.io.File;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * @author 三刀
 * @version V1.0 , 2020/1/1
 */
public class RunLogger {

    private static RunLogger runlogger;

    private Logger logger = null;

    private volatile LoggerStatus status = LoggerStatus.UNINITIALIZE;

    private LoggerConfig config;

    private RunLogger() {
        logger = Logger.getLogger(RunLogger.class.getName());
        logger.setUseParentHandlers(false);
        logger.getParent().setLevel(Level.ALL);
    }

    public static RunLogger getLogger() {
        if (runlogger == null) {
            synchronized (RunLogger.class) {
                if (runlogger == null) {
                    runlogger = new RunLogger();
                }
            }
        }
        return runlogger;
    }

    public synchronized void init(LoggerConfig config) {

        if (LoggerStatus.UNINITIALIZE != status
                && LoggerStatus.TEMP_ENABLED != status) {
            throw new LoggerSystemException(
                    "could not execute Logger init method,RunLogger's current status is "
                            + status);
        }
        try {
            // 移除已注册的Handler
            Handler[] handls = logger.getHandlers();
            if (handls != null) {
                for (Handler h : handls) {
                    logger.removeHandler(h);
                }
            }

            logger.setLevel(config.getLevel());

            // 是否当前日志需要输出至文件,则先检查日志文件存放目录是否存在
            if (config.isLog2File() || config.isErr2File()
                    || config.isOut2File()) {
                File file = new File(config.getLogDir());
                if (!file.isDirectory()) {
                    file.mkdirs();
                }
            }
            // 设置日志文件Handler
            if (config.isLog2File()) {
                logger.addHandler(config.getLogFileHandler());
            }

            // 设置控制台日志Handler
            if (config.isLog2console()) {
                logger.addHandler(config.getConsoleHandler());
            }
            // 设置运行时异常的Handler
            if (config.isErr2File()) {
                logger.addHandler(config.getErrorFileHandler());
            }

            // 设置System.out的Handler
            if (config.isOut2File()) {
                logger.addHandler(config.getOutFileHandler());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.config = config;
        status = LoggerStatus.ENABLED;
    }

    public void log(Throwable thrown) {

        log(Level.WARNING, thrown.getMessage(), thrown);
    }

    public void log(Level level, String msg) {

        log(level, msg, (Throwable) null);
    }

    public void log(Level level, String msg, Throwable thrown) {
        // 使用未初始化的日至系统时,自动提供一个临时配置
        if (status == LoggerStatus.UNINITIALIZE) {
            setInnerLoggerCfg();
            status = LoggerStatus.TEMP_ENABLED;
        }
        if (status != LoggerStatus.ENABLED
                && status != LoggerStatus.TEMP_ENABLED) {
            throw new LoggerSystemException("RunLogg's status is " + status);
        }
        LogRecord record = new LogRecord(level, null);
        record.setMessage(msg);
        record.setThrown(thrown);
        logger.log(record);

    }

    /**
     * 内置一个日至系统配置
     */
    private void setInnerLoggerCfg() {
        LoggerConfig cfg = new LoggerConfig();
        cfg.setLog2console(true);
        cfg.setLevel(Level.ALL);
        init(cfg);
    }

    /**
     * 获取当前日志系统的日志级别
     *
     * @return
     */
    public Level getLoggerLevel() {
        return config.getLevel();
    }

}
