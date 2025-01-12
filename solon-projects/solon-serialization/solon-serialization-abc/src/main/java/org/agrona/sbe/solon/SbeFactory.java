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
package org.agrona.sbe.solon;

import org.agrona.ExpandableDirectByteBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.noear.solon.serialization.abc.io.AbcFactory;

/**
 * Sbe 序列化工厂
 *
 * @author noear
 * @since 3.0
 * */
public class SbeFactory implements AbcFactory<SbeInput, SbeOutput> {
    public static final SbeFactory instance = new SbeFactory();

    public static AbcFactory<SbeInput, SbeOutput> getInstance() {
        return instance;
    }

    @Override
    public SbeInput createInput(byte[] bytes) {
        MutableDirectBuffer buffer = new UnsafeBuffer(bytes);
        return new SbeInput().wrap(buffer);
    }

    @Override
    public SbeOutput createOutput() {
        ExpandableDirectByteBuffer buffer = new ExpandableDirectByteBuffer(128);
        return new SbeOutput(buffer);
    }

    @Override
    public byte[] extractBytes(SbeOutput out) {
        return out.toBytes();
    }
}