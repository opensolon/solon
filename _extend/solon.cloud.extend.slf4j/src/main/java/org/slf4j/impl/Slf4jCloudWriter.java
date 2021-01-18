package org.slf4j.impl;


import org.noear.solon.cloud.model.Level;

/**
 * @author noear
 * @since 1.2
 * */
public interface Slf4jCloudWriter {
    void write(String name, Level level, String content);
}
