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

import org.commonmark.parser.Parser;
import org.commonmark.node.*;
import org.noear.solon.Utils;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.DocumentLoader;
import org.noear.solon.core.util.SupplierEx;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author noear
 * @since 3.1
 */
public class MarkdownLoader implements DocumentLoader {
    private final SupplierEx<InputStream> resource;
    private final Options options;
    private final Parser parser;

    public MarkdownLoader(SupplierEx<InputStream> resource) {
        this(resource, Options.getDefault());
    }

    public MarkdownLoader(SupplierEx<InputStream> resource, Options options) {
        this.resource = resource;
        this.options = options;
        this.parser = Parser.builder().build();
    }

    @Override
    public List<Document> load() {
        try (InputStream input = this.resource.get()) {
            Node md = this.parser.parseReader(new InputStreamReader(input));
            SplitVisitor splitVisitor = new SplitVisitor(this.options);
            md.accept(splitVisitor);
            return splitVisitor.getDocuments();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 分割观察器
     */
    static class SplitVisitor extends AbstractVisitor {
        private final List<Document> documents = new ArrayList();
        private final List<String> currentParagraphs = new ArrayList();
        private final Options config;
        private Document currentDocument;

        SplitVisitor(Options config) {
            this.config = config;
        }

        public void visit(org.commonmark.node.Document document) {
            this.currentDocument = new Document();
            super.visit(document);
        }

        public void visit(Heading heading) {
            this.buildAndFlush();
            super.visit(heading);
        }

        public void visit(ThematicBreak thematicBreak) {
            if (this.config.horizontalLineAsNew) {
                this.buildAndFlush();
            }

            super.visit(thematicBreak);
        }

        public void visit(SoftLineBreak softLineBreak) {
            this.translateLineBreakToSpace();
            super.visit(softLineBreak);
        }

        public void visit(HardLineBreak hardLineBreak) {
            this.translateLineBreakToSpace();
            super.visit(hardLineBreak);
        }

        public void visit(ListItem listItem) {
            this.translateLineBreakToSpace();
            super.visit(listItem);
        }

        public void visit(BlockQuote blockQuote) {
            if (!this.config.blockquoteAsNew) {
                this.buildAndFlush();
            }

            this.translateLineBreakToSpace();
            this.currentDocument.addMetadata("category", "blockquote");
            super.visit(blockQuote);
        }

        public void visit(Code code) {
            this.currentParagraphs.add(code.getLiteral());
            this.currentDocument.addMetadata("category", "code_inline");
            super.visit(code);
        }

        public void visit(FencedCodeBlock fencedCodeBlock) {
            if (!this.config.codeBlockAsNew) {
                this.buildAndFlush();
            }

            this.translateLineBreakToSpace();
            this.currentParagraphs.add(fencedCodeBlock.getLiteral());
            this.currentDocument.addMetadata("category", "code_block");
            this.currentDocument.addMetadata("lang", fencedCodeBlock.getInfo());
            this.buildAndFlush();
            super.visit(fencedCodeBlock);
        }

        public void visit(Text text) {
            Node var3 = text.getParent();
            if (var3 instanceof Heading) {
                Heading heading = (Heading) var3;
                this.currentDocument.addMetadata("category", String.format("header_%d", (heading.getLevel())));
                this.currentDocument.addMetadata("title", text.getLiteral());
            } else {
                this.currentParagraphs.add(text.getLiteral());
            }

            super.visit(text);
        }

        public List<Document> getDocuments() {
            this.buildAndFlush();
            return this.documents;
        }

        private void buildAndFlush() {
            if (!this.currentParagraphs.isEmpty()) {
                String content = String.join("", this.currentParagraphs);
                this.currentDocument.setContent(content);
                this.config.additionalMetadata.forEach((k, v) -> currentDocument.addMetadata(k, v));
                this.documents.add(currentDocument);
                this.currentParagraphs.clear();
            }

            this.currentDocument = new Document();
        }

        private void translateLineBreakToSpace() {
            if (Utils.isNotEmpty(this.currentParagraphs)) {
                this.currentParagraphs.add(" ");
            }
        }
    }


    /// /////////////////////


    /**
     * 选项
     */
    public static class Options {
        private static Options DEFAULT = new Options();

        public static Options getDefault() {
            return DEFAULT;
        }

        /**
         * 有水平线新建
         */
        public boolean horizontalLineAsNew;
        /**
         * 有代码块新建
         */
        public boolean codeBlockAsNew;
        /**
         * 有块引用新建
         */
        public boolean blockquoteAsNew;
        /**
         * 增量元数据
         */
        public Map<String, Object> additionalMetadata = new HashMap<>();

        public Options includeHorizontalLine(boolean includeHorizontalLine) {
            this.horizontalLineAsNew = includeHorizontalLine;
            return this;
        }

        public Options includeCodeBlock(boolean includeCodeBlock) {
            this.codeBlockAsNew = includeCodeBlock;
            return this;
        }

        public Options includeBlockquote(boolean includeBlockquote) {
            this.blockquoteAsNew = includeBlockquote;
            return this;
        }

        public Options additionalMetadata(String key, Object value) {
            this.additionalMetadata.put(key, value);
            return this;
        }
    }
}