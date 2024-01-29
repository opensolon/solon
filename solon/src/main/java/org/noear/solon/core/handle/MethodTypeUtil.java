package org.noear.solon.core.handle;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.3
 */
public class MethodTypeUtil {
    static final Map<String, MethodType> enumMap = new HashMap<>();

    static {
        MethodType[] enumOrdinal = MethodType.class.getEnumConstants();

        for (int i = 0; i < enumOrdinal.length; ++i) {
            MethodType e = enumOrdinal[i];

            enumMap.put(e.name(), e);
        }
    }

    public static MethodType valueOf(String name) {
        MethodType tmp = enumMap.get(name);

        if (tmp == null) {
            return MethodType.UNKNOWN;
        } else {
            return tmp;
        }
    }
}
