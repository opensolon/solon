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

import org.noear.solon.expression.exception.CompilationException;
import org.noear.solon.expression.exception.EvaluationException;
import org.noear.solon.expression.Evaluator;
import org.noear.solon.expression.Expression;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Solon 表达式语言模板引擎（简称，SnTmpl）
 *
 * @author noear
 * @since 3.1
 */
public class TemplateEvaluator implements Evaluator<String> {
    private static final TemplateEvaluator INSTANCE = new TemplateEvaluator();
    private final Map<String, Expression<String>> exprCache = new ConcurrentHashMap<>();

    // 配置常量
    private static final int BUFFER_SIZE = 1024; // 增大缓冲区
    private static final char MARK_START1 = '#';
    private static final char MARK_START2 = '$';
    private static final char MARK_BRACE_OPEN = '{';
    private static final char MARK_BRACE_CLOSE = '}';

    public static TemplateEvaluator getInstance() {
        return INSTANCE;
    }

    @Override
    public String eval(String expr, Function context, boolean cached) {
        try {
            return (String) (cached ? exprCache.computeIfAbsent(expr, this::parse) : parse(expr))
                    .eval(context);
        } catch (EvaluationException e) {
            throw e;
        } catch (Exception e) {
            throw new EvaluationException("Evaluation failed", e); // 简化错误信息
        }
    }

    @Override
    public Expression<String> parse(Reader reader) {
        try (BufferedReader br = wrapReader(reader)) {
            ParserState state = new ParserState(br);
            List<TemplateFragment> fragments = new ArrayList<>(64); // 预设初始容量

            char[] buffer = new char[BUFFER_SIZE];
            int readCount;
            while ((readCount = br.read(buffer)) != -1) {
                parseBuffer(buffer, readCount, state, fragments);
            }

            completeParsing(state, fragments);
            return new TemplateNode(fragments);
        } catch (IOException e) {
            throw new CompilationException("Compilation failed", e);
        }
    }

    private void parseBuffer(char[] buffer, int length,
                             ParserState state, List<TemplateFragment> fragments) {
        for (int i = 0; i < length; ) {
            if (state.inExpression) {
                i = processExpression(buffer, i, length, state, fragments);
            } else {
                i = processText(buffer, i, length, state, fragments);
            }
        }
    }

    private int processText(char[] buffer, int index, int length,
                            ParserState state, List<TemplateFragment> fragments) {
        int start = index;
        while (index < length) {
            char ch = buffer[index];
            if ((ch == MARK_START1 || ch == MARK_START2) && index + 1 < length) {
                state.marker = ch;
                if (buffer[index + 1] == MARK_BRACE_OPEN) {
                    flushText(state, fragments, buffer, start, index);
                    state.inExpression = true;
                    return index + 2; // 跳过两个字符
                } else {
                    index++; // 跳过当前字符，继续处理
                }
            }
            index++;
        }
        state.textBuffer.append(buffer, start, index - start);
        return index;
    }

    private int processExpression(char[] buffer, int index, int length,
                                  ParserState state, List<TemplateFragment> fragments) {
        int start = index;
        while (index < length) {
            if (buffer[index] == MARK_BRACE_CLOSE) {
                state.exprBuffer.append(buffer, start, index - start);
                fragments.add(new TemplateFragment(true, state.marker, state.exprBuffer.toString()));
                state.exprBuffer.setLength(0);
                state.inExpression = false;
                return index + 1;
            }
            index++;
        }
        state.exprBuffer.append(buffer, start, index - start);
        return index;
    }

    private void flushText(ParserState state, List<TemplateFragment> fragments,
                           char[] buffer, int start, int end) {
        if (start < end) {
            state.textBuffer.append(buffer, start, end - start);
        }
        if (state.textBuffer.length() > 0) {
            fragments.add(new TemplateFragment(false, 0, state.textBuffer.toString()));
            state.textBuffer.setLength(0);
        }
    }

    private void completeParsing(ParserState state, List<TemplateFragment> fragments) {
        if (state.inExpression) {
            fragments.add(new TemplateFragment(false, 0, "#{" + state.exprBuffer));
        }
        flushText(state, fragments, new char[0], 0, 0); // 强制刷新剩余文本
    }

    private static BufferedReader wrapReader(Reader reader) {
        return reader instanceof BufferedReader ?
                (BufferedReader) reader : new BufferedReader(reader);
    }

    private static class ParserState {
        final StringBuilder textBuffer = new StringBuilder(256);
        final StringBuilder exprBuffer = new StringBuilder(64);
        boolean inExpression;
        int marker;
        final BufferedReader reader;

        ParserState(BufferedReader reader) {
            this.reader = reader;
        }
    }
}