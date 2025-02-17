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
import org.noear.solon.lang.Preview;

import java.util.List;

/**
 * 文档
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public class Document {
    private String id;
    private String content;
    private List<Float> embedding;

    public static Document of(String content) {
        Document doc = new Document();
        doc.content = content;
        doc.id = Utils.guid();
        return doc;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setEmbedding(List<Float> embedding) {
        this.embedding = embedding;
    }

    /**
     * id
     */
    public String getId() {
        return id;
    }

    /**
     * 内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 嵌入
     */
    public List<Float> getEmbedding() {
        return embedding;
    }
}