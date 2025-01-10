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
package org.noear.solon.flow.core;

/**
 * 元系类型：0开始，1线，2执行节点，3排他网关，4并行网关，5汇聚网关，9结束
 *
 * @author noear
 * @since 3.0
 * */
public enum ElementType {
    /**
     * 开始
     */
    start(0), // 0;
    /**
     * 线
     */
    line(1),// = 1;
    /**
     * 执行节点
     */
    execute(2),// = 2;
    /**
     * 排他网关
     */
    exclusive(3),// = 3;
    /**
     * 并行网关
     */
    parallel(4),// = 4;
    /**
     * 汇聚网关
     */
    converge(5),// = 5;
    /**
     * 结束
     */
    stop(9);// = 9;

    private int code;

    public int getCode() {
        return code;
    }

    ElementType(int code) {
        this.code = code;
    }

    public static ElementType valueOf(int code) {
        ElementType[] values = ElementType.values();
        for (ElementType v : values) {
            if (v.code == code) {
                return v;
            }
        }

        return start;
    }
}
