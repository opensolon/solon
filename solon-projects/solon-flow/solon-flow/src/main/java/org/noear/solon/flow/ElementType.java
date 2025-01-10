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
package org.noear.solon.flow;

/**
 * 元系类型：0开始，1线，2执行节点，3排他网关，4并行网关，5汇聚网关，9结束
 *
 * @author noear
 * @since 3.0
 * */
public class ElementType {
    /**
     * 开始
     */
    public static final int start = 0;
    /**
     * 线
     */
    public static final int line = 1;
    /**
     * 执行节点
     */
    public static final int execute = 2;
    /**
     * 排他网关
     */
    public static final int exclusive = 3;
    /**
     * 并行网关
     */
    public static final int parallel = 4;
    /**
     * 汇聚网关
     */
    public static final int converge = 5;
    /**
     * 结束
     */
    public static final int stop = 9;
}
