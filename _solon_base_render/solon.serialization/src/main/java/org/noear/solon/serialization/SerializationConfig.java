package org.noear.solon.serialization;

import org.noear.solon.Solon;

/**
 * @author noear
 * @since 2.2
 */
public class SerializationConfig {

    private static boolean outputMeta;

    static {
        outputMeta = Solon.cfg().getInt("solon.output.meta", 0) > 0;
    }

    /**
     * 是否输出元信息
     * */
    public static boolean isOutputMeta() {
        return outputMeta;
    }
}
