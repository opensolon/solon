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

import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.DocumentSplitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Json 文档分割器（将数组数据分割为不同的文档）
 *
 * @author noear
 * @since 3.1
 */
public class JsonSplitter implements DocumentSplitter {
    private List<String> contentSelector;
    private Function<Map<String, Object>, Map<String, Object>> metadataMapper;

    /**
     * 内容选择器
     */
    public JsonSplitter contentSelector(String... keys) {
        this.contentSelector = Arrays.asList(keys);
        return this;
    }

    /**
     * 元数据映射器（将内容数据转为元数据）
     */
    public JsonSplitter metadataMapper(Function<Map<String, Object>, Map<String, Object>> mapper) {
        this.metadataMapper = mapper;
        return this;
    }

    @Override
    public List<Document> split(List<Document> documents) {
        List<Document> outs = new ArrayList<>();

        for (Document doc : documents) {
            splitDocument(doc, outs);
        }

        return outs;
    }

    /**
     * 分割文档
     */
    protected List<Document> splitDocument(Document in, List<Document> outs) {
        for (Document doc : splitJson(in.getContent())) {
            //加载父元信息
            in.getMetadata().forEach((k, v) -> {
                doc.getMetadata().putIfAbsent(k, v);
            });

            outs.add(doc);
        }

        return outs;
    }

    /**
     * 分割 json text
     */
    protected List<Document> splitJson(String josn) {
        List<Document> tmp = new ArrayList<>();

        ONode oNode = ONode.load(josn);
        if (oNode.isArray()) {
            for (ONode n1 : oNode.ary()) {
                splitJsonNode(n1, tmp);
            }
        } else {
            splitJsonNode(oNode, tmp);
        }

        return tmp;
    }

    /**
     * 分割 json node
     */
    protected void splitJsonNode(ONode oNode, List<Document> docs) {
        if (oNode.isArray()) {
            for (ONode n1 : oNode.ary()) {
                splitJsonNode(n1, docs);
            }
        } else if (oNode.isObject()) {
            Map<String, Object> jsonData = oNode.toObject(Map.class);

            docs.add(new Document(buildContent(jsonData), buildMetadata(jsonData)));
        }
    }

    /**
     * 构建内容
     */
    protected String buildContent(Map<String, Object> jsonData) {
        StringBuilder buf = new StringBuilder();

        if (Utils.isEmpty(contentSelector)) {
            for (Map.Entry<String, Object> kv : jsonData.entrySet()) {
                buf.append(kv.getKey()).append(": ").append(kv.getValue()).append("\n");
            }
        } else {
            for (String key : contentSelector) {
                buf.append(key).append(": ").append(jsonData.get(key)).append("\n");
            }
        }

        return buf.toString();
    }

    /**
     * 构建元数据
     */
    protected Map<String, Object> buildMetadata(Map<String, Object> jsonData) {
        Map<String, Object> metadata = null;
        if (metadataMapper != null) {
            metadata = metadataMapper.apply(jsonData);
        }

        return metadata;
    }
}