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
package org.noear.solon.ai.integration;

import org.noear.solon.ai.chat.dialect.ChatDialect;
import org.noear.solon.ai.chat.dialect.ChatDialectManager;
import org.noear.solon.ai.embedding.dialect.EmbeddingDialect;
import org.noear.solon.ai.embedding.dialect.EmbeddingDialectManager;
import org.noear.solon.ai.image.dialect.ImageDialect;
import org.noear.solon.ai.image.dialect.ImageDialectManager;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 3.1
 */
public class AiPlugin implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        context.subBeansOfType(ChatDialect.class, bean -> {
            ChatDialectManager.register(bean);
        });

        context.subBeansOfType(EmbeddingDialect.class, bean -> {
            EmbeddingDialectManager.register(bean);
        });

        context.subBeansOfType(ImageDialect.class, bean -> {
            ImageDialectManager.register(bean);
        });
    }
}
