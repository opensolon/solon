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
import org.noear.solon.net.http.HttpUtils;

import java.io.IOException;
import java.util.*;

/**
 * 网页搜索知识库（基于搜索接口实现）
 *
 * @author noear
 * @since 3.1
 */
public class WebSearchRepository implements Repository {
    private final AiConfig config;
    private EmbeddingModel embeddingModel;

    /**
     * 设置嵌入模型
     * */
    public void setEmbeddingModel(EmbeddingModel embeddingModel) {
        //可选
        this.embeddingModel = embeddingModel;
    }

    public WebSearchRepository(String apiKey) {
        this("https://api.bochaai.com/v1/web-search", apiKey);
    }

    public WebSearchRepository(String apiUrl, String apiKey) {
        this.config = new AiConfig();
        this.config.setApiUrl(apiUrl);
        this.config.setApiKey(apiKey);
    }

    public WebSearchRepository(AiConfig config) {
        this.config = config;
    }

    @Override
    public List<Document> search(SearchCondition condition) throws IOException {
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

        if(embeddingModel != null){
            //如果有嵌入模型设置，则做相互度排序和二次过滤
            embeddingModel.embed(docs);

            return SearchUtil.filter(condition, embeddingModel, docs);
        }

        return docs;
    }

}
