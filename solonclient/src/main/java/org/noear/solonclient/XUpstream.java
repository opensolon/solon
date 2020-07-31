package org.noear.solonclient;

@FunctionalInterface
public interface XUpstream {
    default void setBackup(String server){}
    String getServer(String name);
}
