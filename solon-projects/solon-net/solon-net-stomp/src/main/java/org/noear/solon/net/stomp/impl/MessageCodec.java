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
package org.noear.solon.net.stomp.impl;


import org.noear.solon.net.stomp.Message;

import java.util.function.Consumer;


/**
 * 消息编解码器
 *
 * @author limliu
 * @since 2.7
 */
public interface MessageCodec {

    /**
     * 编码
     *
     * @param input Stomp 消息
     * @return 编码后的文本
     */
    String encode(Message input);

    /**
     * 解码
     *
     * @param input 输入
     * @param out   输出
     */
    void decode(String input, Consumer<Message> out);

}