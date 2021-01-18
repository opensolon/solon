package org.slf4j.impl;


import org.slf4j.event.Level;

/**
 * @author noear
 * @since 1.2
 * */
public interface Slf4jCloudWriter {
    void write(Level level, String name, String content);
}
