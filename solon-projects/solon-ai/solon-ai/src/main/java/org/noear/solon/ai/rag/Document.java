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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文档
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public class Document {
    private final String id;
    private final String content;
    private final Map<String, Object> metadata;
    private final Double score;

    private List<Float> embedding;

    public Document(String content) {
        this(content, null);
    }

    public Document(String content, Map<String, Object> metadata) {
        this(null, content, metadata);
    }

    public Document(String id, String content, Map<String, Object> metadata) {
        this(id, content, metadata, null);
    }

    public Document(String id, String content, Map<String, Object> metadata, Double score) {
        this.id = (id == null ? Utils.guid() : id);
        this.content = content;
        this.metadata = (metadata == null ? new HashMap<>() : metadata);
        this.score = score;
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

    /**
     * 评分
     */
    public Double getScore() {
        return score;
    }

    /**
     * 元数据
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "Document{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", metadata=" + metadata +
                ", score=" + score +
                '}';
    }
}