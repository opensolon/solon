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
package org.noear.solon.ai.embedding;

import org.noear.solon.ai.embedding.dialect.EmbeddingDialect;
import org.noear.solon.net.http.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * 嵌入请求
 *
 * @author noear
 * @since 3.1
 */
public class EmbeddingRequest {
    private static final Logger log = LoggerFactory.getLogger(EmbeddingRequest.class);
    private static final EmbeddingOptions OPTIONS_DEFAULT = new EmbeddingOptions();

    private final EmbeddingConfig config;
    private final EmbeddingDialect dialect;
    private final List<String> input;
    private EmbeddingOptions options;

    protected EmbeddingRequest(EmbeddingConfig config, EmbeddingDialect dialect, List<String> input) {
        this.config = config;
        this.dialect = dialect;
        this.input = input;
        this.options = OPTIONS_DEFAULT;
    }

    /**
     * 选项
     */
    public EmbeddingRequest options(EmbeddingOptions options) {
        if (options != null) {
            this.options = options;
        }

        return this;
    }

    /**
     * 选项
     */
    public EmbeddingRequest options(Consumer<EmbeddingOptions> optionsBuilder) {
        this.options = EmbeddingOptions.of();
        optionsBuilder.accept(options);
        return this;
    }

    /**
     * 调用
     */
    public EmbeddingResponse call() throws IOException {
        HttpUtils httpUtils = config.createHttpUtils();

        String reqJson = dialect.buildRequestJson(config, options, input);

        if (log.isTraceEnabled()) {
            log.trace("ai-request: {}", reqJson);
        }

        String respJson = httpUtils.bodyOfJson(reqJson).post();

        if (log.isTraceEnabled()) {
            log.trace("ai-response: {}", respJson);
        }

        EmbeddingResponse resp = dialect.parseResponseJson(config, respJson);

        return resp;
    }
}