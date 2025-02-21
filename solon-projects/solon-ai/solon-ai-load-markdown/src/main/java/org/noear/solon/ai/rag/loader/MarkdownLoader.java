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
import org.noear.solon.ai.rag.DocumentSplitter;

import java.util.*;
import java.util.regex.Pattern;

/**
 * md 文档加载器
 *
 * @author chengchuanyao
 * @since 3.1
 */
public class MarkdownLoader implements DocumentSplitter {

    // Markdown文档清理正则表达式
    private static final String[][] MD_PATTERNS = {
            {"(?m)^#{1,6}\\s+(.+)$", "$1"},                        //标题
            {"```[\\s\\S]*?```", ""},                              //代码块
            {"`[^`]*`", ""},                                       //行内代码
            {"!\\[([^\\]]*?)\\]\\([^\\)]+\\)", ""},                //图片
            // {"\\[(#\\d+|[^\\]]+)\\]\\(([^\\)]+)\\)\\)?", "($2)"},  //链接转换为(url)格式
            {"\\[(#\\d+|[^\\]]+)\\]\\(([^\\)]+)\\)\\)?", "$2"},    //链接转换为url格式（不带括号）
            {"^[\\*\\-+]\\s+", ""},                                //无序列表
            {"^\\d+\\.\\s+", ""},                                  //有序列表
            {"\\*\\*(.+?)\\*\\*|__(.+?)__", "$1$2"},               //加粗
            {"\\*(.+?)\\*|_(.+?)_", "$1$2"},                       //斜体
            {"^>\\s*", ""},                                        //引用
            {"^-{3,}|={3,}|\\*{3,}$", ""}                          //分隔线
    };
    // 预定义的正则表达式处理规则
    private static final String[][] PREDEFINED_PATTERNS = {
            {"\\s+", " "},                                                                      // 替换连续的空格、换行符和制表符
            {"(?:https?://|www\\.)[^\\s]+|[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}", ""} // 删除URL和邮箱
    };
    private final String delimiter;           // 分段标识符
    private final int maxSegmentLength;       // 最大分段长度
    private final int segmentOverlapLength;   // 分段重叠长度
    private final List<String[]> extraPatterns; // 额外的正则表达式规则
    private final Set<PatternType> enabledPatterns;  // 启用的预定义模式

    private static final int MAX_SEGMENT_LENGTH = 500;

    private static final int SEGMENT_OVERLAP_LENGTH = 50;

    public MarkdownLoader() {
        this("\n\n", MAX_SEGMENT_LENGTH, SEGMENT_OVERLAP_LENGTH);
    }

    public MarkdownLoader(int maxSegmentLength, int segmentOverlapLength) {
        this("\n\n", maxSegmentLength, segmentOverlapLength);
    }

    public MarkdownLoader(String delimiter) {
        this(delimiter, MAX_SEGMENT_LENGTH, SEGMENT_OVERLAP_LENGTH);
    }

    public MarkdownLoader(String delimiter, int maxSegmentLength, int segmentOverlapLength) {
        this(delimiter, maxSegmentLength, segmentOverlapLength, EnumSet.noneOf(PatternType.class), null);
    }

    public MarkdownLoader(String delimiter,Set<PatternType> enabledPatterns) {
        this(delimiter, MAX_SEGMENT_LENGTH, SEGMENT_OVERLAP_LENGTH, enabledPatterns, null);
    }

    public MarkdownLoader(String delimiter, List<String[]> extraPatterns) {
        this(delimiter, MAX_SEGMENT_LENGTH, SEGMENT_OVERLAP_LENGTH, EnumSet.noneOf(PatternType.class), extraPatterns);
    }

    public MarkdownLoader(String delimiter,Set<PatternType> enabledPatterns, List<String[]> extraPatterns) {
        this(delimiter, MAX_SEGMENT_LENGTH, SEGMENT_OVERLAP_LENGTH, enabledPatterns, extraPatterns);
    }

