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

import org.noear.solon.ai.rag.Document;

import java.util.function.Predicate;

/**
 * 搜索条件
 *
 * @author noear
 * @since 3.1
 */
public class SearchCondition {
    private final String query;
    private int limit = 4;
    private double similarityThreshold = 0.4D;
    private Predicate<Document> filter = (doc) -> true;

    public SearchCondition(String query) {
        this.query = query;
    }

    /// /////////////////

    /**
     * 获取查询字符串
     */
    public String getQuery() {
        return query;
    }

    /**
     * 获取限制条数
     */
    public int getLimit() {
        return limit;
    }

    /**
     * 获取相似度阈值
     */
    public double getSimilarityThreshold() {
        return similarityThreshold;
    }

    /**
     * 获取过滤器
     */
    public Predicate<Document> getFilter() {
        return filter;
    }

    /// /////////////////

    /**
     * 配置限制条数
     */
    public SearchCondition limit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * 配置过滤器
     */
    public SearchCondition filter(Predicate<Document> filter) {
        this.filter = filter;
        return this;
    }

    /**
     * 配置相似度阈值
     */
    public SearchCondition similarityThreshold(double similarityThreshold) {
        this.similarityThreshold = similarityThreshold;
        return this;
    }
}
