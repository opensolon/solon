/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.serialization.abc.sbe;

import org.noear.solon.serialization.abc.io.BytesFactory;
import org.noear.solon.serialization.abc.io.BytesSerializable;

/**
 * Sbe 可序列化定制接口
 *
 * @author noear
 * @since 3.0
 * */
public interface SbeSerializable extends BytesSerializable<SbeInputBuffers,SbeOutputBuffers> {
    @Override
    default BytesFactory<SbeInputBuffers, SbeOutputBuffers> serializeFactory(){
        return SbeFactory.getInstance();
    }
}
