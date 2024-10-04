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
/**
 * 头信息
 *
 * @author limliu
 * @since 2.7
 */
public class Constants {

    /**
     * 订阅者 ID
     */
    public static final String ID = "id";

    /**
     * 接受版本
     */
    public static final String ACCEPT_VERSION = "accept-version";

    /**
     * 心跳
     */
    public static final String HEART_BEAT = "heart-beat";

    /**
     * 目的地
     */
    public static final String DESTINATION = "destination";

    /**
     * 内容类型
     */
    public static final String CONTENT_TYPE = "content-type";

    /**
     * 消息 ID
     */
    public static final String MESSAGE_ID = "message-id";

    /**
     * 凭据
     */
    public static final String RECEIPT = "receipt";

    /**
     * 凭据 ID
     */
    public static final String RECEIPT_ID = "receipt-id";

    /**
     * 订阅者
     */
    public static final String SUBSCRIPTION = "subscription";

    /**
     * 确认
     */
    public static final String ACK = "ack";

    /**
     * 确认
     */
    public static final String NACK = "nack";

    /**
     * 账号
     */
    public static final String LOGIN = "login";

    /**
     * 密码
     */
    public static final String PASSCODE = "passcode";

    /**
     * 鉴权-有权限
     */
    public static final String AUTHORIZED = "Authorized";

    /**
     * 鉴权-无权限
     */
    public static final String UNAUTHORIZED = "Unauthorized";

    /**
     *
     */
    public static final String SERVER = "server";

    /**
     *
     */
    public static final String VERSION = "version";

    /**
     * 是否开启持久化, boolean类型
     */
    public static final String OPEN_PERSISTENCE = "openPersistence";

    /**
     * 是否开启ack, boolean类型
     */
    public static final String OPEN_ACK = "openAck";

}