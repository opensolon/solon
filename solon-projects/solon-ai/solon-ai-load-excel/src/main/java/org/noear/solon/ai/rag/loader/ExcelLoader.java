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

import cn.hutool.json.JSONArray;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import java.io.InputStream;
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

	private InputStream inputStream;
	
	public ExcelLoader(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
    @Override
    public List<Document> load() {
    	JSONArray jsonArray = new JSONArray();
    	List<Document> docs = new ArrayList<>();
    	
    	ExcelReader reader = ExcelUtil.getReader(this.inputStream);
    	int numberOfSheets = reader.getWorkbook().getNumberOfSheets();
    	for(int s=0;s<numberOfSheets;s++) {
    		List<Map<String, Object>> readAll = reader.setSheet(s).readAll();
    		if(readAll.size()>0) {
    			// 第一行标题
            	Map<String, Object> titleRow = readAll.get(0);
            	readAll.remove(0);
            	
            	// 每200行切分一段
            	int batchSize = 200;
        		int totalSize = readAll.size();
                int numBatches = (totalSize + batchSize - 1) / batchSize; // 计算总批次数
                for (int i = 0; i < numBatches; i++) {
                	int start = i * batchSize;
                    int end = Math.min(start + batchSize, totalSize); // 确保最后一组不会越界

                    List<Map<String, Object>> batch = readAll.subList(start, end);
                    
                    jsonArray.addAll(batch);
                    
                    Document doc = new Document(jsonArray.toString());
                    docs.add(doc);
                }
    		}
    	}
    	
    	
    	
//        return Collections.emptyList();
    	return docs;
    }
}
