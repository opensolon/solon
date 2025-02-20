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
package org.noear.solon.ai.rag.splitter;

import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.DocumentSplitter;

import java.util.ArrayList;
import java.util.List;

/**
 * 文本分割器
 *
 * @author noear
 * @since 3.1
 */
public abstract class TextSplitter implements DocumentSplitter {

    @Override
    public List<Document> split(List<Document> documents) {
        List<Document> outs = new ArrayList<>();

        for (Document doc : documents) {
            splitDocument(doc, outs);
        }

        return outs;
    }

    protected List<Document> splitDocument(Document in, List<Document> outs) {
        for (String chuck : splitText(in.getContent())) {
            if (chuck.length() > 0) {
                outs.add(new Document(chuck, in.getMetadata()));
            }
        }

        return outs;
    }

    protected abstract List<String> splitText(String text);
}
