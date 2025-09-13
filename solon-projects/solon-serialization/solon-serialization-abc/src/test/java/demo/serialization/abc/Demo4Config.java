package demo.serialization.abc;

import org.noear.solon.annotation.Configuration;
import org.noear.solon.serialization.abc.AbcBytesSerializer;

/**
 *
 * @author noear 2025/9/13 created
 *
 */
@Configuration
public class Demo4Config {
    public void config(AbcBytesSerializer serializer) throws Exception {
        serializer.bodyRequired();
    }
}