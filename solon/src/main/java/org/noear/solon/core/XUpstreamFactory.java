package org.noear.solon.core;

@FunctionalInterface
public interface XUpstreamFactory {
    XUpstream create(String service);
}
