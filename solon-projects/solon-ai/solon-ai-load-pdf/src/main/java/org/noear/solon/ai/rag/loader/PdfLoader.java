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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.noear.solon.ai.rag.Document;

/**
 * pdf 文档加载器
 *
 * @author 小奶奶花生米
 * @author noear
 * @since 3.1
 * */
public class PdfLoader extends AbstractDocumentLoader {
    /**
     * 文件源
     */
    private final File source;
    /**
     * 加载选项
     */
    private final Options options;

    public PdfLoader(File source) {
        this(source, null);
    }

    public PdfLoader(File source, Options options) {
        this.source = source;

        if (options == null) {
            this.options = Options.DEFAULT;
        } else {
            this.options = options;
        }

        this.check();
    }

    private void check() {
        // 检查文件是否为null
        if (source == null) {
            throw new IllegalArgumentException("File source cannot be null");
        }

        // 检查文件是否存在
        if (!source.exists()) {
            throw new IllegalArgumentException("File source does not exist: " + source.getPath());
        }

        // 检查是否是文件而不是目录
        if (!source.isFile()) {
            throw new IllegalArgumentException("File source is not a file: " + source.getPath());
        }

        // 检查文件是否可读
        if (!source.canRead()) {
            throw new IllegalArgumentException("File source is not readable: " + source.getPath());
        }

        // 检查文件扩展名
        String fileName = source.getName().toLowerCase();
        if (!fileName.endsWith(".pdf")) {
            throw new IllegalArgumentException("File source is not a PDF: " + source.getPath());
        }

        // 检查文件大小不为0
        if (source.length() == 0) {
            throw new IllegalArgumentException("File source is empty: " + source.getPath());
        }
    }

    /**
     * 加载PDF文档
     * <p>
     * 在SINGLE模式下，将整个PDF文档作为一个Document返回，使用pageDelimiter分隔不同页面
     * 在PAGE模式下，将每一页作为单独的Document返回
     * </p>
     *
     * @return Document列表，包含提取的文本内容和元数据
     * @throws RuntimeException 如果PDF文件加载或处理失败
     * @author 小奶奶花生米
     */
    @Override
    public List<Document> load() {
        List<Document> documents = new ArrayList<>();

        try (PDDocument pdf = PDDocument.load(source)) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("source", source.getName());
            metadata.put("type", "pdf");

            if (options.loadMode == LoadMode.SINGLE) {
                // 整个文档作为一个 Document
                PDFTextStripper stripper = new PDFTextStripper();
                // 设置页面分隔符
                stripper.setPageEnd(options.pageDelimiter);
                String text = stripper.getText(pdf);

                Document doc = new Document(text, metadata)
                        .title(source.getName())
                        .addMetadata(this.additionalMetadata)
                        .addMetadata("pages", pdf.getNumberOfPages());
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
                            .title(source.getName())
                            .snippet("Page " + pageNum)
                            .addMetadata(this.additionalMetadata);
                    documents.add(doc);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to load: " + source.getPath(), e);
        }

        return documents;
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
     *
     * @author noear
     * @since 3.1
     */
    public static class Options {
        private static final Options DEFAULT = new Options();

        /**
         * PDF 加载模式，可以是单文档模式或分页模式
         */
        private LoadMode loadMode = LoadMode.PAGE;

        /**
         * 页面分隔符，用于在单文档模式下分隔不同页面的文本
         */
        private String pageDelimiter = "\n\f";

        public Options loadMode(LoadMode loadMode) {
            this.loadMode = loadMode;
            return this;
        }

        public Options pageDelimiter(String pageDelimiter) {
            if (pageDelimiter != null) {
                this.pageDelimiter = pageDelimiter;
            }
            return this;
        }
    }
}