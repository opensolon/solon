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
package org.noear.solon.ai.rag;

import org.noear.solon.Utils;
import org.noear.solon.ai.chat.message.ChatMessage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * 提示语工具
 *
 * @author noear
 * @since 3.1
 */
public final class Prompts {
    /**
     * 增强提示语
     */
    public static ChatMessage augment(String message, Document... context) {
        return augment(message, Arrays.asList(context));
    }

    /**
     * 增强提示语
     */
    public static ChatMessage augment(String message, List<Document> context) {
        return ChatMessage.ofUser(format(message, context));
    }

    /**
     * 增强提示语
     */
    private static String format(String message, List<Document> context) {
        if (Utils.isEmpty(context)) {
            return null;
        }


        return String.format("%s\n\n now: %s \n\n context: %s", message,
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME), context);
    }
}