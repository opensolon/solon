package org.noear.solon.serialization.snack3;

import org.noear.snack.ONode;
import org.noear.snack.core.Constants;
import org.noear.solon.serialization.StringSerializer;

import java.io.IOException;

/**
 * @author noear
 * @since 1.5
 */
public class SnackSerializer implements StringSerializer {
    final Constants config;

    public SnackSerializer(Constants config) {
        this.config = config;
    }

    @Override
    public String serialize(Object obj) throws IOException {
        return ONode.loadObj(obj, config).toJson();
    }
}
