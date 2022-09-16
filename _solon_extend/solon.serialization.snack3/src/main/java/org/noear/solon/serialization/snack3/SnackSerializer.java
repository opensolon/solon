package org.noear.solon.serialization.snack3;

import org.noear.snack.ONode;
import org.noear.snack.core.Options;
import org.noear.solon.serialization.StringSerializer;

import java.io.IOException;

/**
 * Json 序列化器
 *
 * @author noear
 * @since 1.5
 */
public class SnackSerializer implements StringSerializer {
    final Options options;

    public SnackSerializer(Options options) {
        this.options = options;
    }

    @Override
    public String serialize(Object obj) throws IOException {
        return ONode.loadObj(obj, options).toJson();
    }
}
