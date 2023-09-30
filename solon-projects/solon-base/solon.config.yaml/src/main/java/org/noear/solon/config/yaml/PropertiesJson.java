package org.noear.solon.config.yaml;

import org.noear.snack.ONode;

import java.io.IOException;
import java.util.Properties;

/**
 * Json 属性
 *
 * @author noear
 * @since 1.5
 * */
public class PropertiesJson extends Properties {
    public synchronized void loadJson(String text) throws IOException {
        ONode.loadStr(text).bindTo(this);
    }
}
