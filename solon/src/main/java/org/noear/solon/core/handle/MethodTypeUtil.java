/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
