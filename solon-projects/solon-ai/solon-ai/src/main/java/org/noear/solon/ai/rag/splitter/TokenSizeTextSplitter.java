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
package org.noear.solon.ai.rag.splitter;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;
import com.knuddels.jtokkit.api.IntArrayList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 词元大小文本分割器
 *
 * @author noear
 * @since 3.1
 */
public class TokenSizeTextSplitter extends TextSplitter {
    private EncodingRegistry encodingRegistry;
    private EncodingType encodingType;
    private final int chunkSize;
    private final int minChunkSizeChars;
    private final int minChunkLengthToEmbed;
    private final int maxChunkCount;
    private final boolean keepSeparator;

    public TokenSizeTextSplitter() {
        this(500);
    }

    public TokenSizeTextSplitter(int chunkSize) {
        this(chunkSize, 300);
    }

    public TokenSizeTextSplitter(int chunkSize, int minChunkSizeChars) {
        this(chunkSize, minChunkSizeChars, 5, 1000, true);
    }

    public TokenSizeTextSplitter(int chunkSize, int minChunkSizeChars, int minChunkLengthToEmbed, int maxChunkCount, boolean keepSeparator) {
        this.encodingRegistry = Encodings.newLazyEncodingRegistry();
        this.encodingType = EncodingType.CL100K_BASE;

        this.chunkSize = chunkSize;
        this.minChunkSizeChars = minChunkSizeChars;
        this.minChunkLengthToEmbed = minChunkLengthToEmbed;
        this.maxChunkCount = maxChunkCount;
        this.keepSeparator = keepSeparator;
    }

    /**
     * 设置编码库
     */
    public void setEncodingRegistry(EncodingRegistry encodingRegistry) {
        if (encodingRegistry != null) {
            this.encodingRegistry = encodingRegistry;
        }
    }

    /**
     * 设置编码类型
     */
    public void setEncodingType(EncodingType encodingType) {
        if (encodingType != null) {
            this.encodingType = encodingType;
        }
    }

    @Override
    protected List<String> splitText(String text) {
        Encoding encoding = encodingRegistry.getEncoding(encodingType);
        List<String> chunks = new ArrayList();

        if (text != null && !text.trim().isEmpty()) {
            List<Integer> tokens = this.encodeTokens(encoding, text);
            int chunksCount = 0;

            while (!tokens.isEmpty() && chunksCount < this.maxChunkCount) {
                List<Integer> chunk = tokens.subList(0, Math.min(chunkSize, tokens.size()));
                String chunkText = this.decodeTokens(encoding, chunk);
                if (chunkText.trim().isEmpty()) {
                    tokens = tokens.subList(chunk.size(), tokens.size());
                } else {
                    int lastPunctuation = Math.max(chunkText.lastIndexOf(46),
                            Math.max(chunkText.lastIndexOf(63),
                                    Math.max(chunkText.lastIndexOf(33),
                                            chunkText.lastIndexOf(10))));

                    if (lastPunctuation > 0 && lastPunctuation > this.minChunkSizeChars) {
                        chunkText = chunkText.substring(0, lastPunctuation + 1);
                    }

                    String chunkTextToAppend = this.keepSeparator ? chunkText.trim() : chunkText.replace(System.lineSeparator(), " ").trim();
                    if (chunkTextToAppend.length() > this.minChunkLengthToEmbed) {
                        chunks.add(chunkTextToAppend);
                    }

                    tokens = tokens.subList(this.encodeTokens(encoding, chunkText).size(), tokens.size());
                    ++chunksCount;
                }
            }

            if (!tokens.isEmpty()) {
                String remaining_text = this.decodeTokens(encoding, tokens).replace(System.lineSeparator(), " ").trim();
                if (remaining_text.length() > this.minChunkLengthToEmbed) {
                    chunks.add(remaining_text);
                }
            }
        }

        return chunks;
    }

    /**
     * 编码符号
     */
    protected List<Integer> encodeTokens(Encoding encoding, String text) {
        Objects.requireNonNull(text, "tokens is null");

        return encoding.encode(text).boxed();
    }

    /**
     * 解码符号
     */
    protected String decodeTokens(Encoding encoding, List<Integer> tokens) {
        Objects.requireNonNull(tokens, "tokens is null");

        IntArrayList tmp = new IntArrayList(tokens.size());
        tokens.forEach(tmp::add);
        return encoding.decode(tmp);
    }
}