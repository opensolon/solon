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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.ai.rag.Document;

import org.noear.solon.core.util.SupplierEx;
import org.noear.solon.lang.Preview;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Excel 文档加载器
 * <p>读取excel表格内容，并转换为json</p>
 *
 * @author linziguan
 * @since 3.1
 */
@Preview("3.1")
public class ExcelLoader extends AbstractOptionsDocumentLoader<ExcelLoader.Options, ExcelLoader> {
    private final SupplierEx<InputStream> source;

    public ExcelLoader(File file) {
        this(() -> new FileInputStream(file));
    }

    public ExcelLoader(URL url) {
        this(() -> url.openStream());
    }

    public ExcelLoader(SupplierEx<InputStream> source) {
        this.source = source;
        this.options = new Options();
    }

    @Override
    public List<Document> load() throws IOException {
        List<Document> docs = new ArrayList<>();

        try (InputStream stream = source.get()) {
            try (XSSFWorkbook reader = new XSSFWorkbook(stream)) {
                int numberOfSheets = reader.getNumberOfSheets();
                for (int num = 0; num < numberOfSheets; num++) {
                    XSSFSheet sheet = reader.getSheetAt(num);

                    List<Map<String, Object>> readAll = new ArrayList<>();
                    List<Object> titles = null;

                    for (Row row : sheet) {
                        List<Object> values = readRow(row);

                        if (Utils.isEmpty(values)) {
                            break;
                        }

                        if (titles == null) {
                            titles = values;
                        } else {
                            readAll.add(rowToMap(titles, values));
                        }
                    }

                    if (readAll.size() > 0) {
                        if (options.documentMaxRows < 0) {
                            docs.add(new Document(ONode.stringify(readAll)).metadata(this.additionalMetadata));
                        } else {
                            int totalSize = readAll.size();
                            int numBatches = (totalSize + options.documentMaxRows - 1) / options.documentMaxRows; // 计算总批次数
                            for (int i = 0; i < numBatches; i++) {
                                int start = i * options.documentMaxRows;
                                int end = Math.min(start + options.documentMaxRows, totalSize); // 确保最后一组不会越界

                                List<Map<String, Object>> batch = readAll.subList(start, end);

                                docs.add(new Document(ONode.stringify(batch)).metadata(this.additionalMetadata));
                            }
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

    private Map<String, Object> rowToMap(List<Object> titles, List<Object> values) {
        Map<String, Object> rowMap = new LinkedHashMap<>();
        for (int i = 0; i < titles.size(); i++) {
            Object val = null;
            if (values.size() > i) {
                val = values.get(i);
            }

            rowMap.put(String.valueOf(titles.get(i)), String.valueOf(val));
        }

        return rowMap;
    }

    private List<Object> readRow(Row row) {
        int noneCount = 0;
        List<Object> values = new ArrayList<>();
        for (Cell cell : row) {
            switch (cell.getCellType()) {
                case _NONE:
                    values.add(null);
                    noneCount++;
                    break;
                case BLANK:
                    values.add("");
                    noneCount++;
                    break;
                case STRING:
                    values.add(cell.getStringCellValue());
                    break;
                case NUMERIC:
                    values.add(cell.getNumericCellValue());
                    break;
                case BOOLEAN:
                    values.add(cell.getBooleanCellValue());
                    break;
                case FORMULA:
                    values.add(cell.getCellFormula());
                    break;
                case ERROR:
                    values.add(cell.getErrorCellValue());
                    break;
            }
        }

        if (values.size() == noneCount) {
            return null;
        } else {
            return values;
        }
    }

    /**
     * 选项
     */
    public static class Options {
        private int documentMaxRows = 200;

        /**
         * 一个文档最多放多少行
         */
        public Options documentMaxRows(int documentMaxRows) {
            this.documentMaxRows = documentMaxRows;
            return this;
        }
    }
}