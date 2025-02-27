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
package org.noear.solon.ai.rag.repository;

import org.noear.snack.ONode;
import org.noear.solon.ai.AiConfig;
import org.noear.solon.ai.embedding.EmbeddingModel;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.Repository;
import org.noear.solon.ai.rag.util.QueryCondition;
import org.noear.solon.ai.rag.util.SimilarityUtil;
import org.noear.solon.lang.Nullable;
import org.noear.solon.net.http.HttpUtils;

import java.io.IOException;
import java.util.*;

/**
 * 联网搜索知识库
 *
 * @author noear
 * @since 3.1
 */
public class WebSearchRepository implements Repository {
    private final AiConfig config;
    private final @Nullable EmbeddingModel embeddingModel;

    public WebSearchRepository(AiConfig config) {
        this(null, config);
    }

    public WebSearchRepository(EmbeddingModel embeddingModel, AiConfig config) {
        this.embeddingModel = embeddingModel;
        this.config = config;
    }

    @Override
    public List<Document> search(QueryCondition condition) throws IOException {
        //此示例，可作为对接其它搜索的参考
        HttpUtils httpUtils = config.createHttpUtils();

        ONode reqNode = new ONode();

        reqNode.set("query", condition.getQuery());
        reqNode.set("count", String.valueOf(condition.getLimit()));

        if (condition.getFreshness() != null) {
            reqNode.set("freshness", condition.getFreshness().value);
        }

        String respJson = httpUtils.bodyOfJson(reqNode.toJson())
                .post();

        ONode respNode = ONode.load(respJson);

        int code = respNode.get("code").getInt();
        String msg = respNode.get("msg").getString();

        if (code != 200) {
            throw new IOException(msg);
        }

        List<Document> docs = new ArrayList<>();

        for (ONode n1 : respNode.get("data").get("webPages").get("value").ary()) {
            docs.add(new Document(n1.get("snippet").getString())
                    .title(n1.get("title").getString())
                    .url(n1.get("url").getString()));
        }

        if (embeddingModel != null) {
            //如果有嵌入模型设置，则做相互度排序和二次过滤
            embeddingModel.embed(docs);

            float[] queryEmbed = embeddingModel.embed(condition.getQuery());
            return SimilarityUtil.scoreAndfilter(condition, docs.stream(), queryEmbed);
        } else {
            return docs;
        }
    }

    public static Builder of(String apiUrl) {
        return new Builder(apiUrl);
    }

    /**
     * 构建器
     */
    public static class Builder {
        private AiConfig config = new AiConfig();
        private EmbeddingModel embeddingModel;

        public Builder(String apiUrl) {
            config.setApiUrl(apiUrl);
        }

        public Builder apiKey(String apiKey) {
            config.setApiKey(apiKey);
            return this;
        }

        public Builder embeddingModel(EmbeddingModel embeddingModel) {
            this.embeddingModel = embeddingModel;
            return this;
        }

        public WebSearchRepository build() {
            return new WebSearchRepository(embeddingModel, config);
        }
    }
}