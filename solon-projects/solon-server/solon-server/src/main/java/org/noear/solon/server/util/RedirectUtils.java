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
package org.noear.solon.server.util;

import org.noear.solon.Solon;
import org.noear.solon.Utils;

/**
 * Web 跳转工具
 *
 * @author noear
 * @since 1.11
 */
public class RedirectUtils {
    /**
     * 获取跳转地址
     */
    public static String getRedirectPath(String location) {
        if (Utils.isEmpty(Solon.cfg().serverContextPath())) {
            return location;
        }

        if (location.startsWith("/")) {
            if (location.startsWith(Solon.cfg().serverContextPath())) {
                return location;
            } else {
                return Solon.cfg().serverContextPath() + location.substring(1);
            }
        } else {
            return location;
        }
    }
}
