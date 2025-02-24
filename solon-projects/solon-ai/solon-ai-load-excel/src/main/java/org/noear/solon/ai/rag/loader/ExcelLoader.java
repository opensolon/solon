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

import org.noear.snack.ONode;
import org.noear.solon.ai.rag.Document;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import org.noear.solon.core.util.SupplierEx;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Excel 文档加载器
 * <p>读取excel表格内容，并转换为json</p>
 *
 * @author linziguan
 * @since 3.1
 */
public class ExcelLoader extends AbstractDocumentLoader {
    private final SupplierEx<InputStream> source;
    private final Options options;

    public ExcelLoader(File file) {
        this(file, null);
    }

    public ExcelLoader(File file, Options options) {
        this(() -> new FileInputStream(file), options);
    }

    public ExcelLoader(URL url) {
        this(url, null);
    }

    public ExcelLoader(URL url, Options options) {
        this(() -> url.openStream(), options);
    }

    public ExcelLoader(SupplierEx<InputStream> source, Options options) {
        this.source = source;

        if (options == null) {
            this.options = Options.DEFAULT;
        } else {
            this.options = options;
        }
    }

    @Override
    public List<Document> load() throws IOException {
        List<Document> docs = new ArrayList<>();

        try (InputStream stream = source.get()) {
            try (ExcelReader reader = ExcelUtil.getReader(stream)) {
                int numberOfSheets = reader.getWorkbook().getNumberOfSheets();
                for (int num = 0; num < numberOfSheets; num++) {
                    List<Map<String, Object>> readAll = reader.setSheet(num).readAll();
                    if (readAll.size() > 0) {
                        String title = null;
                        if (options.firstLineIsHeader) {
                            // 第一行标题
                            Map<String, Object> titleRow = readAll.get(0);
                            readAll.remove(0);
                        }

                        int totalSize = readAll.size();
                        int numBatches = (totalSize + options.documentMaxRows - 1) / options.documentMaxRows; // 计算总批次数
                        for (int i = 0; i < numBatches; i++) {
                            int start = i * options.documentMaxRows;
                            int end = Math.min(start + options.documentMaxRows, totalSize); // 确保最后一组不会越界

                            List<Map<String, Object>> batch = readAll.subList(start, end);

                            docs.add(new Document(ONode.stringify(batch)).title(title).metadata(this.additionalMetadata));
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        return docs;
    }

    /**
     * 选项
     */
    public static class Options {
        private static final Options DEFAULT = new Options();

        private boolean firstLineIsHeader = true;
        private int documentMaxRows = 200;

        /**
         * 第一行是表头
         */
        public Options firstLineIsHeader(boolean firstLineIsHeader) {
            this.firstLineIsHeader = firstLineIsHeader;
            return this;
        }

        /**
         * 一个文档最多放多少行
         */
        public Options documentMaxRows(int documentMaxRows) {
            this.documentMaxRows = documentMaxRows;
            return this;
        }
    }
}