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
package org.noear.solon.expression.snel;

import org.noear.solon.expression.Parser;
import org.noear.solon.expression.Expression;
import org.noear.solon.expression.util.LRUCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Solon 表达式语言模板解析器
 *
 * <p>
 * 支持以下示例：
 * 1."name is #{user.name}, and key is ${key}"
 * </p>
 * @author noear
 * @since 3.1
 */
public class SnelTemplateParser implements Parser<String> {
    private static final SnelTemplateParser INSTANCE = new SnelTemplateParser(1000);
    private final Map<String, Expression<String>> exprCached;

    public SnelTemplateParser(int cahceCapacity) {
        exprCached = Collections.synchronizedMap(new LRUCache<>(cahceCapacity));
    }

    private static final char MARK_START1 = '#';
    private static final char MARK_START2 = '$';
    private static final char MARK_BRACE_OPEN = '{';
    private static final char MARK_BRACE_CLOSE = '}';

    public static SnelTemplateParser getInstance() {
        return INSTANCE;
    }

    @Override
    public Expression<String> parse(String expr, boolean cached) {
        return cached ? exprCached.computeIfAbsent(expr, this::parseDo) : parseDo(expr);
    }

    private Expression<String> parseDo(String expr) {
        List<TemplateFragment> fragments = new ArrayList<>(expr.length() / 16); // 基于经验值的容量初始化
        boolean inExpression = false;
        char marker = 0;
        int textStart = 0;
        int scanPosition = 0;
        final int length = expr.length();

        while (scanPosition < length) {
            if (!inExpression) {
                // 文本模式：批量扫描直到发现表达式起始标记
                int exprStart = findExpressionStart(expr, scanPosition);
                if (exprStart != -1) {
                    // 添加前置文本
                    if (textStart < exprStart) {
                        fragments.add(new TemplateFragment(false, 0, expr.substring(textStart, exprStart)));
                    }
                    marker = expr.charAt(exprStart);
                    inExpression = true;
                    textStart = scanPosition = exprStart + 2; // 跳过标记符
                } else {
                    // 剩余部分全部作为文本
                    fragments.add(new TemplateFragment(false, 0, expr.substring(textStart)));
                    break;
                }
            } else {
                // 表达式模式：精确查找闭合标记
                int closePos = expr.indexOf(MARK_BRACE_CLOSE, scanPosition);
                if (closePos != -1) {
                    fragments.add(new TemplateFragment(true, marker, expr.substring(textStart, closePos)));
                    inExpression = false;
                    textStart = scanPosition = closePos + 1; // 跳过闭合标记
                } else {
                    // 未闭合表达式作为文本回退
                    fragments.add(new TemplateFragment(false, 0, expr.substring(textStart - 2, textStart) + expr.substring(textStart)));
                    break;
                }
            }
        }

        return new TemplateNode(fragments);
    }

    // 快速定位表达式起始标记
    private int findExpressionStart(String s, int start) {
        for (int i = start; i < s.length() - 1; i++) {
            char c = s.charAt(i);
            if ((c == MARK_START1 || c == MARK_START2) && s.charAt(i + 1) == MARK_BRACE_OPEN) {
                return i;
            }
        }
        return -1;
    }
}