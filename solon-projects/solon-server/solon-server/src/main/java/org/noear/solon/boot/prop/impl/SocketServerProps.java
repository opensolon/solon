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
package org.noear.solon.boot.prop.impl;

import org.noear.solon.Solon;
import org.noear.solon.boot.ServerConstants;

/**
 * Socket 信号服务属性
 *
 * @author noear
 * @since 1.8
 */
public class SocketServerProps extends BaseServerProps {
    public static SocketServerProps getInstance() {
        return Solon.context().attachOf(SocketServerProps.class, () -> new SocketServerProps(20000));
    }

    public SocketServerProps(int portBase) {
        super(ServerConstants.SIGNAL_SOCKET, portBase);
    }
}