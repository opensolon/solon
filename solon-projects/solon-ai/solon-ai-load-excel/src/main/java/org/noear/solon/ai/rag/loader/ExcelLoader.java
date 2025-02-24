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
import org.noear.solon.ai.rag.DocumentLoader;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Excel 文档加载器
 *
 * @author noear
 * @since 3.1
 */
public class ExcelLoader extends AbstractDocumentLoader {
	private InputStream inputStream;
	
	public ExcelLoader(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
    @Override
    public List<Document> load() {
    	ExcelReader reader = ExcelUtil.getReader(this.inputStream);
    	List<Map<String, Object>> readAll = reader.readAll();
    	
        return Collections.emptyList();
    }
}
