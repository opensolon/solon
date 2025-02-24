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
package org.noear.solon.ai.rag.loader;

import org.noear.solon.ai.rag.Document;
import org.noear.solon.core.util.ResourceUtil;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Text 文档加载器
 *
 * @author noear
 * @since 3.1
 */
public class TextLoader extends AbstractDocumentLoader {
    private final URL url;

    public TextLoader(File file) throws IOException {
        this(file.toURI());
    }

    public TextLoader(URI uri) throws IOException {
        this(uri.toURL());
    }

    public TextLoader(URL url) {
        this.url = url;
    }

    @Override
    public List<Document> load() {
        try {
            String temp = ResourceUtil.getResourceAsString(url);
            return Arrays.asList(new Document(temp).metadata(additionalMetadata));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
