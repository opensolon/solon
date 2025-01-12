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
package org.noear.solon.validation.util;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Result;

import java.lang.annotation.Annotation;

/**
 * @author noear
 * @since 2.8
 */
public class FormatUtils {
    /**
     * 格式化验证消息
     */
    public static String format(Annotation anno, Result rst, String msg) {
        if (Utils.isEmpty(msg)) {
            if (anno != null) {
                if (Utils.isEmpty(rst.getDescription())) {
                    msg = new StringBuilder(100)
                            .append("@")
                            .append(anno.annotationType().getSimpleName())
                            .append(" verification failed")
                            .toString();
                } else {
                    msg = new StringBuilder(100)
                            .append("@")
                            .append(anno.annotationType().getSimpleName())
                            .append(" verification failed: ")
                            .append(rst.getDescription())
                            .toString();
                }
            }
        }

        return msg;
    }
}