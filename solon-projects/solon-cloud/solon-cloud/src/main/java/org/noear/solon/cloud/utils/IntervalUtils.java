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
package org.noear.solon.cloud.utils;

import org.noear.solon.Utils;

/**
 * 时间间隔转换工具
 *
 * @author 夜の孤城
 * @since 1.2
 */
public class IntervalUtils {
    /**
     * 获取简隔时间
     * */
    public static int getInterval(String val) {
        if (Utils.isEmpty(val)) {
            return 0;
        }

        if (val.endsWith("ms")) {
            return Integer.parseInt(val.substring(0, val.length() - 2));
        }

        if (val.endsWith("s")) {
            return Integer.parseInt(val.substring(0, val.length() - 1)) * 1000;
        }

        if (val.indexOf("s") < 0) {
            //ms
            return Integer.parseInt(val);
        }

        return 0;
    }
}
