package org.noear.solon.ext;

import org.noear.solon.XApp;

@FunctionalInterface
public interface Builder {
    void build(XApp app) throws Throwable;
}
