package org.noear.solon.flow.core;

/**
 * 
 * @author noear 2025/1/11 created
 * */
public interface Line {
    String id();
    String title();
    ElementType type();

    String prveId();
    String nextId();
}
