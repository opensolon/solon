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
package org.noear.solon.net.stomp.broker;

import org.noear.solon.net.stomp.Frame;

import java.util.function.Consumer;

/**
 * Stomp 帧编解码器
 *
 * @author limliu
 * @since 2.7
 */
public interface FrameCodec {

    /**
     * 编码
     *
     * @param frame 帧
     * @return 被编码的文本
     */
    String encode(Frame frame);

    /**
     * 解码
     *
     * @param input 被编码的文本
     * @param out   输出
     */
    void decode(String input, Consumer<Frame> out);
}