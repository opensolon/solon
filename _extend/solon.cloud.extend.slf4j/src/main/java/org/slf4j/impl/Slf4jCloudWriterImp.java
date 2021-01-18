package org.slf4j.impl;


import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.model.Level;

/**
 * @author noear
 * @since 1.2
 * */
public class Slf4jCloudWriterImp implements Slf4jCloudWriter {
    @Override
    public void write(String name, Level level, String content) {
        if (CloudClient.log() != null) {
            CloudClient.log().write(name, level, content);
        }
    }
}
