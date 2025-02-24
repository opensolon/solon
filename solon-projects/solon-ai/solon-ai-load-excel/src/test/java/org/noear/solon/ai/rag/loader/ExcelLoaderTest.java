package org.noear.solon.ai.rag.loader;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.noear.solon.ai.rag.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelLoaderTest {
	private static final Logger log = LoggerFactory.getLogger(ExcelLoaderTest.class);
	
	@Test
    public void test1() throws Exception {
		ExcelLoader loader = new ExcelLoader(new FileInputStream(new File("一个excel文档地址")));
		List<Document> docs = loader.load();
		System.out.println(docs);
	}
}
