package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

/**
 * @author noear
 * @since 1.0
 */
public enum StaticLoggerBinder implements LoggerFactoryBinder {
    /**
     * Binder 单例
     */
    INSTANCE;

    /**
     * Logger Factory name
     */
    private static final String LOGGER_FACTORY_NAME = SolonLogger.class.getName();

    /**
     * StaticLoggerBinder 单例, slf4j-api 将调用该方法进行实现绑定
     *
     * @return StaticLoggerBinder实例
     * @see LoggerFactory #bind()
     */
    public static StaticLoggerBinder getSingleton() {
        return INSTANCE;
    }

    @Override
    public ILoggerFactory getLoggerFactory() {
        return SolonLoggerFactory.INSTANCE;
    }

    @Override
    public String getLoggerFactoryClassStr() {
        return LOGGER_FACTORY_NAME;
    }
}