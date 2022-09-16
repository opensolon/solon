package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

/**
 * @author noear
 * @since 1.0
 */
public enum SolonLoggerFactory implements ILoggerFactory {
    /**
     * 工厂单例
     */
    INSTANCE;

    SolonLoggerFactory() {

    }

    @Override
    public Logger getLogger(String name) {
        return new SolonLogger(name);
    }

}
