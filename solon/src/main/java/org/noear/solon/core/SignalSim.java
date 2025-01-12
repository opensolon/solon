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
package org.noear.solon.core;

/**
 * 信号简单实现
 *
 * @author noear
 * @since 1.3
 */
public class SignalSim implements Signal {
    private String host;
    private int port;
    private String protocol;
    private SignalType type;
    private String name;

    /**
     * 信号名
     * */
    @Override
    public String name() {
        return name;
    }

    /**
     * 主机
     * */
    @Override
    public String host() {
        return host;
    }

    /**
     * 信号端口
     * */
    @Override
    public int port() {
        return port;
    }

    /**
     * 信号协议
     * */
    @Override
    public String protocol() {
        return protocol;
    }

    /**
     * 信号类型
     * */
    @Override
    public SignalType type() {
        return type;
    }

    public SignalSim(String name, String host, int port, String protocol, SignalType type) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.protocol = protocol.toLowerCase();
        this.type = type;
    }
}
