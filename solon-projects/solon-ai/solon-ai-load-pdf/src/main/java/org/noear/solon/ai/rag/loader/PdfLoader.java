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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.core.util.SupplierEx;
import org.noear.solon.lang.Preview;

/**
 * pdf 文档加载器
 *
 * @author 小奶奶花生米
 * @author noear
 * @since 3.1
 * */
@Preview("3.1")
public class PdfLoader extends AbstractOptionsDocumentLoader<PdfLoader.Options, PdfLoader> {
    /**
     * 文件源
     */
    private final SupplierEx<InputStream> source;


    public PdfLoader(File file) {
        this(() -> new FileInputStream(file));
    }


    public PdfLoader(URL url) {
        this(() -> url.openStream());
    }

    public PdfLoader(SupplierEx<InputStream> source) {
        if (source == null) {
            throw new IllegalArgumentException("Source cannot be null");
        }

        this.source = source;
        this.options = new Options();
    }

    @Override
    public List<Document> load() throws IOException {

        try (InputStream stream = source.get(); PDDocument pdf = PDDocument.load(stream)) {
            List<Document> documents = new ArrayList<>();

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("type", "pdf");

            if (options.loadMode == LoadMode.SINGLE) {
                // 整个文档作为一个 Document
                PDFTextStripper stripper = new PDFTextStripper();
                // 设置页面分隔符
                stripper.setPageEnd(options.pageDelimiter);
                String text = stripper.getText(pdf);

                Document doc = new Document(text, metadata)
                        .metadata(this.additionalMetadata)
                        .metadata("pages", pdf.getNumberOfPages());
                documents.add(doc);

            } else {
                // 每页作为一个 Document
                PDFTextStripper stripper = new PDFTextStripper();
                for (int pageNum = 1; pageNum <= pdf.getNumberOfPages(); pageNum++) {
                    stripper.setStartPage(pageNum);
                    stripper.setEndPage(pageNum);
                    String pageText = stripper.getText(pdf);

                    Map<String, Object> pageMetadata = new HashMap<>(metadata);
                    pageMetadata.put("page", pageNum);
                    pageMetadata.put("total_pages", pdf.getNumberOfPages());

                    Document doc = new Document(pageText.trim(), pageMetadata)
                            .summary("Page " + pageNum)
                            .metadata(this.additionalMetadata);
                    documents.add(doc);
                }
            }

            return documents;
        } catch (IOException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 分割模式
     *
     * @author 小奶奶花生米
     * @since 3.1
     */
    public static enum LoadMode {
        /**
         * 整个文档作为一个 Document
         */
        SINGLE,
        /**
         * 每页作为一个 Document
         */
        PAGE
    }

    /**
     * 加载选项
     */
    public static class Options {
        private LoadMode loadMode = LoadMode.PAGE;
        private String pageDelimiter = "\n\f";

        /**
         * PDF 加载模式，可以是单文档模式或分页模式
         */
        public Options loadMode(LoadMode loadMode) {
            this.loadMode = loadMode;
            return this;
        }

        /**
         * 页面分隔符，用于在单文档模式下分隔不同页面的文本
         */
        public Options pageDelimiter(String pageDelimiter) {
            if (pageDelimiter != null) {
                this.pageDelimiter = pageDelimiter;
            }
            return this;
        }
    }
}