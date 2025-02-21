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
package org.noear.solon.ai.rag.loader;

import org.jsoup.Jsoup;
import org.noear.solon.Utils;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.DocumentLoader;

import java.util.Arrays;
import java.util.List;

/**
 * Html 文档加载器
 *
 * @author noear
 * @since 3.1
 */
public class HtmlSimpleLoader implements DocumentLoader {
    private final String html;
    private final String baseUri;

    public HtmlSimpleLoader(String html) {
        this(html, null);
    }

    public HtmlSimpleLoader(String html, String baseUri) {
        this.html = html;
        this.baseUri = baseUri;
    }

    @Override
    public List<Document> load() {
        String text;

        if (Utils.isEmpty(baseUri)) {
            text = Jsoup.parse(html).text();
        } else {
            text = Jsoup.parse(html, baseUri).text();
        }

        return Arrays.asList(new Document(text));
    }
}
