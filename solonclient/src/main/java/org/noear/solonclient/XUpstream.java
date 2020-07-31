package org.noear.solonclient;

@FunctionalInterface
public interface XUpstream {
    default void setDefault(String server){}
    String getServer(String name);
}
