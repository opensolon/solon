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
package org.noear.solon.scheduling.retry;

import org.noear.solon.core.aspect.Invocation;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 被调用者
 *
 * @author noear
 * @since 2.4
 */
public class CalleeImpl implements Callee{
    private Invocation inv;

    public CalleeImpl(Invocation inv) {
        this.inv = inv;
    }

    /**
     * 被调目标
     * */
    public Object target() {
        return inv.target();
    }

    /**
     * 被调函数
     * */
    public Method method() {
        return inv.method().getMethod();
    }

    /**
     * 参数
     * */
    public Object args() {
        return inv.args();
    }

    /**
     * 参数 map 形式
     * */
    public Map<String, Object> argsAsMap() {
        return inv.argsAsMap();
    }

    /**
     * 调用
     * */
    public Object call() throws Throwable {
        return inv.method().getMethod().invoke(inv.target(), inv.args());
    }
}
