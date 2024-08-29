/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.boot.web;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;

/**
 * 头处理工具类
 *
 * @author noear
 * @since 1.11
 */
public class HeaderUtils {
    /**
     * 提取内容长度
     * */
    public static int getContentLength(Context ctx) {
        long length = getContentLengthLong(ctx);
        return length > 2147483647L ? -1 : (int)length;
    }

    /**
     * 提取内容长度
     * */
    public static long getContentLengthLong(Context ctx) {
        String contentLength = ctx.header("Content-Length");
        return contentLength != null && !contentLength.isEmpty() ? Long.parseLong(contentLength) : -1L;
    }

    /**
     * 提取头信息中的分段值（例：Content-Type:text/json;charset=utf-8）
     * */
    public static String extractQuotedValueFromHeader(String header, String key) {
        if(Utils.isEmpty(header)){
            return null;
        }

        int keypos = 0;
        int pos = -1;
        boolean whiteSpace = true;
        boolean inQuotes = false;

        int i;
        int start;
        for(i = 0; i < header.length() - 1; ++i) {
            start = header.charAt(i);
            if (inQuotes) {
                if (start == 34) {
                    inQuotes = false;
                }
            } else {
                if (key.charAt(keypos) == start && (whiteSpace || keypos > 0)) {
                    ++keypos;
                    whiteSpace = false;
                } else if (start == 34) {
                    keypos = 0;
                    inQuotes = true;
                    whiteSpace = false;
                } else {
                    keypos = 0;
                    whiteSpace = start == 32 || start == 59 || start == 9;
                }

                if (keypos == key.length()) {
                    if (header.charAt(i + 1) == '=') {
                        pos = i + 2;
                        break;
                    }

                    keypos = 0;
                }
            }
        }

        if (pos == -1) {
            return null;
        } else {
            char c;
            if (header.charAt(pos) == '"') {
                start = pos + 1;

                for(i = start; i < header.length(); ++i) {
                    c = header.charAt(i);
                    if (c == '"') {
                        break;
                    }
                }

                return header.substring(start, i);
            } else {
                for(i = pos; i < header.length(); ++i) {
                    c = header.charAt(i);
                    if (c == ' ' || c == '\t' || c == ';') {
                        break;
                    }
                }

                return header.substring(pos, i);
            }
        }
    }
}
