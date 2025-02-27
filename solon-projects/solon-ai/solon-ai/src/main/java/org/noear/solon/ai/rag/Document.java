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

import org.noear.solon.lang.Preview;

import java.util.HashMap;
import java.util.Map;

/**
 * 文档
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public class Document {
    protected String id;
    protected String content;
    protected final Map<String, Object> metadata;
    protected final transient double score; //不进行序列化（搜索时动态产生）

    private float[] embedding;

    public Document() {
        this("");
    }

    public Document(String content) {
        this(content, null);
    }

    public Document(String content, Map<String, Object> metadata) {
        this(null, content, metadata, 0.0D);
    }

    public Document(String id, String content, Map<String, Object> metadata, double score) {
        this.id = id;
        this.content = content;
        this.metadata = (metadata == null ? new HashMap<>() : metadata);
        this.score = score;
    }

    /**
     * 设置 id
     */
    public Document id(String id) {
        this.id = id;
        return this;
    }

    /**
     * 获取 id
     */
    public String getId() {
        return id;
    }

    /**
     * 设置内容
     */
    public Document content(String content) {
        this.content = content;
        return this;
    }

    /**
     * 获取内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置嵌入矢量
     */
    public Document embedding(float[] embedding) {
        this.embedding = embedding;
        return this;
    }

    /**
     * 设置嵌入矢量
     */
    public float[] getEmbedding() {
        return embedding;
    }

    /**
     * 获取评分
     */
    public double getScore() {
        return score;
    }

    /**
     * 获取元数据
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
                '}';
    }

    /// /////////////////////////////

    /**
     * 添加元数据
     */
    public Document metadata(String key, Object value) {
        if (value != null) {
            this.metadata.put(key, value);
        }
        return this;
    }

    /**
     * 添加元数据
     */
    public Document metadata(Map<String, Object> metadata) {
        this.metadata.putAll(metadata);
        return this;
    }

    /**
     * 获取元数据
     */
    public Object getMetadata(String key) {
        if (this.metadata == null) {
            return null;
        } else {
            return this.metadata.get(key);
        }
    }

    /// /////////////////////////////
    /**
     * 标题（可选）
     */
    public Document title(String title) {
        return metadata("title", title);
    }

    /**
     * 获取标题
     */
    public String getTitle() {
        return (String) getMetadata("title");
    }

    /**
     * 资源地址（可选）
     */
    public Document url(String url) {
        return metadata("url", url);
    }

    /**
     * 获取资源地址
     */
    public String getUrl() {
        return (String) getMetadata("url");
    }

    /**
     * 摘要（可选）
     */
    public Document summary(String summary) {
        return metadata("summary", summary);
    }

    /**
     * 获取摘要
     */
    public String getSummary() {
        return (String) getMetadata("summary");
    }
}