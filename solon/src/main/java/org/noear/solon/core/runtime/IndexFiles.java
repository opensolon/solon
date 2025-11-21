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
package org.noear.solon.core.runtime;

import org.noear.solon.Utils;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.lang.Internal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

/**
 * 索引文件工具
 * 
 * 编译时：借助AOT时机，生成'类'或'资源'索引文件
 * 启动时：查找包对应的'类'或'资源'索引文件。如果有，则替代 ScanUtil.scan 机制
 *
 * @author noear
 * @since 3.7
 */
@Internal
public class IndexFiles {
    private static final Logger log = LoggerFactory.getLogger(IndexFiles.class);

    //索引文件后缀名
    private static final String INDEX_FILE_SUFFIX = ".index";

    //索引文件存储路径
    private static final String INDEX_FILE_DIR = "META-INF/solon-index/";

    /**
     * 加载类索引文件
     *
     * @param dir 目录
     * @return 类名列表，如果不存在索引文件则返回null
     */
    public static List<String> loadIndexFile(String dir, String tag) {
        String indexFileName = getIndexFileName(dir, tag);

        try {
            URL uri = ResourceUtil.getResource(INDEX_FILE_DIR + indexFileName);
            if (uri == null) {
                return null;
            }

            List<String> indexList = new ArrayList<>();
            try (InputStream inputStream = uri.openStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    indexList.add(line.trim());
                }
            }

            return indexList;
        } catch (IOException e) {
            // 索引文件读取失败，返回null
            log.warn("Failed to load class index file for dir: {}", dir, e);
            return null;
        }
    }

    /**
     * 获取索引文件名
     *
     * @param dir 目标
     * @param tag 标记
     */
    public static String getIndexFileName(String dir, String tag) {
        if (Utils.isEmpty(dir)) {
            throw new IllegalArgumentException("dir cannot be null or empty");
        }

        if (Utils.isEmpty(tag)) {
            throw new IllegalArgumentException("tag cannot be null or empty");
        }

        // 防止路径遍历攻击
        if (dir.contains("..")) {
            throw new IllegalArgumentException("Invalid dir: contains path traversal characters");
        }

        return dir.replace('/', '-').replace('.', '-') +
                "_" +
                tag +
                INDEX_FILE_SUFFIX;
    }

    /**
     * 写入索引文件
     */
    public static void writeIndexFile(String dir, String tag, List<String> indexList) {
        String indexFileName = getIndexFileName(dir, tag);

        File indexFile = RuntimeService.singleton().createClassOutputFile(INDEX_FILE_DIR + indexFileName);

        try {
            // 确保目录存在
            indexFile.getParentFile().mkdirs();

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(indexFile.toPath()), StandardCharsets.UTF_8))) {
                for (String idx : indexList) {
                    writer.write(idx);
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            log.warn("Failed to write class index file for dir: {}", dir, e);
        }
    }
}