    // 新增构造函数，支持自定义正则表达式
    public MarkdownLoader(String delimiter, int maxSegmentLength, int segmentOverlapLength, Set<PatternType> enabledPatterns, List<String[]> extraPatterns) {
        if (maxSegmentLength <= segmentOverlapLength) {
            throw new IllegalArgumentException("maxSegmentLength must be greater than segmentOverlapLength");
        }
        this.delimiter = delimiter;
        this.maxSegmentLength = maxSegmentLength;
        this.segmentOverlapLength = segmentOverlapLength;
        this.enabledPatterns = enabledPatterns != null ? enabledPatterns : EnumSet.noneOf(PatternType.class);
        this.extraPatterns = extraPatterns != null ? extraPatterns : Collections.EMPTY_LIST;
    }

    // 新增构造函数，支持自定义正则表达式
    public MarkdownLoader(String delimiter, int maxSegmentLength, int segmentOverlapLength, Set<PatternType> enabledPatterns) {
        if (maxSegmentLength <= segmentOverlapLength) {
            throw new IllegalArgumentException("maxSegmentLength must be greater than segmentOverlapLength");
        }
        this.delimiter = delimiter;
        this.maxSegmentLength = maxSegmentLength;
        this.segmentOverlapLength = segmentOverlapLength;
        this.enabledPatterns = enabledPatterns != null ? enabledPatterns : EnumSet.noneOf(PatternType.class);
        this.extraPatterns = Collections.EMPTY_LIST;
    }

    private String applyExtraPatterns(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        // 应用启用的预定义模式
        for (PatternType type : enabledPatterns) {
            content = Pattern.compile(type.pattern)
                    .matcher(content)
                    .replaceAll(type.replacement);
        }
        // 应用额外的自定义模式
        if (extraPatterns != null) {
            for (String[] pattern : extraPatterns) {
                content = Pattern.compile(pattern[0])
                        .matcher(content)
                        .replaceAll(pattern[1]);
            }
        }
        return content;
    }

    @Override
    public List<Document> split(List<Document> documents) {
        List<Document> result = new ArrayList<>();
        for (Document doc : documents) {
            // 1. 标准化换行符
            String content = doc.getContent()
                    .replaceAll("\\r\\n|\\r", "\n")
                    .replaceAll("\\n{3,}", "\n\n");

            // 2. 清理Markdown语法
            content = cleanMarkdown(content);

            // 3. 应用额外的正则表达式处理
            content = applyExtraPatterns(content);

            // 4. 分段处理
            List<String> segments = segment(content);

            // 5. 转换为Document列表
            for (String segment : segments) {
                if (!segment.trim().isEmpty()) {
                    result.add(new Document(segment));
                }
            }
        }
        return result;
    }

    private String cleanMarkdown(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        for (String[] pattern : MD_PATTERNS) {
            content = Pattern.compile(pattern[0])
                    .matcher(content)
                    .replaceAll(pattern[1]);
        }
        // 只替换连续空白字符，保留换行符
        return content.replaceAll("[ \\t]+", " ").trim();
    }

    private List<String> segment(String text) {
        List<String> segments = new ArrayList<>();

        // 1. 根据分段标识符进行初步分段
        String[] parts;
        if (delimiter != null && !delimiter.isEmpty()) {
            parts = text.split(Pattern.quote(delimiter));
        } else {
            parts = new String[]{text};
        }

        // 2. 处理每个分段，确保长度合适并添加重叠
        for (String part : parts) {
            if (part.length() <= maxSegmentLength) {
                if (!part.trim().isEmpty()) {
                    segments.add(part);
                }
            } else {
                int currentPosition = 0;
                while (currentPosition < part.length()) {
                    int endPosition = Math.min(currentPosition + maxSegmentLength, part.length());
                    String segment = part.substring(currentPosition, endPosition);

                    if (!segment.trim().isEmpty()) {
                        segments.add(segment);
                    }

                    if (endPosition < part.length()) {
                        currentPosition = endPosition - segmentOverlapLength;
                    } else {
                        break;
                    }
                }
            }
        }

        return segments;
    }
}

// 预定义的正则表达式类型
enum PatternType {
    SPACE("\\s+", " "),                                                                            // 替换连续的空格、换行符和制表符
    URL_EMAIL("(?:https?://|www\\.)[^\\s]+|[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}", "");  // 删除URL和邮箱

    protected final String pattern;
    protected final String replacement;

    PatternType(String pattern, String replacement) {
        this.pattern = pattern;
        this.replacement = replacement;
    }
}