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
import org.noear.solon.ai.rag.DocumentLoader;

/**
 * pdf 文档加载器
 *
 * @author noear
 * @since 3.1
 * */
public class PdfLoader implements DocumentLoader {
    /**
     * PDF 文件对象
     */
    private final File pdfFile;

    /**
     * PDF 加载模式，可以是单文档模式或分页模式
     */
    private final LoadMode mode;

    /**
     * 页面分隔符，用于在单文档模式下分隔不同页面的文本
     */
    private final String pageDelimiter;

    /**
     * 构造函数，使用默认的分页模式
     *
     * @param pdfFile PDF文件对象
     * @author 小奶奶花生米
     */
    public PdfLoader(File pdfFile) {
        this(pdfFile, LoadMode.PAGE);
    }

    /**
     * 构造函数，使用指定的加载模式和默认的页面分隔符
     *
     * @param pdfFile PDF文件对象
     * @param mode    加载模式，SINGLE或PAGE
     * @author 小奶奶花生米
     */
    public PdfLoader(File pdfFile, LoadMode mode) {
        this(pdfFile, mode, "\n\f");
    }

    /**
     * 构造函数，完整参数配置
     *
     * @param pdfFile       PDF文件对象
     * @param mode          加载模式，SINGLE或PAGE
     * @param pageDelimiter 页面分隔符，用于在单文档模式下分隔不同页面的文本
     * @throws IllegalArgumentException 如果PDF文件不存在、不可读或不是PDF文件
     * @author 小奶奶花生米
     */
    public PdfLoader(File pdfFile, LoadMode mode, String pageDelimiter) {
        // 检查文件是否为null
        if (pdfFile == null) {
            throw new IllegalArgumentException("PDF file cannot be null");
        }

        // 检查文件是否存在
        if (!pdfFile.exists()) {
            throw new IllegalArgumentException("PDF file does not exist: " + pdfFile.getPath());
        }

        // 检查是否是文件而不是目录
        if (!pdfFile.isFile()) {
            throw new IllegalArgumentException("Path is not a file: " + pdfFile.getPath());
        }

        // 检查文件是否可读
        if (!pdfFile.canRead()) {
            throw new IllegalArgumentException("PDF file is not readable: " + pdfFile.getPath());
        }

        // 检查文件扩展名
        String fileName = pdfFile.getName().toLowerCase();
        if (!fileName.endsWith(".pdf")) {
            throw new IllegalArgumentException("File is not a PDF: " + pdfFile.getPath());
        }

        // 检查文件大小不为0
        if (pdfFile.length() == 0) {
            throw new IllegalArgumentException("PDF file is empty: " + pdfFile.getPath());
        }

        // 检查mode参数
        if (mode == null) {
            throw new IllegalArgumentException("Load mode cannot be null");
        }

        // 检查pageDelimiter参数
        if (pageDelimiter == null) {
            throw new IllegalArgumentException("Page delimiter cannot be null");
        }

        this.pdfFile = pdfFile;
        this.mode = mode;
        this.pageDelimiter = pageDelimiter;
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

        try (PDDocument pdf = PDDocument.load(pdfFile)) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("source", pdfFile.getName());
            metadata.put("type", "pdf");

            if (mode == LoadMode.SINGLE) {
                // 整个文档作为一个 Document
                PDFTextStripper stripper = new PDFTextStripper();
                // 设置页面分隔符
                stripper.setPageEnd(pageDelimiter);
                String text = stripper.getText(pdf);

                Document doc = new Document(text, metadata)
                        .title(pdfFile.getName())
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
                            .title(pdfFile.getName())
                            .snippet("Page " + pageNum);
                    documents.add(doc);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to load PDF file: " + pdfFile.getPath(), e);
        }

        return documents;
    }

    /**
     * PDF 加载模式
     *
     * @author 小奶奶花生米
     * @date 2025-02-21
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
}