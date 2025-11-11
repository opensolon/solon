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
 * 类索引工具类
 * 
 * 编译时：借助AOT时机，通过beanScan收集类索引，一个"basePackage"对应一个索引文件
 * 启动时：查找包对应的类索引文件。如果有，使用类索引文件替代ScanUtil.scan机制
 *
 * @author noear
 * @since 3.7
 */
@Internal
public class ClassIndexUtil {
    private static final Logger log = LoggerFactory.getLogger(ClassIndexUtil.class);

    //索引文件后缀名
    private static final String INDEX_FILE_SUFFIX = ".index";

    //索引文件存储路径
    private static final String INDEX_FILE_DIR = "META-INF/solon-index/";

    /**
     * 加载类索引文件
     *
     * @param basePackage 基础包名
     * @return 类名列表，如果不存在索引文件则返回null
     */
    public static Collection<String> loadClassIndex(String basePackage) {
        String indexFileName = getIndexFileName(basePackage);

        try {
            URL uri = ResourceUtil.getResource(INDEX_FILE_DIR + indexFileName);
            if (uri == null) {
                return null;
            }

            List<String> classNames = new ArrayList<>();
            try (InputStream inputStream = uri.openStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    classNames.add(line.trim());
                }
            }

            return classNames;
        } catch (IOException e) {
            // 索引文件读取失败，返回null
            log.warn("Failed to load class index file for package: {}", basePackage, e);
            return null;
        }
    }

    /**
     * 获取索引文件名
     */
    public static String getIndexFileName(String basePackage) {
        if (basePackage == null || basePackage.isEmpty()) {
            throw new IllegalArgumentException("basePackage cannot be null or empty");
        }

        // 防止路径遍历攻击
        if (basePackage.contains("..") || basePackage.contains("/") || basePackage.contains("\\")) {
            throw new IllegalArgumentException("Invalid basePackage: contains path traversal characters");
        }

        return basePackage.replace('.', '-') + INDEX_FILE_SUFFIX;
    }

    /**
     * 写入索引文件
     */
    public static void writeIndexFile(String basePackage, List<String> classNames) {
        String indexFileName = getIndexFileName(basePackage);

        File indexFile = RuntimeService.global().createClassOutputFile(INDEX_FILE_DIR + indexFileName);

        try {
            // 确保目录存在
            indexFile.getParentFile().mkdirs();

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(indexFile.toPath()), StandardCharsets.UTF_8))) {
                for (String className : classNames) {
                    writer.write(className);
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            log.warn("Failed to write class index file for package: {}", basePackage, e);
        }
    }
}