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
package org.noear.solon.expression.sntmpl;

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
 * @author noear
 * @since 3.1
 */
public class SntmplEvaluator implements Evaluator<String> {
    private static final SntmplEvaluator instance = new SntmplEvaluator();
    private final Map<String, Expression<String>> exprCached = new ConcurrentHashMap<>();

    public static SntmplEvaluator getInstance() {
        return instance;
    }

    private static final int MARK_START1 = '#';
    private static final String MARK_START2 = "#{";
    private static final int MARK_START3 = '{';
    private static final int MARK_END = '}';

    @Override
    public String eval(String expr, Function context, boolean cached) {
        try {
            if (cached) {
                Expression<String> expression = exprCached.computeIfAbsent(expr, k -> compile(k));
                return expression.eval(context);
            } else {
                return (String) compile(expr).eval(context);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to evaluate expression: " + expr, e);
        }
    }

    @Override
    public Expression<String> compile(Reader reader) {
        try {
            List<TemplateFragment> fragments = new ArrayList<>();
            ParserState state = new ParserState(reader);

            int currentChar;
            while ((currentChar = state.reader.read()) != -1) {
                char ch = (char) currentChar;
                handleChar(state, ch, fragments, reader);
            }

            // 处理解析结束后的剩余内容
            finalizeParsing(state, fragments);
            return new TemplateNode(fragments);
        } catch (IOException e) {
            throw new RuntimeException("Failed to compile template from Reader", e);
        }
    }


    /**
     * 处理当前字符
     */
    public void handleChar(ParserState state, char ch, List<TemplateFragment> fragments, Reader reader) throws IOException {
        if (state.isEvaluable) {
            handleEvaluableState(state, ch, fragments);
        } else {
            handleTextState(state, ch, fragments, reader);
        }
    }

    /**
     * 处理文本状态
     */
    private void handleTextState(ParserState state, char ch, List<TemplateFragment> fragments, Reader reader) throws IOException {
        if (ch == MARK_START1) {
            reader.mark(1);
            int nextChar = reader.read();
            if (nextChar == MARK_START3) {
                addTextFragment(state, fragments);
                state.isEvaluable = true;
            } else {
                reader.reset();
                state.currentText.append(ch);
            }
        } else {
            state.currentText.append(ch);
        }
    }

    /**
     * 处理可评估状态
     */
    private void handleEvaluableState(ParserState state, char ch, List<TemplateFragment> fragments) {
        if (ch == MARK_END) {
            addVariableFragment(state, fragments);
            state.isEvaluable = false;
        } else {
            state.variableName.append(ch);
        }
    }

    /**
     * 添加文本片段
     */
    private void addTextFragment(ParserState state, List<TemplateFragment> fragments) {
        if (state.currentText.length() > 0) {
            fragments.add(new TemplateFragment(false, state.currentText.toString()));
            state.currentText.setLength(0);
        }
    }

    /**
     * 添加变量片段
     */
    private void addVariableFragment(ParserState state, List<TemplateFragment> fragments) {
        fragments.add(new TemplateFragment(true, state.variableName.toString()));
        state.variableName.setLength(0);
    }

    /**
     * 完成解析，处理剩余内容
     */
    public void finalizeParsing(ParserState state, List<TemplateFragment> fragments) {
        if (state.isEvaluable) {
            state.currentText.append(MARK_START2).append(state.variableName);
        }
        if (state.currentText.length() > 0) {
            fragments.add(new TemplateFragment(false, state.currentText.toString()));
        }
    }

    private static class ParserState {
        private final StringBuilder currentText = new StringBuilder();
        private final StringBuilder variableName = new StringBuilder();
        private boolean isEvaluable = false;
        private final BufferedReader reader;

        public ParserState(Reader reader) {
            if (reader instanceof BufferedReader) {
                this.reader = (BufferedReader) reader;
            } else {
                this.reader = new BufferedReader(reader);
            }
        }
    }
}