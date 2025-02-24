package org.noear.solon.ai.rag.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.noear.solon.ai.rag.util.QueryCondition;

/**
 * Elasticsearch 查询转换器
 *
 * @author 小奶奶花生米
 * @since 3.1
 */
public class ElasticsearchQueryTranslator {
    /**
     * 转换查询条件为 ES 查询语句
     */
    public Map<String, Object> translate(QueryCondition condition) {
        Map<String, Object> query = new HashMap<>();
        Map<String, Object> bool = new HashMap<>();
        List<Map<String, Object>> must = new ArrayList<>();

        // 构建文本查询
        if (condition.getQuery() != null && !condition.getQuery().isEmpty()) {
            Map<String, Object> match = new HashMap<>();
            Map<String, Object> matchQuery = new HashMap<>();
            matchQuery.put("content", condition.getQuery());
            match.put("match", matchQuery);
            must.add(match);
        } else {
            // 空查询时返回所有文档
            Map<String, Object> matchAll = new HashMap<>();
            matchAll.put("match_all", new HashMap<>());
            must.add(matchAll);
        }

        // 构建过滤条件
        if (condition.getFilter() != null) {
            Map<String, Object> filter = new HashMap<>();
            filter.put("match_all", new HashMap<>());
            must.add(filter);
        }

        bool.put("must", must);
        query.put("bool", bool);

        return query;
    }
}
