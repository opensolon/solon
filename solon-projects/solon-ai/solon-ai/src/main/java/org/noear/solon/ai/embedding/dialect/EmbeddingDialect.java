package org.noear.solon.ai.embedding.dialect;

import org.noear.solon.ai.embedding.EmbeddingConfig;
import org.noear.solon.ai.embedding.EmbeddingOptions;
import org.noear.solon.ai.embedding.EmbeddingResponse;

import java.util.List;

/**
 * 内嵌方言
 *
 * @author noear
 * @since 3.1
 */
public interface EmbeddingDialect {
    /**
     * 匹配检测
     *
     * @param config 聊天配置
     */
    boolean matched(EmbeddingConfig config);

    /**
     * 构建请求数据
     *
     * @param config  聊天配置
     * @param options 聊天选项
     */
    String buildRequestJson(EmbeddingConfig config, EmbeddingOptions options, List<String> input);

    /**
     * 分析响应数据
     *
     * @param config 聊天配置
     */
    EmbeddingResponse parseResponseJson(EmbeddingConfig config, String respJson);
}
