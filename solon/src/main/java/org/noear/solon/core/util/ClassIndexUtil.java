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
package org.noear.solon.core.util;

import org.noear.solon.Utils;
import org.noear.solon.core.runtime.NativeDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

/**
 * 类索引工具类
 * 
 * 编译时：借助AOT时机，通过beanScan收集类索引，一个"basePackage"对应一个索引文件
 * 启动时：查找包对应的类索引文件。如果有，使用类索引文件替代ScanUtil.scan机制
 *
 * @author noear
 * @since 3.6
 */
public class ClassIndexUtil {
    private static final Logger log = LoggerFactory.getLogger(ClassIndexUtil.class);
    
    /**
     * 索引文件后缀名
     */
    private static final String INDEX_FILE_SUFFIX = ".solonindex";

    /**
     * 索引文件存储路径
     */
    private static final String INDEX_FILE_DIR = "META-INF/solon-index/";
    
    /**
     * 文件名中允许的字符正则表达式
     */
    private static final String SAFE_FILENAME_PATTERN = "^[a-zA-Z0-9._-]+$";

    /**
     * 生成类索引文件
     *
     * @param classLoader 类加载器
     * @param basePackage 基础包名
     * @return 是否生成了索引文件
     */
    public static boolean generateClassIndex(ClassLoader classLoader, String basePackage) {
        if (classLoader == null) {
            throw new IllegalArgumentException("classLoader cannot be null");
        }
        
        if (Utils.isEmpty(basePackage)) {
            throw new IllegalArgumentException("basePackage cannot be null or empty");
        }
        
        if (!NativeDetector.isAotRuntime()) {
            // 只在AOT编译时生成索引
            return false;
        }

        String dir = basePackage.replace('.', '/');
        
        // 扫描包下的所有类文件
        Set<String> classNames = ScanUtil.scan(classLoader, dir, n -> n.endsWith(".class"));
        
        if (classNames.isEmpty()) {
            return false;
        }

        // 生成索引文件内容
        List<String> indexContent = new ArrayList<>();
        for (String className : classNames) {
            String fullClassName = className.substring(0, className.length() - 6).replace('/', '.');
            indexContent.add(fullClassName);
        }

        // 排序，确保索引文件内容稳定
        Collections.sort(indexContent);

        // 写入索引文件
        return writeIndexFile(basePackage, indexContent);
    }

    /**
     * 加载类索引文件
     *
     * @param basePackage 基础包名
     * @return 类名列表，如果不存在索引文件则返回null
     */
    public static List<String> loadClassIndex(String basePackage) {
        String indexFileName = getIndexFileName(basePackage);
        
        try {
            URL resourceUrl = ResourceUtil.getResource(INDEX_FILE_DIR + indexFileName);
            if (resourceUrl == null) {
                return null;
            }

            List<String> classNames = new ArrayList<>();
            try (InputStream inputStream = resourceUrl.openStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        classNames.add(line.trim());
                    }
                }
            }
            
            return classNames;
        } catch (IOException e) {
            // 索引文件读取失败，返回null
            return null;
        }
    }

    /**
     * 检查是否存在类索引文件
     *
     * @param basePackage 基础包名
     * @return 是否存在索引文件
     */
    public static boolean hasClassIndex(String basePackage) {
        String indexFileName = getIndexFileName(basePackage);
        return ResourceUtil.getResource(INDEX_FILE_DIR + indexFileName) != null;
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
        
        // 验证包名格式
        if (!basePackage.matches(SAFE_FILENAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid basePackage: contains unsafe characters");
        }
        
        return basePackage.replace('.', '-') + INDEX_FILE_SUFFIX;
    }

    /**
     * 写入索引文件
     */
    public static boolean writeIndexFile(String basePackage, List<String> classNames) {
        String indexFileName = getIndexFileName(basePackage);
        File indexFile = new File("target/classes/" + INDEX_FILE_DIR + indexFileName);
        
        try {
            // 确保目录存在
            indexFile.getParentFile().mkdirs();
            
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(indexFile), StandardCharsets.UTF_8))) {
                for (String className : classNames) {
                    writer.write(className);
                    writer.newLine();
                }
            }
            
            return true;
        } catch (IOException e) {
            log.warn("Failed to write class index file for package: {}", basePackage, e);
            return false;
        }
    }
}