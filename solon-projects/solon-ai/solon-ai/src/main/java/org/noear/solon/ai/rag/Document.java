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
    protected final String content;
    protected final Map<String, Object> metadata;
    protected final double score;

    private float[] embedding;

    public Document() {
        this("");
    }

    public Document(String content) {
        this(content, null);
    }

    public Document(String content, Map<String, Object> metadata) {
        this(null, content, metadata);
    }

    public Document(String id, String content, Map<String, Object> metadata) {
        this(id, content, metadata, 0.0D);
    }

    public Document(String id, String content, Map<String, Object> metadata, double score) {
//      this.id = (id == null ? Utils.guid() : id);
//    	id不在document创建时初始化，而是在store方法调用时，作为判断依据，如果id为空就表示insert，如果id不为空就update
    	this.id = id;
        this.content = content;
        this.metadata = (metadata == null ? new HashMap<>() : metadata);
        this.score = score;
    }

    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }

    /**
     * id
     */
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
    	this.id = id;
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
    public float[] getEmbedding() {
        return embedding;
    }

    /**
     * 评分
     */
    public double getScore() {
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
                '}';
    }

    /// /////////////////////////////

    public Document addMetadata(String key, Object value) {
        this.metadata.put(key, value);
        return this;
    }

    public Document title(String title) {
        return addMetadata("title", title);
    }
    
    public String getTitle() {
    	if(this.metadata!=null) {
    		return (String) this.metadata.get("title");
    	}else {
    		return null;
    	}
    }

    public Document url(String url) {
        return addMetadata("url", url);
    }
    
    public String getUrl() {
    	if(this.metadata!=null) {
    		return (String) this.metadata.get("url");
    	}else {
    		return null;
    	}
    }

    public Document snippet(String snippet) {
        return addMetadata("snippet", snippet);
    }
    
    public String getSnippet() {
    	if(this.metadata!=null) {
    		return (String) this.metadata.get("snippet");
    	}else {
    		return null;
    	}
    }
}