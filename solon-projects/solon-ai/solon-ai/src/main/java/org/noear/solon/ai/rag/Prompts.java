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
    public static ChatMessage augment(State state) {
        return augment(state.question, state.context);
    }

    /**
     * 增强提示语
     */
    public static ChatMessage augment(String question, Document... context) {
        return augment(question, Arrays.asList(context));
    }

    public static ChatMessage augment(String question, List<Document> context) {
        return ChatMessage.ofUser(format(question, context));
    }

    /**
     * 增强提示语
     */
    private static String format(String question, List<Document> context) {
        if (Utils.isEmpty(context)) {
            return null;
        }

        StringBuilder results = new StringBuilder();

        for (Document d1 : context) {
            results.append("Title: ").append(d1.getTitle()).append("\n");

            if (Utils.isNotEmpty(d1.getUrl())) {
                results.append("Source: ").append(d1.getUrl()).append("\n");
            }

            if (Utils.isNotEmpty(d1.getSummary())) {
                results.append("Content: ").append(d1.getSummary()).append("\n");
            }

            if (Utils.isNotEmpty(d1.getSnippet())) {
                results.append("Snippet: ").append(d1.getSnippet()).append("\n");
            }

            if (Utils.isNotEmpty(d1.getLanguage())) {
                results.append("Language: ").append(d1.getLanguage()).append("\n");
            }

            results.append("\n\n");
        }

        return String.format("question: %s\n\n now:%s \n\n context:%s", question,
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME), results);
    }
}
