package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

/**
 * @author noear 2023/5/22 created
 */
public class SolonServiceProvider implements SLF4JServiceProvider {
    @Override
    public ILoggerFactory getLoggerFactory() {
        return SolonLoggerFactory.INSTANCE;
    }

    @Override
    public IMarkerFactory getMarkerFactory() {
        return StaticMarkerBinder.getSingleton().getMarkerFactory();
    }

    @Override
    public MDCAdapter getMDCAdapter() {
        return StaticMDCBinder.getSingleton().getMDCA();
    }

    @Override
    public String getRequestedApiVersion() {
        return "2.0.99";
    }

    @Override
    public void initialize() {

    }
}
