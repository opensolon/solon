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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import org.jsoup.Jsoup;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.core.util.SupplierEx;
import org.noear.solon.lang.Preview;

/**
 * 简单html加载器
 *
 * @author 小奶奶花生米
 * @since 3.1
 */
@Preview("3.1")
public class HtmlSimpleLoader extends AbstractOptionsDocumentLoader<HtmlSimpleLoader.Options, HtmlSimpleLoader> {
    private final SupplierEx<InputStream> source;

    public HtmlSimpleLoader(byte[] source) {
        this(() -> new ByteArrayInputStream(source));
    }

    public HtmlSimpleLoader(URL source) {
        this(() -> source.openStream());
    }

    public HtmlSimpleLoader(SupplierEx<InputStream> source) {
        this.source = source;
        this.options = new Options();
    }

    @Override
    public List<Document> load() throws IOException {
        try (InputStream stream = source.get()) {
            org.jsoup.nodes.Document soup = Jsoup.parse(stream, options.charset, options.baseUri);
            String text = soup.body().text();
            Map<String, Object> metadata = buildMetadata(soup);
            return Arrays.asList(new Document(text, metadata).metadata(this.additionalMetadata));
        } catch (IOException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 从HTML文档中提取元数据
     */
    private Map<String, Object> buildMetadata(org.jsoup.nodes.Document soup) {
        Map<String, Object> metadata = new HashMap<>();

        String title = soup.title();
        if (!Utils.isEmpty(title)) {
            metadata.put("title", title);
        }

        String description = soup.select("meta[name=description]").attr("content");
        if (!Utils.isEmpty(description)) {
            metadata.put("description", description);
        }

        String language = soup.select("html").attr("lang");
        if (!Utils.isEmpty(language)) {
            metadata.put("language", language);
        }

        return metadata;
    }

    public static class Options {
        private String charset = Solon.encoding();
        private String baseUri = "";

        public Options charset(String charset) {
            this.charset = charset;
            return this;
        }

        public Options baseUri(String baseUri) {
            if (baseUri != null) {
                this.baseUri = baseUri;
            }
            return this;
        }
    }
}