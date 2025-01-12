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
package org.noear.solon.net.stomp;

/**
 * 头信息
 *
 * @author limliu
 * @since 2.7
 * @since 3.0
 */
public interface Headers {

    /**
     * 订阅者 ID
     */
    String ID = "id";

    /**
     * 接受版本
     */
    String ACCEPT_VERSION = "accept-version";

    /**
     * 心跳
     */
    String HEART_BEAT = "heart-beat";

    /**
     * 目的地
     */
    String DESTINATION = "destination";

    /**
     * 内容类型
     */
    String CONTENT_TYPE = "content-type";

    /**
     * 内容长度
     */
    String CONTENT_LENGTH = "content-length";

    /**
     * 消息 ID
     */
    String MESSAGE_ID = "message-id";

    /**
     * 凭据
     */
    String RECEIPT = "receipt";

    /**
     * 凭据 ID
     */
    String RECEIPT_ID = "receipt-id";

    /**
     * 事务
     * */
    String TRANSACTION = "transaction";

    /**
     * 订阅者
     */
    String SUBSCRIPTION = "subscription";

    /**
     * 确认
     */
    String ACK = "ack";

    /**
     * 确认
     */
    String NACK = "nack";

    /**
     * 账号
     */
    String LOGIN = "login";

    /**
     * 密码
     */
    String PASSCODE = "passcode";

    /**
     * 鉴权-有权限
     */
    String AUTHORIZED = "Authorized";

    /**
     * 鉴权-无权限
     */
    String UNAUTHORIZED = "Unauthorized";

    /**
     *
     */
    String SERVER = "server";

    /**
     *
     */
    String VERSION = "version";

    /**
     * 是否开启持久化, boolean类型
     */
    String OPEN_PERSISTENCE = "openPersistence";

    /**
     * 是否开启ack, boolean类型
     */
    String OPEN_ACK = "openAck";

}