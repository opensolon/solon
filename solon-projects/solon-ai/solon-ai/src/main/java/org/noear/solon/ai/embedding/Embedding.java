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
package org.noear.solon.ai.embedding;

import java.util.Arrays;

/**
 * 嵌入数据
 *
 * @author noear
 * @since 3.1
 */
public class Embedding {
    private int index;
    private float[] embedding;

    public Embedding() {
        //用于反序列化
    }

    public Embedding(int index, float[] embedding) {
        this.embedding = embedding;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public float[] getEmbedding() {
        return embedding;
    }

    @Override
    public String toString() {
        return "{" +
                "index=" + index +
                ", embedding=" + Arrays.toString(embedding) +
                '}';
    }
}
