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
import org.noear.solon.core.util.SupplierEx;
import org.noear.solon.lang.Preview;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Markdown 文档加载器
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public class MarkdownLoader extends AbstractOptionsDocumentLoader<MarkdownLoader.Options, MarkdownLoader> {
    private final SupplierEx<InputStream> source;
    private final Parser parser;

    public MarkdownLoader(byte[] source) {
        this(() -> new ByteArrayInputStream(source));
    }

    public MarkdownLoader(File file) {
        this(() -> new FileInputStream(file));
    }

    public MarkdownLoader(URL source) {
        this(() -> source.openStream());
    }

    public MarkdownLoader(SupplierEx<InputStream> source) {
        if (source == null) {
            throw new IllegalArgumentException("Source cannot be null");
        }

        this.source = source;
        this.parser = Parser.builder().build();
        this.options = new Options();
    }

    @Override
    public List<Document> load() throws IOException {
        try (InputStream input = this.source.get()) {
            Node md = this.parser.parseReader(new InputStreamReader(input));
            SplitVisitor splitVisitor = new SplitVisitor(this);
            md.accept(splitVisitor);
            return splitVisitor.extract();
        } catch (IOException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
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
        private final MarkdownLoader loader;
        private Document currentDocument;

        SplitVisitor(MarkdownLoader loader) {
            this.loader = loader;
        }

        @Override
        public void visit(org.commonmark.node.Document document) {
            this.currentDocument = new Document();
            super.visit(document);
        }

        @Override
        public void visit(Heading heading) {
            this.doneAndNew();
            super.visit(heading);
        }

        @Override
        public void visit(ThematicBreak thematicBreak) {
            if (loader.options.horizontalLineAsNew) {
                this.doneAndNew();
            }

            super.visit(thematicBreak);
        }

        @Override
        public void visit(SoftLineBreak softLineBreak) {
            this.translateLineBreakToSpace();
            super.visit(softLineBreak);
        }

        @Override
        public void visit(HardLineBreak hardLineBreak) {
            this.translateLineBreakToSpace();
            super.visit(hardLineBreak);
        }

        @Override
        public void visit(ListItem listItem) {
            this.translateLineBreakToSpace();
            super.visit(listItem);
        }

        @Override
        public void visit(BlockQuote blockQuote) {
            if (loader.options.blockquoteAsNew) {
                this.doneAndNew();
            }

            this.translateLineBreakToSpace();
            this.currentDocument.metadata("category", "blockquote");
            super.visit(blockQuote);
        }

        @Override
        public void visit(Code code) {
            this.currentParagraphs.add(code.getLiteral());
            this.currentDocument.metadata("category", "code_inline");
            super.visit(code);
        }

        @Override
        public void visit(FencedCodeBlock fencedCodeBlock) {
            if (loader.options.codeBlockAsNew) {
                this.doneAndNew();
            }

            this.translateLineBreakToSpace();
            this.currentParagraphs.add(fencedCodeBlock.getLiteral());
            this.currentDocument.metadata("category", "code_block");
            this.currentDocument.metadata("lang", fencedCodeBlock.getInfo());
            this.doneAndNew();
            super.visit(fencedCodeBlock);
        }

        @Override
        public void visit(Text text) {
            Node tmp = text.getParent();
            if (tmp instanceof Heading) {
                Heading heading = (Heading) tmp;
                this.currentDocument.metadata("category", String.format("header_%d", (heading.getLevel())));
                this.currentDocument.metadata("title", text.getLiteral());
            } else {
                this.currentParagraphs.add(text.getLiteral());
            }

            super.visit(text);
        }

        /**
         * 提取文档
         */
        public List<Document> extract() {
            this.doneAndNew();
            return this.documents;
        }

        /**
         * 结束并重新开始
         */
        private void doneAndNew() {
            if (!this.currentParagraphs.isEmpty()) {
                String content = String.join("", this.currentParagraphs);
                this.currentDocument.content(content);
                this.currentDocument.metadata(loader.additionalMetadata);
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
     * 加载选项
     */
    public static class Options {
        /**
         * 有水平线新建
         */
        private boolean horizontalLineAsNew;
        /**
         * 有代码块新建
         */
        private boolean codeBlockAsNew;
        /**
         * 有块引用新建
         */
        private boolean blockquoteAsNew;

        public Options horizontalLineAsNew(boolean horizontalLineAsNew) {
            this.horizontalLineAsNew = horizontalLineAsNew;
            return this;
        }

        public Options codeBlockAsNew(boolean codeBlockAsNew) {
            this.codeBlockAsNew = codeBlockAsNew;
            return this;
        }

        public Options blockquoteAsNew(boolean blockquoteAsNew) {
            this.blockquoteAsNew = blockquoteAsNew;
            return this;
        }
    }
}