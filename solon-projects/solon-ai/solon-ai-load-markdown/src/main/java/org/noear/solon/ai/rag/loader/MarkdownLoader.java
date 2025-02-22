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
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.DocumentLoader;
import org.noear.solon.core.util.SupplierEx;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author noear
 * @since 3.1
 */
public class MarkdownLoader implements DocumentLoader {
    private final SupplierEx<InputStream> resource;
    private final Config config;
    private final Parser parser;

    public MarkdownLoader(SupplierEx<InputStream> resource) {
        this(resource, Config.getDefault());
    }

    public MarkdownLoader(SupplierEx<InputStream> resource, Config config) {
        this.resource = resource;
        this.config = config;
        this.parser = Parser.builder().build();
    }

    @Override
    public List<Document> load() {
        try (InputStream input = this.resource.get()) {
            Node node = this.parser.parseReader(new InputStreamReader(input));
            DocumentVisitor documentVisitor = new DocumentVisitor(this.config);
            node.accept(documentVisitor);
            return documentVisitor.getDocuments();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    static class DocumentVisitor extends AbstractVisitor {
        private final List<Document> documents = new ArrayList();
        private final List<String> currentParagraphs = new ArrayList();
        private final Config config;
        private Document currentDocument;

        DocumentVisitor(Config config) {
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
            if (this.config.horizontalRuleCreateDocument) {
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
            if (!this.config.includeBlockquote) {
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
            if (!this.config.includeCodeBlock) {
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
            if (!this.currentParagraphs.isEmpty()) {
                this.currentParagraphs.add(" ");
            }
        }
    }


    /// /////////////////////


    public static class Config {
        private static Config DEFAULT = new Config();

        public static Config getDefault() {
            return DEFAULT;
        }

        public boolean horizontalRuleCreateDocument;
        public boolean includeCodeBlock;
        public boolean includeBlockquote;
        public Map<String, Object> additionalMetadata = new HashMap<>();

        public Config horizontalRuleCreateDocument(boolean horizontalRuleCreateDocument) {
            this.horizontalRuleCreateDocument = horizontalRuleCreateDocument;
            return this;
        }

        public Config includeCodeBlock(boolean includeCodeBlock) {
            this.includeCodeBlock = includeCodeBlock;
            return this;
        }

        public Config includeBlockquote(boolean includeBlockquote) {
            this.includeBlockquote = includeBlockquote;
            return this;
        }

        public Config additionalMetadata(String key, Object value) {
            this.additionalMetadata.put(key, value);
            return this;
        }
    }
}