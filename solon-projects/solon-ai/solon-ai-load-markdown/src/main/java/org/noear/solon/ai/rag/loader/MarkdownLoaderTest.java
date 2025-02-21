package org.noear.solon.ai.rag.loader;

import org.noear.solon.ai.rag.Document;
import org.noear.solon.core.util.ResourceUtil;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

/**
 * @author chengchuanyao
 * @date 2025/2/21 17:18
 */
public class MarkdownLoaderTest {

    public static void main(String[] args) throws IOException {
//        MarkdownLoader loader = new MarkdownLoader();
//        MarkdownLoader loader = new MarkdownLoader("\n\n", 500, 50);
        MarkdownLoader loader = new MarkdownLoader("\n\n", 500, 50, EnumSet.of(PatternType.SPACE));
        List<Document> split = loader.split(ResourceUtil.getResourceAsString(new File("D:\\work\\chuzhong\\solon\\README_CN.md").toURI().toURL()));
        split.forEach(s-> System.out.println("分段：\n"+s.getContent()));
    }
}
