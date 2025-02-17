package org.noear.solon.ai.embedding;

import org.noear.solon.Utils;
import org.noear.solon.net.http.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author noear
 * @since 3.1
 */
public class EmbeddingRequestDefault implements EmbeddingRequest {
    private static final Logger log = LoggerFactory.getLogger(EmbeddingRequestDefault.class);
    private static final EmbeddingOptions OPTIONS_DEFAULT = new EmbeddingOptions();

    private final EmbeddingConfig config;
    private EmbeddingOptions options;
    private List<String> input;

    public EmbeddingRequestDefault(EmbeddingConfig config, List<String> input) {
        this.config = config;
        this.input = input;
        this.options = OPTIONS_DEFAULT;
    }

    public EmbeddingRequestDefault options(EmbeddingOptions options) {
        if (options != null) {
            this.options = options;
        }

        return this;
    }

    @Override
    public EmbeddingRequest options(Consumer<EmbeddingOptions> optionsBuilder) {
        this.options = EmbeddingOptions.of();
        optionsBuilder.accept(options);
        return this;
    }

    /**
     * 调用
     */
    @Override
    public EmbeddingResponse call() throws IOException {
        HttpUtils httpUtils = buildReqHttp();

        String reqJson = config.dialect().buildRequestJson(config, options, input);

        if (log.isTraceEnabled()) {
            log.trace("ai-request: {}", reqJson);
        }

        String respJson = httpUtils.bodyOfJson(reqJson).post();

        if (log.isTraceEnabled()) {
            log.trace("ai-response: {}", respJson);
        }

        EmbeddingResponse resp = config.dialect().parseResponseJson(config, respJson);

        return resp;
    }

    private HttpUtils buildReqHttp() {
        HttpUtils httpUtils = HttpUtils
                .http(config.apiUrl())
                .timeout((int) config.timeout().getSeconds());

        if (Utils.isNotEmpty(config.apiKey())) {
            httpUtils.header("Authorization", "Bearer " + config.apiKey());
        }

        httpUtils.headers(config.headers());

        return httpUtils;
    }
}