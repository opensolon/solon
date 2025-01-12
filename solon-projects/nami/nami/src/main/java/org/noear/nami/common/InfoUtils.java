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
package org.noear.nami.common;

import org.noear.nami.annotation.NamiClient;
import org.noear.solon.Utils;
import org.noear.solon.core.util.LogUtil;

/**
 * 打印信息
 *
 * @author noear
 * @since 1.2
 * */
public class InfoUtils {
    public static void print(Class<?> type, NamiClient anno) {
        StringBuilder buf = new StringBuilder();
        buf.append("Bind the service ")
                .append(type.getTypeName());

        if (Utils.isNotEmpty(anno.url())) {
            buf.append(" to url(").append(anno.url()).append(")");
        }

        if (Utils.isNotEmpty(anno.name())) {
            buf.append(" to upstream(").append(anno.name()).append(")");
        }

        if (anno.upstream().length > 0) {
            buf.append(" to upstream(");
            for (String url : anno.upstream()) {
                buf.append(url).append(",");
            }
            buf.setLength(buf.length() - 1);
            buf.append(")");
        }

        LogUtil.global().info("Nami: " + buf);
    }
}
