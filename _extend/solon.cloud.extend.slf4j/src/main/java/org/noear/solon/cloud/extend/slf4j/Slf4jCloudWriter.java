package org.noear.solon.cloud.extend.slf4j;


import org.noear.solon.cloud.model.Level;

public interface Slf4jCloudWriter {
    void write(String name, Level level, String content);
}
