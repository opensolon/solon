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
package org.noear.solon.ai.image;

import org.noear.solon.ai.AiModel;
import org.noear.solon.ai.image.dialect.ImageDialect;
import org.noear.solon.ai.image.dialect.ImageDialectManager;
import org.noear.solon.lang.Preview;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * 图像模型
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public class ImageModel implements AiModel {
    private final ImageConfig config;
    private final ImageDialect dialect;

    protected ImageModel(ImageConfig config) {
        this.dialect = ImageDialectManager.select(config);
        this.config = config;
    }


    /**
     * 输入
     */
    public ImageRequest prompt(String prompt) {
        return new ImageRequest(config, dialect, prompt);
    }


    /**
     * 构建
     */
    public static Builder of(ImageConfig config) {
        return new Builder(config);
    }

    /**
     * 构建
     */
    public static Builder of(String apiUrl) {
        return new Builder(apiUrl);
    }

    /// /////////////

    /**
     * 嵌入模型构建器实现
     *
     * @author noear
     * @since 3.1
     */
    public static class Builder {
        private final ImageConfig config;

        public Builder(String apiUrl) {
            this.config = new ImageConfig();
            this.config.setApiUrl(apiUrl);
        }

        public Builder(ImageConfig config) {
            this.config = config;
        }

        public Builder apiKey(String apiKey) {
            config.setApiKey(apiKey);
            return this;
        }

        public Builder provider(String provider) {
            config.setProvider(provider);
            return this;
        }

        public Builder model(String model) {
            config.setModel(model);
            return this;
        }

        public Builder headerSet(String key, String value) {
            config.setHeader(key, value);
            return this;
        }

        public Builder timeout(Duration timeout) {
            config.setTimeout(timeout);
            return this;
        }

        public ImageModel build() {
            return new ImageModel(config);
        }
    }
}